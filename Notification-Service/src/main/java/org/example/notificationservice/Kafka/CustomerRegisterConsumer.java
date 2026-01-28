package org.example.notificationservice.Kafka;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.Dto.MailDto;
import org.example.notificationservice.Service.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerRegisterConsumer {
    private final MailService mailService;
    @KafkaListener(
            topics = "customer-register-topic",
            groupId = "notification-service-group"
    )
    public void consume(MailDto mailDto) throws MessagingException {
        log.info("📥 Kafka event received | customerId={} | email={}",
                mailDto.getCustomerId(), mailDto.getEmail());
        mailService.Sendmail(mailDto);
    }

}
