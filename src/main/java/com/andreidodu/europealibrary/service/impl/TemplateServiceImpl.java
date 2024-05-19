package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.PasswordResetEmailRequestDTO;
import com.andreidodu.europealibrary.service.TemplateService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    final Configuration freeMarkerConfiguration;

    @Override
    public String generatePasswordResetEmailContent(PasswordResetEmailRequestDTO passwordResetEmailRequestDTO) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("passwordResetEmailRequest", passwordResetEmailRequestDTO);
        freeMarkerConfiguration.getTemplate("email/password-reset.html")
                .process(model, stringWriter);
        return stringWriter.getBuffer().toString();
    }
}
