package org.example.inventory_service.Service;

import lombok.RequiredArgsConstructor;
import org.example.inventory_service.Dto.CustomResponseDto;
import org.example.inventory_service.Entity.InventoryEntity;
import org.example.inventory_service.Repository.InventoryRepo;
import org.example.inventory_service.Util.InventorySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CustomerInventoryServiceImpl implements CustomerInventoryService {
    private final InventoryRepo inventoryRepo;

    @Override
    public CustomResponseDto maptoinventoryCustomResponseDto(InventoryEntity entity){
        return  CustomResponseDto.builder()
                .flightNumber(entity.getFlightNumber())
                .businessPrice(entity.getBusinessPrice())
                .destination(entity.getDestination())
                .economyPrice(entity.getEconomyPrice())
                .source(entity.getSource())
                .flightDate(entity.getFlightDate())
                .build();
    }

    @Override
    public Page<CustomResponseDto> getAllInventory(
            String source,
            String destination,
            Date fromDate,
            Date toDate,
            Pageable pageable) {
        Specification<InventoryEntity> spec=
                InventorySpecification.filter(
                        source, destination, fromDate, toDate);
        return inventoryRepo.findAll(spec,pageable)
                .map(this::maptoinventoryCustomResponseDto);
    }

}
