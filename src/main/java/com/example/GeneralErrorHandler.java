package com.example;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneralErrorHandler {

    @ExceptionHandler
    public Map<String,Object> handleException(Exception e, HttpServletRequest request) {
        Map<String,Object> errorMap = new LinkedHashMap<>();
        errorMap.put("statusCode",400);
        errorMap.put("error","Bad Request");
        errorMap.put("exception",e.getClass());
        errorMap.put("message",e.getMessage());
        errorMap.put("path",request.getRequestURI());
        errorMap.put("method",request.getMethod());
        errorMap.put("timestamp", LocalDateTime.now());
        return  errorMap;
    }
}
