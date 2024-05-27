package com.andreidodu.europealibrary.resource.advice;

import com.andreidodu.europealibrary.dto.HttpErrorAdditionalInfoDTO;
import com.andreidodu.europealibrary.dto.HttpErrorDTO;
import com.andreidodu.europealibrary.enums.InternalErrorCodeEnum;
import com.andreidodu.europealibrary.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpErrorDTO handleEntityNotFoundException(EntityNotFoundException e) {
        log.debug(e.getMessage());
        return new HttpErrorDTO(HttpStatus.NOT_FOUND.value(), "Item not found");
    }

    @ExceptionHandler(value = {WorkInProgressException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public HttpErrorDTO handleWorkInProgressException(WorkInProgressException e) {
        log.debug(e.getMessage());
        return new HttpErrorDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service unavailable");
    }


    @ExceptionHandler(value = {BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public HttpErrorDTO handleBadCredentialsException(BadCredentialsException e) {
        log.debug(e.getMessage());
        return new HttpErrorDTO(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpErrorDTO handleValidationException(ValidationException e) {
        log.debug(e.getMessage());
        return new HttpErrorDTO(HttpStatus.BAD_REQUEST.value(), "Bad request");
    }

    @ExceptionHandler(value = {ApplicationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpErrorDTO handleApplicationExceptionException(ApplicationException e) {
        log.debug(e.getMessage());
        return new HttpErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
    }

    @ExceptionHandler(value = {NullPointerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpErrorDTO handleApplicationExceptionException(NullPointerException e) {
        log.debug(e.toString());
        return new HttpErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
    }

    @ExceptionHandler(value = {FurtherStepNeededException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpErrorDTO handleApplicationExceptionException(FurtherStepNeededException e) {
        log.debug(e.toString());
        HttpErrorAdditionalInfoDTO error = new HttpErrorAdditionalInfoDTO();
        error.setCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage("Bad request");
        error.setInternalErrorCode(InternalErrorCodeEnum.MANDATORY_PAYEE_INFO);
        error.setDescription(InternalErrorCodeEnum.MANDATORY_PAYEE_INFO.getDescription());
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    HttpErrorDTO onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.debug(e.getMessage());
        HttpErrorDTO error = new HttpErrorDTO();
        error.setCode(400);
        error.setMessage("validations errors: ");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.setMessage(error.getMessage() + " " + fieldError.getField() + "-" + fieldError.getDefaultMessage());
        }
        return error;
    }

}