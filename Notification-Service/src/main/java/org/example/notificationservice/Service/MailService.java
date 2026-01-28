package org.example.notificationservice.Service;

import jakarta.mail.MessagingException;
import org.example.notificationservice.Dto.MailDto;

public interface MailService {
    void Sendmail(MailDto dto) throws MessagingException;
}
