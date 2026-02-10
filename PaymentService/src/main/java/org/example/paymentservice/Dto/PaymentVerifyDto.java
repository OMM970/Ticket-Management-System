package org.example.paymentservice.Dto;

import lombok.Data;

@Data
public class PaymentVerifyDto {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
