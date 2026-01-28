package org.example.customermanagementservice.Dto;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.websocket.OnMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.customermanagementservice.Enum.Customer_Type;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReqDto {

    @NotBlank(message = "Cannot be blank")
    private String firstName;

    @NotBlank(message = "Cannot be blank")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "Invalid email format (e.g., joe@gmail.com)")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character"
    )
    @NotBlank(message = "Password cannot be empty")
    private String password;
    @NotBlank(message = "feild cannot be blank")
    private String confirmPassword;

    @Pattern(
            regexp = "^\\+[1-9]\\d{1,14}$",
            message = "Invalid phone number format. Must be a valid phone number (e.g., +919876543210)."
    )
    private String phoneNumber;

    private Customer_Type type;
}
