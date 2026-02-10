package org.example.paymentservice.Repository;

import org.example.paymentservice.Entity.PaymentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepo extends MongoRepository<PaymentEntity, String> {
    boolean existsByBookingId(String bookingId);

    Optional<PaymentEntity> findByRazorpayOrderId(String razorpayOrderId);
}
