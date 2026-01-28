    package org.example.notificationservice.Service;
    
    import jakarta.mail.MessagingException;
    import jakarta.mail.internet.MimeMessage;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.example.notificationservice.Dto.MailDto;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.stereotype.Service;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class MailServiceImpl implements MailService {

        private final JavaMailSender mailSender;


        @Value("${spring.mail.username}")
        private String from;

        @Override
        public void Sendmail(MailDto mailDto) {
            try {
                String html = loadWelcomeTemplate(mailDto.getFirstname());

                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper =
                        new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setFrom(from);
                helper.setTo(mailDto.getEmail());
                helper.setSubject("Welcome to Airways ");
                helper.setText(html, true);

                mailSender.send(mimeMessage);

                log.info("Welcome mail sent to {}", mailDto.getEmail());

            } catch (Exception e) {
                log.error("Failed to send welcome mail", e);
                throw new RuntimeException("Email sending failed");
            }
        }

        private String loadWelcomeTemplate(String customerFirstName) throws Exception {
            String html = new String(
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream("Templates/Welcome_file.html")
                            .readAllBytes()
            );

            return html.replace("${customerFirstName}", customerFirstName);
        }
    }