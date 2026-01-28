package org.example.customermanagementservice.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReqDto {
    @NotBlank(message = "mail cannot be blank")
    private String email;

    @NotBlank(message = "passowrd cannot be empty")
    private String password;
}
