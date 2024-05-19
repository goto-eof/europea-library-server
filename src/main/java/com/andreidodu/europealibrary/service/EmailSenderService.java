package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Future;

public interface EmailSenderService {
    @Async
    Future<OperationStatusDTO> sendEmail(String title, String mailFrom, String mailTo, String message) throws MessagingException;
}
