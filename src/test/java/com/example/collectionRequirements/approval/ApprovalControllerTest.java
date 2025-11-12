package com.example.collectionRequirements.approval;

import com.example.DTOs.ApprovalSubmissionResponse;
import com.example.DTOs.NewApprovalDetails;
import com.example.collectionRequirements.request.RequestNotFound;
import com.example.user.UserNotFound;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApprovalController.class)
public class ApprovalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApprovalService approvalService;

    @Autowired
    private ObjectMapper objectMapper;

    private NewApprovalDetails testApprovalDetails;
    private ApprovalSubmissionResponse testResponse;

    @BeforeEach
    void setUp() {
        // Initialize test approval details
        testApprovalDetails = new NewApprovalDetails();
        testApprovalDetails.setRequestId(1L);
        testApprovalDetails.setApprovedBy("approver456");
        testApprovalDetails.setApprovalStatus("Approved");
        testApprovalDetails.setApprovalNotes("Looks good");

        // Initialize response
        testResponse = new ApprovalSubmissionResponse("Approved request with ID 1 successfully.");
    }

    // ==================== POST /submit-approval TESTS ====================

    @Test
    void testAddApproval_Success() throws Exception {
        // Arrange
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Approved request with ID 1 successfully."));

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_ApprovedStatus() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalStatus("Approved");
        ApprovalSubmissionResponse approvedResponse = new ApprovalSubmissionResponse(
                "Approved request with ID 1 successfully.");
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(approvedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Approved request with ID 1 successfully."));

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_RejectedStatus() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalStatus("Rejected");
        testApprovalDetails.setApprovalNotes("Does not meet requirements");
        ApprovalSubmissionResponse rejectedResponse = new ApprovalSubmissionResponse(
                "Rejected request with ID 1 successfully.");
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(rejectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rejected request with ID 1 successfully."));

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_WithApprovalNotes() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalNotes("Please proceed with training");
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk());

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_WithoutApprovalNotes() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalNotes(null);
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk());

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_RequestNotFound() throws Exception {
        // Arrange
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenThrow(new RequestNotFound("Request not found."));

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isNotFound()); // RequestNotFound is handled by global error handler returning 404

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_UserNotFound() throws Exception {
        // Arrange
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenThrow(new UserNotFound("User with CDS ID approver456 not found."));

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isNotFound()); // UserNotFound is handled by global error handler returning 404

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_DifferentRequestId() throws Exception {
        // Arrange
        testApprovalDetails.setRequestId(42L);
        ApprovalSubmissionResponse customResponse = new ApprovalSubmissionResponse(
                "Approved request with ID 42 successfully.");
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(customResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Approved request with ID 42 successfully."));

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_DifferentApprover() throws Exception {
        // Arrange
        testApprovalDetails.setApprovedBy("approver789");
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk());

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_UpdateExistingApproval() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalStatus("Rejected");
        testApprovalDetails.setApprovalNotes("Needs more information");
        ApprovalSubmissionResponse updateResponse = new ApprovalSubmissionResponse(
                "Rejected request with ID 1 successfully.");
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(updateResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rejected request with ID 1 successfully."));

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_InvalidJSON() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isInternalServerError()); // HttpMessageNotReadableException returns 500

        verify(approvalService, never()).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_EmptyRequestBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk()); // Controller doesn't validate, service will handle

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_LongApprovalNotes() throws Exception {
        // Arrange
        String longNotes = "This is a very long approval note. ".repeat(50);
        testApprovalDetails.setApprovalNotes(longNotes);
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk());

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_SpecialCharactersInNotes() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalNotes("Special chars: @#$%^&*()_+-=[]{}|;':\",./<>?");
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk());

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }

    @Test
    void testAddApproval_MultilineApprovalNotes() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalNotes("Line 1\nLine 2\nLine 3");
        when(approvalService.addApprovalWithRequestId(any(NewApprovalDetails.class)))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/approvals/submit-approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDetails)))
                .andExpect(status().isOk());

        verify(approvalService, times(1)).addApprovalWithRequestId(any(NewApprovalDetails.class));
    }
}

