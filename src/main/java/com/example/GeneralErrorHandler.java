package com.example;

import com.example.collectionRequirements.approval.ApprovalException;
import com.example.collectionRequirements.event.EventException;
import com.example.collectionRequirements.event.EventNotFound;
import com.example.collectionRequirements.request.RequestAlreadyLinked;
import com.example.collectionRequirements.request.RequestException;
import com.example.collectionRequirements.request.RequestNotApproved;
import com.example.collectionRequirements.request.RequestNotFound;
import com.example.department.DepartmentAlreadyExist;
import com.example.department.DepartmentException;
import com.example.department.DepartmentNotFound;
import com.example.region.RegionException;
import com.example.region.RegionNotFound;
import com.example.user.UserAlreadyExist;
import com.example.user.UserException;
import com.example.user.UserNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneralErrorHandler {

    // ==================== NOT FOUND EXCEPTIONS (404) ====================
    @ExceptionHandler({UserNotFound.class, EventNotFound.class, RequestNotFound.class,
                       DepartmentNotFound.class, RegionNotFound.class})
    public ResponseEntity<Map<String,Object>> handleResourceNotFound(RuntimeException e, HttpServletRequest request) {
        return createErrorResponse(404, "Not Found", e, request, HttpStatus.NOT_FOUND);
    }

    // ==================== CONFLICT EXCEPTIONS (409) ====================
    @ExceptionHandler({UserAlreadyExist.class, DepartmentAlreadyExist.class, RequestAlreadyLinked.class})
    public ResponseEntity<Map<String,Object>> handleConflict(RuntimeException e, HttpServletRequest request) {
        return createErrorResponse(409, "Conflict", e, request, HttpStatus.CONFLICT);
    }

    // ==================== BAD REQUEST EXCEPTIONS (400) ====================
    @ExceptionHandler(RequestNotApproved.class)
    public ResponseEntity<Map<String,Object>> handleBadRequest(RuntimeException e, HttpServletRequest request) {
        return createErrorResponse(400, "Bad Request", e, request, HttpStatus.BAD_REQUEST);
    }

    // ==================== GENERAL EXCEPTIONS ====================
    @ExceptionHandler({UserException.class, EventException.class, RequestException.class,
                       DepartmentException.class, RegionException.class, ApprovalException.class})
    public ResponseEntity<Map<String,Object>> handleBusinessException(RuntimeException e, HttpServletRequest request) {
        return createErrorResponse(400, "Business Logic Error", e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleException(Exception e, HttpServletRequest request) {
        return createErrorResponse(500, "Internal Server Error", e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== HELPER METHOD ====================
    private Map<String,Object> buildErrorMap(int status, String error, Exception e, HttpServletRequest request) {
        Map<String,Object> errorMap = new LinkedHashMap<>();
        errorMap.put("status", status);
        errorMap.put("error", error);
        errorMap.put("exception", e.getClass().getSimpleName());
        errorMap.put("message", e.getMessage());
        errorMap.put("path", request.getRequestURI());
        errorMap.put("method", request.getMethod());
        errorMap.put("timestamp", LocalDateTime.now());
        return errorMap;
    }

    private ResponseEntity<Map<String,Object>> createErrorResponse(int status, String error, Exception e,
                                                                     HttpServletRequest request, HttpStatus httpStatus) {
        return new ResponseEntity<>(buildErrorMap(status, error, e, request), httpStatus);
    }
}
