package com.andreidodu.europealibrary.controller.advice;

import com.andreidodu.europealibrary.dto.HttpErrorDTO;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.exception.WorkInProgressException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpErrorDTO handleEntityNotFoundException(EntityNotFoundException e) {
        return new HttpErrorDTO(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = {WorkInProgressException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public HttpErrorDTO handleWorkInProgressException(WorkInProgressException e) {
        return new HttpErrorDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage());
    }


    @ExceptionHandler(value = {BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public HttpErrorDTO handleException(BadCredentialsException e) {
        return new HttpErrorDTO(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

}