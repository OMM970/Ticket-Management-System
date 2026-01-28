package org.example.customermanagementservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.customermanagementservice.Config.Security;
import org.example.customermanagementservice.Dto.CustomerReqDto;
import org.example.customermanagementservice.Dto.CustomerResDto;
import org.example.customermanagementservice.Dto.Events.Eventmaker;
import org.example.customermanagementservice.Dto.LoginReqDto;
import org.example.customermanagementservice.Dto.LoginResDto;
import org.example.customermanagementservice.Entity.CustomerEntity;
import org.example.customermanagementservice.Exception.CoustomerNotFound;
import org.example.customermanagementservice.Exception.PasswordErrorException;
import org.example.customermanagementservice.Kafka.KafkaSender;
import org.example.customermanagementservice.Repository.Customer_Repo;
import org.example.customermanagementservice.Security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Customerserviceimpl implements Customerservice {
    private final Customer_Repo customerRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaSender kafkaSender;

    @Override
    public CustomerResDto maptoDto(CustomerEntity customerEntity) {
        CustomerResDto customerResDto = CustomerResDto.builder()
                .email(customerEntity.getEmail())
                .type(String.valueOf(customerEntity.getType()))
                .lastName(customerEntity.getLastName())
                .phoneNumber(customerEntity.getPhoneNumber())
                .firstName(customerEntity.getFirstName())
                .build();
        return customerResDto;

    }

    @Override
    public CustomerResDto registerCustomer(CustomerReqDto customerReqDto) {
        CustomerEntity customerEntity = CustomerEntity.builder()
                .email(customerReqDto.getEmail())
                .firstName(customerReqDto.getFirstName())
                .lastName(customerReqDto.getLastName())
                .password(passwordEncoder.encode(customerReqDto.getPassword()))
                .confirmPassword(customerReqDto.getConfirmPassword())
                .phoneNumber(customerReqDto.getPhoneNumber())
                .type(customerReqDto.getType()).build();
        customerEntity = customerRepo.save(customerEntity);
        Eventmaker eventmaker = Eventmaker.builder()
                .email(customerEntity.getEmail())
                .customerId(String.valueOf(customerEntity.getId()))
                .firstname(customerEntity.getFirstName())
                .build();
        kafkaSender.sendEvent(eventmaker);
        return maptoDto(customerEntity);

    }

    @Override
    public LoginResDto loginCustomer(LoginReqDto loginReqDto) {
        CustomerEntity customer = customerRepo.findByEmail(loginReqDto.getEmail())
                .orElseThrow(() -> new CoustomerNotFound("Coustomer not found"));
        if (!passwordEncoder.matches(loginReqDto.getPassword(), customer.getPassword())) {
            throw new PasswordErrorException("Invalid Password");
        }
        String token = jwtUtil.generateToken(
                customer.getId(),
                customer.getEmail(),
                customer.getType()
        );
        return new LoginResDto(
                customer.getEmail(),
                token,
                "Welcome to Ticket Manager"
        );
    }

    @Override
    public CustomerResDto getCustomerById(Long id) {
        CustomerEntity customer=customerRepo.findById(id)
                .orElseThrow(()->new CoustomerNotFound("Customer not found"));
        return maptoDto(customer);
    }


}
