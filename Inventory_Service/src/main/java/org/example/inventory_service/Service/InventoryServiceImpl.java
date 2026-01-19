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
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepo inventoryRepo;

    @Override
    public ResponseDto addToinventory(RequestDto RequestDto) {
        InventoryEntity entity = InventoryEntity.builder()
                .businessPrice(RequestDto.getBusinessPrice())
                .destination(RequestDto.getDestination())
                .economyPrice(RequestDto.getEconomyPrice())
                .economySeats(RequestDto.getEconomySeats())
                .businessSeats(RequestDto.getBusinessSeats())
                .source(RequestDto.getSource())
                .flightDate(RequestDto.getFlightDate())
                .flightNumber(RequestDto.getFlightNumber())
                .build();
        entity = inventoryRepo.save(entity);
        return maptoinventoryResponseDto(entity);



    }
    @Override
    public ResponseDto maptoinventoryResponseDto(InventoryEntity entity){
        return  ResponseDto.builder()
                .id(entity.getId())
                .flightNumber(entity.getFlightNumber())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .businessPrice(entity.getBusinessPrice())
                .destination(entity.getDestination())
                .economyPrice(entity.getEconomyPrice())
                .economySeats(entity.getEconomySeats())
                .businessSeats(entity.getBusinessSeats())
                .source(entity.getSource())
                .flightDate(entity.getFlightDate())
                .build();


    }

    @Override
    public Page<ResponseDto> getAllInventory(
            String source,
            String destination,
            Date fromDate,
            Date toDate,
            Pageable pageable) {
        Specification<InventoryEntity> spec=
                InventorySpecification.filter(
                        source, destination, fromDate, toDate);
        return inventoryRepo.findAll(spec,pageable)
                .map(this::maptoinventoryResponseDto);
    }

    @Transactional
    @Override
    public void bulkImport(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            List<InventoryEntity> batch = new ArrayList<>(500);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
                Row row = sheet.getRow(i);
                if (row == null) continue;

                batch.add(mapRowToEntity(row));

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


    private InventoryEntity mapRowToEntity(Row row) {

        return InventoryEntity.builder()
                .flightNumber(row.getCell(0).getStringCellValue())
                .source(row.getCell(1).getStringCellValue())
                .destination(row.getCell(2).getStringCellValue())
                .flightDate(row.getCell(3).getLocalDateTimeCellValue().toLocalDate())
                .economySeats((int) row.getCell(4).getNumericCellValue())
                .businessSeats((int) row.getCell(5).getNumericCellValue())
                .economyPrice(BigDecimal.valueOf(row.getCell(6).getNumericCellValue()))
                .businessPrice(BigDecimal.valueOf(row.getCell(7).getNumericCellValue()))
                .build();
    }



}
