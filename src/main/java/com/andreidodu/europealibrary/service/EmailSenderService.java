package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import org.springframework.scheduling.annotation.Async;

public interface EmailSenderService {
    @Async
    OperationStatusDTO sendPasswordRecoveryEmail(String title, String mailTo, String message);
}
