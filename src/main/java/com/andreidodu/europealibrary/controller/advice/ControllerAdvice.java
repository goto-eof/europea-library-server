package com.andreidodu.europealibrary.controller.advice;

import com.andreidodu.europealibrary.dto.HttpErrorDTO;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpErrorDTO handleEntityNotFoundException(EntityNotFoundException e) {
        return new HttpErrorDTO(404, e.getMessage());
    }

}