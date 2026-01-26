package org.example.customermanagementservice.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.customermanagementservice.Dto.CustomerReqDto;
import org.example.customermanagementservice.Dto.CustomerResDto;
import org.example.customermanagementservice.Dto.LoginReqDto;
import org.example.customermanagementservice.Dto.LoginResDto;
import org.example.customermanagementservice.Service.Customerservice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/customer/auth")
@RequiredArgsConstructor
public class CustomerController {
    private final Customerservice customerservice;
    @PostMapping("/register")
    public ResponseEntity<CustomerResDto> registerCustomer(@RequestBody @Valid CustomerReqDto customerReqDto) {
        return ResponseEntity.ok(customerservice.registerCustomer(customerReqDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResDto> loginCustomer(@RequestBody @Valid LoginReqDto loginReqDto) {
        return ResponseEntity.ok(customerservice.loginCustomer(loginReqDto));
    }


}
