package org.example.customermanagementservice.Service;

import org.example.customermanagementservice.Dto.CustomerReqDto;
import org.example.customermanagementservice.Dto.CustomerResDto;
import org.example.customermanagementservice.Dto.LoginReqDto;
import org.example.customermanagementservice.Dto.LoginResDto;
import org.example.customermanagementservice.Entity.CustomerEntity;

public interface Customerservice {
    CustomerResDto maptoDto(CustomerEntity customerEntity);

    CustomerResDto registerCustomer(CustomerReqDto customerReqDto);

    LoginResDto loginCustomer(LoginReqDto loginReqDto);
}
