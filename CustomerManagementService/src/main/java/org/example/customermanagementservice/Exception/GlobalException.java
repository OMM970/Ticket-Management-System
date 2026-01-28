package org.example.customermanagementservice.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(CoustomerNotFound.class)
    public ResponseEntity<CustomErrorResponse> handleMailExistException(CoustomerNotFound e) {
        CustomErrorResponse response = new CustomErrorResponse(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                "false"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(PasswordErrorException.class)
    public ResponseEntity<CustomErrorResponse> handleMailExistException(PasswordErrorException e) {
        CustomErrorResponse response = new CustomErrorResponse(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                "false"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
