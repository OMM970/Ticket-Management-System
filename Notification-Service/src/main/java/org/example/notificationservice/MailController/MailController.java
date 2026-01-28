package org.example.notificationservice.MailController;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.notificationservice.Dto.MailDto;
import org.example.notificationservice.Service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/mailservice")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/sentMail")
    public ResponseEntity<String> sendmail(@RequestBody MailDto dto) throws MessagingException {
        mailService.Sendmail(dto);
        return ResponseEntity.ok("Mail sent successfully");
    }
}
