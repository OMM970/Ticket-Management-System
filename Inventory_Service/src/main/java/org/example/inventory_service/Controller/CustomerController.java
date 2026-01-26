package org.example.inventory_service.Controller;

import lombok.RequiredArgsConstructor;
import org.example.inventory_service.Dto.CustomResponseDto;
import org.example.inventory_service.Dto.PageResponse;
import org.example.inventory_service.Service.CustomerInventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("api/v1/inventory")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerInventoryService customerInventoryService;

    @GetMapping("/getall")
    public ResponseEntity<PageResponse<CustomResponseDto>> getAllInventory(
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

        Page<CustomResponseDto> pageData = customerInventoryService.getAllInventory(source, destination, fromDate, toDate,pageable);

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


}
