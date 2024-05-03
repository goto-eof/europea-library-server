package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    public Future<OperationStatusDTO> sendPasswordRecoveryEmail(String title, String mailFrom, String mailTo, String message) {
        log.debug("sending email to: {}", mailTo);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailFrom);
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setTo(mailTo);
        simpleMailMessage.setText(message);

        try {
            this.javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("mail sent");
            return CompletableFuture.completedFuture(new OperationStatusDTO(false, "Mail NOT sent"));
        }
        log.debug("mail sent");
        return CompletableFuture.completedFuture(new OperationStatusDTO(true, "Mail sent"));
    }
}
