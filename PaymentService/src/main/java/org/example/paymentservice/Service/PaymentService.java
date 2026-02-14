package org.example.paymentservice.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.example.paymentservice.Dto.PaymentVerifyDto;
import org.example.paymentservice.Entity.PaymentEntity;
import org.example.paymentservice.Enums.Status;
import org.example.paymentservice.Feign.BookingServiceClient;
import org.example.paymentservice.Repository.PaymentRepo;
import org.example.paymentservice.Utill.PaymentTokenUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentTokenUtil tokenUtil;
    private final PaymentRepo paymentRepository;
    private final BookingServiceClient bookingService;


    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public Map<String, Object> initiate(String token) {


        Claims claims = tokenUtil.parseToken(token);

        String bookingId = claims.get("bookingId", String.class);
        String userId = claims.get("userId", String.class);
        Double amount = claims.get("amount", Double.class);
        String idempotencyKey = claims.get("idopotencyKey", String.class);


        if (bookingId == null || userId == null || amount == null) {
            throw new IllegalArgumentException("Invalid payment token");
        }


        if (paymentRepository.existsByBookingId(bookingId)) {
            throw new IllegalStateException("Payment already initiated for this booking");
        }


        int amountInPaise = BigDecimal.valueOf(amount)
                .multiply(BigDecimal.valueOf(100))
                .intValueExact();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", bookingId);
        orderRequest.put("notes", new JSONObject()
                .put("bookingId", bookingId)
                .put("userId", userId)
        );

        Order order;
        try {
            order = razorpayClient.orders.create(orderRequest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }


        PaymentEntity payment = PaymentEntity.builder()
                .bookingId(bookingId)
                .userId(userId)
                .amount(amount)
                .idempotencyKey(idempotencyKey)
                .razorpayOrderId(order.get("id"))
                .status(Status.valueOf("ORDER_CREATED"))
                .build();

        paymentRepository.save(payment);

        // 6️⃣ Return response to frontend
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", amountInPaise);
        response.put("currency", "INR");
        response.put("key", razorpayKey);

        return response;
    }

    public void processWebhook(String payload, String razorpaySignature) {

        log.info("🔔 Razorpay webhook received");

        // 1️⃣ VERIFY SIGNATURE (CRITICAL)
        boolean isValid;
        try {
            isValid = Utils.verifyWebhookSignature(
                    payload,
                    razorpaySignature,
                    webhookSecret
            );
        } catch (Exception e) {
            log.error("❌ Webhook signature verification error", e);
            return; // NEVER throw
        }

        if (!isValid) {
            log.error("❌ Invalid Razorpay webhook signature");
            return; // NEVER throw
        }

        log.info("✅ Webhook signature verified");

        // 2️⃣ PARSE PAYLOAD
        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        log.info("📌 Event received: {}", event);

        if (!"payment.captured".equals(event)) {
            log.info("ℹ️ Ignoring event {}", event);
            return;
        }

        JSONObject paymentJson = json
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId = paymentJson.getString("order_id");
        String razorpayPaymentId = paymentJson.getString("id");

        // 3️⃣ FETCH PAYMENT RECORD
        PaymentEntity payment = paymentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElse(null);

        if (payment == null) {
            log.error("❌ Payment record not found for orderId {}", razorpayOrderId);
            return;
        }

        // 4️⃣ IDEMPOTENCY CHECK
        if (payment.getStatus() == Status.SUCCESS) {
            log.warn("⚠️ Payment already SUCCESS, skipping update");
            return;
        }

        // 5️⃣ UPDATE STATUS
        payment.setStatus(Status.SUCCESS);
        payment.setRazorpayPaymentId(razorpayPaymentId);
        paymentRepository.save(payment);
        bookingService.confirmBooking(payment.getIdempotencyKey());

        log.info("✅ Payment marked SUCCESS for bookingId {}", payment.getBookingId());
    }






}
