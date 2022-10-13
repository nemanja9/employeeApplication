package com.example.employeeApplication.exception;

import com.example.employeeApplication.exception.dto.ErrorMessageDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ComponentScan
@Component
@Configuration
@RequiredArgsConstructor
public class ExceptionHandlerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiException.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorMessageDto> handleApiException(ApiException ex) {
        LOGGER.warn("Api exception", ex);
        return ResponseEntity.status(ex.getHttpStatus()).contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessageDto(ex));
    }
}
