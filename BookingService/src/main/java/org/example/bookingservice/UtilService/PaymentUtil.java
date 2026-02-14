package org.example.bookingservice.UtilService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
@Component
public class PaymentUtil {

  @Value("${jwt.payment.secret}")
    private String secret;
  @Value("${jwt.payment.expiry-minutes}")
    private int expiryMinutes;

    public String generateToken(String idopotencyKey, String userId, BigDecimal amount,String bookingId) {

        return Jwts.builder()
                .claim("idopotencyKey", idopotencyKey)
                .claim("userId", userId)
                .claim("amount", amount)
                .claim("bookingId", bookingId)
                .setIssuedAt(new Date())
                .setExpiration(
                        Date.from(
                                Instant.now()
                                        .plus(expiryMinutes, ChronoUnit.MINUTES)
                        )
                )
                .signWith(
                        Keys.hmacShaKeyFor(secret.getBytes()),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

}
