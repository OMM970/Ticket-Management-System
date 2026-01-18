package org.example.inventory_service.Service;
;
import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import org.example.inventory_service.Dto.RequestDto;
import org.example.inventory_service.Dto.ResponseDto;
import org.example.inventory_service.Entity.InventoryEntity;
import org.example.inventory_service.Repository.InventoryRepo;

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

}
