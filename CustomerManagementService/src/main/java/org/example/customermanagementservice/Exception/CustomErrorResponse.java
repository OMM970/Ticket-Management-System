package org.example.customermanagementservice.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String sucess;
}