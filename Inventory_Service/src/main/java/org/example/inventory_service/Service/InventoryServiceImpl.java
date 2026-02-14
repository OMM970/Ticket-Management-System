package org.example.inventory_service.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.inventory_service.Dto.RequestDto;
import org.example.inventory_service.Dto.ResponseDto;
import org.example.inventory_service.Dto.SeatsActionDto;
import org.example.inventory_service.Entity.InventoryEntity;
import org.example.inventory_service.Entity.SeatEntity;
import org.example.inventory_service.Enums.SeatStatus;
import org.example.inventory_service.Enums.SeatsType;
import org.example.inventory_service.Repository.InventoryRepo;
import org.example.inventory_service.Repository.SeatsRepo;
import org.example.inventory_service.Util.InventorySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepo inventoryRepo;
    private final SeatsRepo seatsRepo;
    private final RedisTemplate<String, String> redisTemplate;
    private final SeatGenerationService seatGenerationService;



    @Override
    public ResponseDto addToinventory(RequestDto requestDto) {

        InventoryEntity entity = InventoryEntity.builder()
                .flightNumber(requestDto.getFlightNumber())
                .source(requestDto.getSource())
                .destination(requestDto.getDestination())
                .flightDate(requestDto.getFlightDate())


                .economySeats(requestDto.getEconomySeats())
                .businessSeats(requestDto.getBusinessSeats())


                .economyAvailable(requestDto.getEconomySeats())
                .businessAvailable(requestDto.getBusinessSeats())


                .economyBooked(0)
                .businessBooked(0)

                .economyPrice(requestDto.getEconomyPrice())
                .businessPrice(requestDto.getBusinessPrice())
                .build();
        InventoryEntity savedFlight = inventoryRepo.save(entity);
        createSeatsForFlight(savedFlight);
        return maptoinventoryResponseDto(savedFlight);
    }


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
                .businessAvailable(entity.getBusinessAvailable())
                .economyAvailable(entity.getEconomyAvailable())
                .economyPrice(entity.getEconomyPrice())
                .businessPrice(entity.getBusinessPrice())
                .businessBooked(entity.getBusinessBooked())
                .economyBooked(entity.getEconomyBooked())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }



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



    @Override
    public void bulkImport(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            List<InventoryEntity> batch = new ArrayList<>(100);

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


                if (batch.size() == 100) {

                    List<InventoryEntity> savedFlights = inventoryRepo.saveAll(batch);

                    System.out.println("Saved batch of " + savedFlights.size() + " flights");


                    for (InventoryEntity flight : savedFlights) {
                        seatGenerationService.generateSeatsForFlight(flight);
                    }

                    batch.clear();
                }
            }

            // Save remaining
            if (!batch.isEmpty()) {

                List<InventoryEntity> savedFlights = inventoryRepo.saveAll(batch);

                System.out.println("Saved final batch of " + savedFlights.size() + " flights");

                for (InventoryEntity flight : savedFlights) {
                    seatGenerationService.generateSeatsForFlight(flight);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Excel import failed", e);
        }
    }




    @Override
    public ResponseDto findbyflightbyid(Integer id) {
        InventoryEntity entity = inventoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return maptoinventoryResponseDto(entity);
    }
    @Transactional
    @Override
    public void holdSeats(SeatsActionDto dto) {

        Integer flightId = dto.getFlightId();
        List<String> seatNumbers = new ArrayList<>(dto.getSeatNumbers());
        String bookingId = dto.getHoldBy();

        LocalDateTime now = LocalDateTime.now();

        //  Prevent deadlock
        Collections.sort(seatNumbers);

        List<String> redisKeys = new ArrayList<>();

        try {

            // Acquire short Redis locks
            for (String seatNumber : seatNumbers) {

                String key = "LOCK:flight:" + flightId + ":" + seatNumber;

                Boolean locked = redisTemplate.opsForValue()
                        .setIfAbsent(key, bookingId, Duration.ofSeconds(5));

                if (Boolean.FALSE.equals(locked)) {
                    throw new RuntimeException("Seat " + seatNumber + " is being processed");
                }

                redisKeys.add(key);
            }

            //  DB Pessimistic Lock
            List<SeatEntity> seats =
                    seatsRepo.findByFlightIdAndSeatNumberIn(flightId, seatNumbers);

            if (seats.size() != seatNumbers.size()) {
                throw new RuntimeException("Some seats not found");
            }

            // Auto-release expired HOLD
            for (SeatEntity seat : seats) {

                if (seat.getStatus() == SeatStatus.HOLD &&
                        seat.getHoldUntil() != null &&
                        seat.getHoldUntil().isBefore(now)) {

                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.setHoldBy(null);
                    seat.setHoldUntil(null);
                }
            }

            //  Validate AVAILABLE
            for (SeatEntity seat : seats) {
                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new RuntimeException(
                            "Seat " + seat.getSeatNumber() + " not available");
                }
            }

            //  Mark HOLD
            for (SeatEntity seat : seats) {
                seat.setStatus(SeatStatus.HOLD);
                seat.setHoldBy(bookingId);
                seat.setHoldUntil(now.plusMinutes(15));
            }

            seatsRepo.saveAll(seats);

        } finally {
            for (String key : redisKeys) {
                redisTemplate.delete(key);
            }
        }
    }


    // ================= CONFIRM SEATS =================

    @Transactional
    @Override
    public void confirmSeats(SeatsActionDto dto) {

        Integer flightId = dto.getFlightId();
        List<String> seatNumbers = new ArrayList<>(dto.getSeatNumbers());
        String bookingId = dto.getHoldBy();

        LocalDateTime now = LocalDateTime.now();

        // 🔥 Prevent deadlock
        Collections.sort(seatNumbers);

        // 1️⃣ DB Pessimistic Lock
        List<SeatEntity> seats =
                seatsRepo.findByFlightIdAndSeatNumberIn(flightId, seatNumbers);

        if (seats.size() != seatNumbers.size()) {
            throw new RuntimeException("Some seats not found");
        }

        // 2️⃣ Validate seats belong to booking and not expired
        for (SeatEntity seat : seats) {

            if (seat.getStatus() != SeatStatus.HOLD) {
                throw new RuntimeException(
                        "Seat " + seat.getSeatNumber() + " is not on HOLD");
            }

            if (!bookingId.equals(seat.getHoldBy())) {
                throw new RuntimeException(
                        "Seat " + seat.getSeatNumber() + " does not belong to booking");
            }

            if (seat.getHoldUntil() == null || seat.getHoldUntil().isBefore(now)) {
                throw new RuntimeException(
                        "Seat " + seat.getSeatNumber() + " hold expired");
            }
        }

        // 3️⃣ Mark BOOKED
        for (SeatEntity seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setHoldBy(null);
            seat.setHoldUntil(null);
        }

        seatsRepo.saveAll(seats);
    }

    // ================= RELEASE SEATS =================

    @Transactional
    @Override
    public void releaseSeats(SeatsActionDto dto) {

        Integer flightId = dto.getFlightId();
        List<String> seatNumbers = new ArrayList<>(dto.getSeatNumbers());
        String bookingId = dto.getHoldBy();

        Collections.sort(seatNumbers);

        // DB pessimistic lock
        List<SeatEntity> seats =
                seatsRepo.findByFlightIdAndSeatNumberIn(flightId, seatNumbers);

        if (seats.size() != seatNumbers.size()) {
            throw new RuntimeException("Some seats not found");
        }

        for (SeatEntity seat : seats) {

            // Only release if this booking owns the hold
            if (seat.getStatus() == SeatStatus.HOLD &&
                    bookingId.equals(seat.getHoldBy())) {

                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setHoldBy(null);
                seat.setHoldUntil(null);
            }
        }

        seatsRepo.saveAll(seats);
    }
    private void createSeatsForFlight(InventoryEntity flight) {

        List<SeatEntity> seats = new ArrayList<>();

        // Economy Seats
        for (int i = 1; i <= flight.getEconomySeats(); i++) {

            SeatEntity seat = SeatEntity.builder()
                    .seatNumber("E" + i)
                    .seatType(SeatsType.ECONOMY)
                    .status(SeatStatus.AVAILABLE)
                    .holdBy(null)
                    .holdUntil(null)
                    .flight(flight)
                    .build();

            seats.add(seat);
        }

        // Business Seats
        for (int i = 1; i <= flight.getBusinessSeats(); i++) {

            SeatEntity seat = SeatEntity.builder()
                    .seatNumber("B" + i)
                    .seatType(SeatsType.BUSINESS)
                    .status(SeatStatus.AVAILABLE)
                    .holdBy(null)
                    .holdUntil(null)
                    .flight(flight)
                    .build();

            seats.add(seat);
        }

        seatsRepo.saveAll(seats);
    }

}
