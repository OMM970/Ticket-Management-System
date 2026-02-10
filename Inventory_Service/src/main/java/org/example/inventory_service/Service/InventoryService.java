package org.example.inventory_service.Service;

import org.example.inventory_service.Dto.RequestDto;
import org.example.inventory_service.Dto.ResponseDto;
import org.example.inventory_service.Entity.InventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface InventoryService {
    ResponseDto addToinventory(RequestDto RequestDto);

    ResponseDto maptoinventoryResponseDto(InventoryEntity entity);

    Page<ResponseDto> getAllInventory(
            String source,
            String destination,
            Date fromDate,
            Date toDate,
            Pageable pageable);

    void bulkImport(MultipartFile file);

    ResponseDto findbyflightbyid(Integer id);

    void releaseSeats(
            String flightId,
            String seatType,
            int seats,
            String idempotencyKey
    );

     void confirmSeats(
            String flightId,
            String seatType,
            int seats,
            String idempotencyKey
    );

    void holdSeats(
            String flightId,
            String seatType,
            int seats,
            String idempotencyKey
    );


}
