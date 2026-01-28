package org.example.customermanagementservice.Dto.Events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Eventmaker {
    private String customerId;
    private String firstname;
    private String email;
}
