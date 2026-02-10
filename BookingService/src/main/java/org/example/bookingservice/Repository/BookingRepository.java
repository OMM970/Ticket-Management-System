package org.example.bookingservice.Repository;


import org.example.bookingservice.Entity.BookingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<BookingEntity,String > {

   Optional<BookingEntity> findByIdempotencyKey(String idempotencyKey);
}
