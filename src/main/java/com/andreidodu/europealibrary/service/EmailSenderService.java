package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Future;

public interface EmailSenderService {
    @Async
    Future<OperationStatusDTO> sendPasswordRecoveryEmail(String title, String mailFrom, String mailTo, String message);
}
