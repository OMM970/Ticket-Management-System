package org.example.inventory_service.Controller;

import lombok.RequiredArgsConstructor;
import org.example.inventory_service.Dto.*;
import org.example.inventory_service.Service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/add")
    public ResponseEntity<ResponseDto> addInventory(
            @RequestBody RequestDto requestDto
    ) {
        ResponseDto response = inventoryService.addToinventory(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getall")
    public ResponseEntity<PageResponse<ResponseDto>> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(defaultValue = "FlightDate") String sortby,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            Date fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            Date toDate

    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ?Sort.by(sortby).descending()
                : Sort.by(sortby).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResponseDto> pageData = inventoryService.getAllInventory(source, destination, fromDate, toDate,pageable);

        return ResponseEntity.ok(
                new PageResponse<>(
                        pageData.getContent(),
                        pageData.getNumber(),
                        pageData.getSize(),
                        pageData.getTotalElements(),
                        pageData.getTotalPages(),
                        pageData.isLast()
                )
        );
    }



        @PostMapping("/upload")
    public ResponseEntity<String> bulkUpload(
            @RequestParam("file") MultipartFile file) {

        inventoryService.bulkImport(file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Inventory uploaded successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getInventoryById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(inventoryService.findbyflightbyid(id));    }

    @PostMapping("/hold")
    public ResponseEntity<String> holdSeats(
            @RequestBody SeatsActionDto dto) {
        inventoryService.holdSeats(dto);

        return ResponseEntity.ok("Seats held successfully");
    }


    @PostMapping("/confirm")
    public ResponseEntity<String> confirmSeats(
            @RequestBody SeatsActionDto request) {

        inventoryService.confirmSeats(request);

        return ResponseEntity.ok("Seats confirmed successfully");
    }



    @PostMapping("/release")
    public ResponseEntity<String> releaseSeats(
            @RequestBody SeatsActionDto dto) {

        inventoryService.releaseSeats(dto);

        return ResponseEntity.ok("Seats released");
    }

}
