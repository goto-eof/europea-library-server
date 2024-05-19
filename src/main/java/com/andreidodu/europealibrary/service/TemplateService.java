package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.PasswordResetEmailRequestDTO;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface TemplateService {
    String generatePasswordResetEmailContent(PasswordResetEmailRequestDTO passwordResetEmailRequestDTO) throws IOException, TemplateException;
}
