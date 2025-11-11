package com.example.collectionRequirements.request;

import com.example.DTOs.*;
import com.example.department.DepartmentException;
import com.example.user.UserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestsViewDetails testRequestView;
    private RequestDetails testRequestDetails;
    private RequestSubmitResponse testResponse;
    private Request testRequest;

    @BeforeEach
    void setUp() {
        // Initialize test data for view
        testRequestView = new RequestsViewDetails();
        testRequestView.setRequestId(1L);
        testRequestView.setRequestStatus("Submitted");
        testRequestView.setRequestDate(LocalDate.now());
        testRequestView.setCurriculum("http://curriculum.com");
        testRequestView.setTanNo("TAN123");
        testRequestView.setJustification("Test justification");
        testRequestView.setNoOfParticipants(5);
        testRequestView.setDepartment("IT");
        testRequestView.setEventName("Training Event");
        testRequestView.setRequestedBy("user123");
        testRequestView.setApprovedBy("Not Approved Yet");
        testRequestView.setApprovalNotes("Not Approved Yet");

        // Initialize test data for request details
        testRequestDetails = new RequestDetails();
        testRequestDetails.setRequestorId("user123");
        testRequestDetails.setDepartment("IT");
        testRequestDetails.setJustification("Test justification");
        testRequestDetails.setTanNo("TAN123");
        testRequestDetails.setCurriculum("http://curriculum.com");
        testRequestDetails.setNoOfParticipants(5);
        testRequestDetails.setUsersCdsId(new String[]{"user123"});

        // Initialize response
        testResponse = new RequestSubmitResponse("Operation successful");

        // Initialize request entity
        testRequest = new Request();
        testRequest.setRequestId(1L);
        testRequest.setRequestStatus("Submitted");
    }

    // ==================== POST /newRequest TESTS ====================

    @Test
    void testSubmitNewRequest_Success() throws Exception {
        // Arrange
        when(requestService.submitNewRequest(any(RequestDetails.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/requests/newRequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequestDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Operation successful"));

        verify(requestService, times(1)).submitNewRequest(any(RequestDetails.class));
    }

    @Test
    void testSubmitNewRequest_UserNotFound() throws Exception {
        // Arrange
        when(requestService.submitNewRequest(any(RequestDetails.class)))
                .thenThrow(new com.example.user.UserNotFound("User not found"));

        // Act & Assert
        mockMvc.perform(post("/api/requests/newRequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequestDetails)))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).submitNewRequest(any(RequestDetails.class));
    }

    @Test
    void testSubmitNewRequest_DepartmentNotFound() throws Exception {
        // Arrange
        when(requestService.submitNewRequest(any(RequestDetails.class)))
                .thenThrow(new com.example.department.DepartmentNotFound("Department not found"));

        // Act & Assert
        mockMvc.perform(post("/api/requests/newRequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequestDetails)))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).submitNewRequest(any(RequestDetails.class));
    }

    // ==================== GET /all TESTS ====================

    @Test
    void testGetAllRequests_Success() throws Exception {
        // Arrange
        List<RequestsViewDetails> requestList = Arrays.asList(testRequestView);
        when(requestService.getAllRequests()).thenReturn(requestList);

        // Act & Assert
        mockMvc.perform(get("/api/requests/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(1))
                .andExpect(jsonPath("$[0].requestStatus").value("Submitted"))
                .andExpect(jsonPath("$[0].department").value("IT"));

        verify(requestService, times(1)).getAllRequests();
    }

    @Test
    void testGetAllRequests_NoRequests() throws Exception {
        // Arrange
        when(requestService.getAllRequests())
                .thenThrow(new RequestNotFound("No Requests Found"));

        // Act & Assert
        mockMvc.perform(get("/api/requests/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getAllRequests();
    }

    // ==================== GET /{requestId} TESTS ====================

    @Test
    void testGetRequestById_Success() throws Exception {
        // Arrange
        when(requestService.getRequestById(1L)).thenReturn(testRequestView);

        // Act & Assert
        mockMvc.perform(get("/api/requests/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(1))
                .andExpect(jsonPath("$.requestStatus").value("Submitted"))
                .andExpect(jsonPath("$.department").value("IT"));

        verify(requestService, times(1)).getRequestById(1L);
    }

    @Test
    void testGetRequestById_NotFound() throws Exception {
        // Arrange
        when(requestService.getRequestById(999L))
                .thenThrow(new RequestNotFound("Request not found"));

        // Act & Assert
        mockMvc.perform(get("/api/requests/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getRequestById(999L);
    }

    // ==================== GET /requestor/{cdsId} TESTS ====================

    @Test
    void testGetRequestByCdsId_Success() throws Exception {
        // Arrange
        List<RequestsViewDetails> requestList = Arrays.asList(testRequestView);
        when(requestService.getRequestByCdsId("user123")).thenReturn(requestList);

        // Act & Assert
        mockMvc.perform(get("/api/requests/requestor/user123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(1))
                .andExpect(jsonPath("$[0].requestedBy").value("user123"));

        verify(requestService, times(1)).getRequestByCdsId("user123");
    }

    @Test
    void testGetRequestByCdsId_UserNotFound() throws Exception {
        // Arrange
        when(requestService.getRequestByCdsId("invalidUser"))
                .thenThrow(new com.example.user.UserNotFound("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/requests/requestor/invalidUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getRequestByCdsId("invalidUser");
    }

    @Test
    void testGetRequestByCdsId_RequestNotFound() throws Exception {
        // Arrange
        when(requestService.getRequestByCdsId("user123"))
                .thenThrow(new RequestNotFound("Request not found"));

        // Act & Assert
        mockMvc.perform(get("/api/requests/requestor/user123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getRequestByCdsId("user123");
    }

    // ==================== GET /status/{status} TESTS ====================

    @Test
    void testGetRequestByStatus_Success() throws Exception {
        // Arrange
        List<Request> requestList = Arrays.asList(testRequest);
        when(requestService.getRequestByStatus("Submitted")).thenReturn(requestList);

        // Act & Assert
        mockMvc.perform(get("/api/requests/status/Submitted")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(1));

        verify(requestService, times(1)).getRequestByStatus("Submitted");
    }

    @Test
    void testGetRequestByStatus_NoResults() throws Exception {
        // Arrange
        List<Request> emptyList = Arrays.asList();
        when(requestService.getRequestByStatus("Approved")).thenReturn(emptyList);

        // Act & Assert
        mockMvc.perform(get("/api/requests/status/Approved")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(requestService, times(1)).getRequestByStatus("Approved");
    }

    // ==================== PUT /update/{requestId} TESTS ====================

    @Test
    void testUpdateRequest_Success() throws Exception {
        // Arrange
        RequestSubmitResponse updateResponse = new RequestSubmitResponse("Request updated successfully");
        when(requestService.updateRequest(eq(1L), any(RequestDetails.class)))
                .thenReturn(updateResponse);

        // Act & Assert
        mockMvc.perform(put("/api/requests/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequestDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Request updated successfully"));

        verify(requestService, times(1)).updateRequest(eq(1L), any(RequestDetails.class));
    }

    @Test
    void testUpdateRequest_NotFound() throws Exception {
        // Arrange
        when(requestService.updateRequest(eq(999L), any(RequestDetails.class)))
                .thenThrow(new RequestNotFound("Request not found"));

        // Act & Assert
        mockMvc.perform(put("/api/requests/update/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequestDetails)))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).updateRequest(eq(999L), any(RequestDetails.class));
    }

    @Test
    void testUpdateRequest_DepartmentNotFound() throws Exception {
        // Arrange
        when(requestService.updateRequest(eq(1L), any(RequestDetails.class)))
                .thenThrow(new com.example.department.DepartmentNotFound("Department not found"));

        // Act & Assert
        mockMvc.perform(put("/api/requests/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequestDetails)))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).updateRequest(eq(1L), any(RequestDetails.class));
    }

    // ==================== DELETE /{requestId} TESTS ====================

    @Test
    void testDeleteRequest_Success() throws Exception {
        // Arrange
        RequestSubmitResponse deleteResponse = new RequestSubmitResponse("Request deleted successfully");
        when(requestService.deleteRequest(1L)).thenReturn(deleteResponse);

        // Act & Assert
        mockMvc.perform(delete("/api/requests/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Request deleted successfully"));

        verify(requestService, times(1)).deleteRequest(1L);
    }

    @Test
    void testDeleteRequest_NotFound() throws Exception {
        // Arrange
        when(requestService.deleteRequest(999L))
                .thenThrow(new RequestNotFound("Request not found for deletion"));

        // Act & Assert
        mockMvc.perform(delete("/api/requests/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).deleteRequest(999L);
    }

    // ==================== GET /statistics TESTS ====================

    @Test
    void testGetRequestStatistics_Success() throws Exception {
        // Arrange
        RequestStatistics stats = new RequestStatistics();
        stats.setTotal(6L);
        stats.setSubmitted(2L);
        stats.setApproved(2L);
        stats.setRejected(1L);
        stats.setInProgress(1L);
        stats.setCompleted(0L);
        stats.setDeleted(0L);

        when(requestService.getRequestStatistics()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/api/requests/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(6))
                .andExpect(jsonPath("$.submitted").value(2))
                .andExpect(jsonPath("$.approved").value(2));

        verify(requestService, times(1)).getRequestStatistics();
    }

    @Test
    void testGetRequestStatistics_NoRequests() throws Exception {
        // Arrange
        when(requestService.getRequestStatistics())
                .thenThrow(new RequestNotFound("Requests not found"));

        // Act & Assert
        mockMvc.perform(get("/api/requests/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getRequestStatistics();
    }
}

