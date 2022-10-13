package com.example.employeeApplication.exception.dto;

import com.example.employeeApplication.exception.ApiException;
import com.example.employeeApplication.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageDto {
    private String message;
    private ErrorCode code;

    public ErrorMessageDto(ApiException ex) {
        this.code = ex.getErrorCode();
        this.message = ex.getMessage();
    }
}
