package org.example.customermanagementservice.Kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.customermanagementservice.Dto.Events.Eventmaker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaSender {

    public static final String CUSTOMER_REGISTER_TOPIC = "customer-register-topic";
    private final KafkaTemplate<String, Eventmaker> kafkaTemplate;

    public void sendEvent(Eventmaker eventmaker) {
        log.info("📤 Sending customer registration event to Kafka | customerId={} | email={}",
                eventmaker.getCustomerId(),
                eventmaker.getEmail()
        );

        kafkaTemplate.send(
                CUSTOMER_REGISTER_TOPIC,
                eventmaker.getCustomerId().toString(),
                eventmaker
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Kafka event sent successfully | topic={} | partition={} | offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            } else {
                log.error("❌ Failed to send Kafka event | customerId={}",
                        eventmaker.getCustomerId(),
                        ex
                );
            }
        });
    }
}