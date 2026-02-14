package org.example.paymentservice.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "${booking-service.service.name}",
        url  = "${booking-service.service.url}"
)
public interface BookingServiceClient {
    @PostMapping("api/v1/bookings/confirmBooking/{idempotencyKey}")
    ResponseEntity<?> confirmBooking(
            @PathVariable String idempotencyKey
    );
}
