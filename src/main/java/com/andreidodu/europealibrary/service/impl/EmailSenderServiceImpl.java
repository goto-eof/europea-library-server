package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.service.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    final private JavaMailSender javaMailSender;

    @Async
    @Override
    public Future<OperationStatusDTO> sendEmail(String title, String mailFrom, String mailTo, String message) throws MessagingException {
        log.debug("sending email to: {}", mailTo);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject(title);
        helper.setTo(mailTo);
        helper.setFrom(mailFrom);
        helper.setText(message, true);
        try {
            this.javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.debug("mail sent");
            return CompletableFuture.completedFuture(new OperationStatusDTO(false, "Mail NOT sent"));
        }
        log.debug("mail sent");
        return CompletableFuture.completedFuture(new OperationStatusDTO(true, "Mail sent"));
    }
}
