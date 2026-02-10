package org.example.inventory_service.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.inventory_service.Dto.RequestDto;
import org.example.inventory_service.Dto.ResponseDto;
import org.example.inventory_service.Entity.InventoryEntity;
import org.example.inventory_service.Repository.InventoryRepo;
import org.example.inventory_service.Util.InventorySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepo inventoryRepo;
    private final RedisTemplate<String, String> redisTemplate;

    // ================= CREATE INVENTORY =================

    @Override
    public ResponseDto addToinventory(RequestDto requestDto) {

        InventoryEntity entity = InventoryEntity.builder()
                .flightNumber(requestDto.getFlightNumber())
                .source(requestDto.getSource())
                .destination(requestDto.getDestination())
                .flightDate(requestDto.getFlightDate())

                // TOTAL
                .economySeats(requestDto.getEconomySeats())
                .businessSeats(requestDto.getBusinessSeats())

                // AVAILABLE = TOTAL (INITIAL)
                .economyAvailable(requestDto.getEconomySeats())
                .businessAvailable(requestDto.getBusinessSeats())

                // BOOKED = 0
                .economyBooked(0)
                .businessBooked(0)

                .economyPrice(requestDto.getEconomyPrice())
                .businessPrice(requestDto.getBusinessPrice())
                .build();

        inventoryRepo.save(entity);
        return maptoinventoryResponseDto(entity);
    }

    // ================= RESPONSE MAPPER =================

    @Override
    public ResponseDto maptoinventoryResponseDto(InventoryEntity entity) {
        return ResponseDto.builder()
                .id(entity.getId())
                .flightNumber(entity.getFlightNumber())
                .source(entity.getSource())
                .destination(entity.getDestination())
                .flightDate(entity.getFlightDate())
                .economySeats(entity.getEconomySeats())
                .businessSeats(entity.getBusinessSeats())
                .economyPrice(entity.getEconomyPrice())
                .businessPrice(entity.getBusinessPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ================= SEARCH =================

    @Override
    public Page<ResponseDto> getAllInventory(
            String source,
            String destination,
            Date fromDate,
            Date toDate,
            Pageable pageable) {

        Specification<InventoryEntity> spec =
                InventorySpecification.filter(source, destination, fromDate, toDate);

        return inventoryRepo.findAll(spec, pageable)
                .map(this::maptoinventoryResponseDto);
    }

    // ================= BULK IMPORT =================

    @Transactional
    @Override
    public void bulkImport(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            List<InventoryEntity> batch = new ArrayList<>(500);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                int ecoTotal = (int) row.getCell(4).getNumericCellValue();
                int busTotal = (int) row.getCell(5).getNumericCellValue();

                InventoryEntity entity = InventoryEntity.builder()
                        .flightNumber(row.getCell(0).getStringCellValue())
                        .source(row.getCell(1).getStringCellValue())
                        .destination(row.getCell(2).getStringCellValue())
                        .flightDate(row.getCell(3).getLocalDateTimeCellValue().toLocalDate())

                        .economySeats(ecoTotal)
                        .businessSeats(busTotal)


                        .economyAvailable(ecoTotal)
                        .businessAvailable(busTotal)


                        .economyBooked(0)
                        .businessBooked(0)

                        .economyPrice(BigDecimal.valueOf(row.getCell(6).getNumericCellValue()))
                        .businessPrice(BigDecimal.valueOf(row.getCell(7).getNumericCellValue()))
                        .build();

                batch.add(entity);

                if (batch.size() == 500) {
                    inventoryRepo.saveAll(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                inventoryRepo.saveAll(batch);
            }

        } catch (Exception e) {
            throw new RuntimeException("Excel import failed", e);
        }
    }

    // ================= FIND =================

    @Override
    public ResponseDto findbyflightbyid(Integer id) {
        InventoryEntity entity = inventoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return maptoinventoryResponseDto(entity);
    }

    // ================= HOLD SEATS =================

    @Transactional
    @Override
    public void holdSeats(String flightId, String seatType, int seats, String idempotencyKey) {

        InventoryEntity inv = inventoryRepo.findById(Integer.valueOf(flightId))
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        switch (seatType.toUpperCase()) {

            case "ECONOMY" -> {
                if (inv.getEconomyAvailable() < seats) {
                    throw new RuntimeException("Not enough economy seats");
                }
                inv.setEconomyAvailable(inv.getEconomyAvailable() - seats);
            }

            case "BUSINESS" -> {
                if (inv.getBusinessAvailable() < seats) {
                    throw new RuntimeException("Not enough business seats");
                }
                inv.setBusinessAvailable(inv.getBusinessAvailable() - seats);
            }

            default -> throw new IllegalArgumentException("Invalid seatType");
        }
        if (inv.getEconomyAvailable() < 0 || inv.getBusinessAvailable() < 0) {
            throw new IllegalStateException("Seat invariant violated");
        }

        inventoryRepo.save(inv);

        String holdKey = "HOLD:" + flightId + ":" + idempotencyKey;
        String holdData = redisTemplate.opsForValue().get(holdKey);

        redisTemplate.opsForValue()
                .set(holdKey, seatType + ":" + seats, Duration.ofMinutes(15));
    }


    @Transactional
    @Override
    public void confirmSeats(String flightId, String seatType, int seats, String idempotencyKey) {

        InventoryEntity inv = inventoryRepo.findById(Integer.valueOf(flightId))
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        switch (seatType.toUpperCase()) {

            case "ECONOMY" -> inv.setEconomyBooked(inv.getEconomyBooked() + seats);
            case "BUSINESS" -> inv.setBusinessBooked(inv.getBusinessBooked() + seats);
            default -> throw new IllegalArgumentException("Invalid seatType");
        }

        inventoryRepo.save(inv);
        redisTemplate.delete("HOLD:" + flightId + ":" + idempotencyKey);
        String holdKey = "HOLD:" + flightId + ":" + idempotencyKey;

        String holdData = redisTemplate.opsForValue().get(holdKey);
        if (holdData == null) {
            return;
        }
    }

    @Transactional
    @Override
    public void releaseSeats(String flightId, String seatType, int seats, String idempotencyKey) {

        InventoryEntity inv = inventoryRepo.findById(Integer.valueOf(flightId))
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        switch (seatType.toUpperCase()) {

            case "ECONOMY" ->
                    inv.setEconomyAvailable(inv.getEconomyAvailable() + seats);

            case "BUSINESS" ->
                    inv.setBusinessAvailable(inv.getBusinessAvailable() + seats);

            default -> throw new IllegalArgumentException("Invalid seatType");
        }

        inventoryRepo.save(inv);
        redisTemplate.delete("HOLD:" + flightId + ":" + idempotencyKey);
        String holdKey = "HOLD:" + flightId + ":" + idempotencyKey;

        String holdData = redisTemplate.opsForValue().get(holdKey);
        if (holdData == null) {
            return;
        }
    }
}
