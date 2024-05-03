package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    final private JavaMailSender javaMailSender;

    @Async
    @Override
    public OperationStatusDTO sendPasswordRecoveryEmail(String title, String mailFrom, String mailTo, String message) {
        log.debug("sending email to: {}", mailTo);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailFrom);
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setTo(mailTo);
        simpleMailMessage.setText(message);
        this.javaMailSender.send(simpleMailMessage);
        log.debug("mail sent");
        return new OperationStatusDTO(true, "Mail sent");
    }
}
