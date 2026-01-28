package org.example.customermanagementservice.Controller;

import lombok.RequiredArgsConstructor;
import org.example.customermanagementservice.Dto.CustomerResDto;
import org.example.customermanagementservice.Service.Customerservice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/customer")
@RequiredArgsConstructor
public class CustomerConfigController {
    private final Customerservice customerservice;

    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<CustomerResDto> getCustomer(@PathVariable Long Id) {
        return ResponseEntity.ok(customerservice.getCustomerById(Id));
    }

}
