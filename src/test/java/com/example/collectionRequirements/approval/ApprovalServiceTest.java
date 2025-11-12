package com.example.collectionRequirements.approval;

import com.example.DTOs.ApprovalSubmissionResponse;
import com.example.DTOs.NewApprovalDetails;
import com.example.collectionRequirements.request.Request;
import com.example.collectionRequirements.request.RequestNotFound;
import com.example.collectionRequirements.request.RequestRepository;
import com.example.department.Department;
import com.example.user.UserInfo;
import com.example.user.UserNotFound;
import com.example.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApprovalServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApprovalRepository approvalRepository;

    @InjectMocks
    private ApprovalServiceImplementation approvalService;

    private Request testRequest;
    private UserInfo testUser;
    private UserInfo approverUser;
    private Approval testApproval;
    private NewApprovalDetails testApprovalDetails;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        // Initialize test user (requestor)
        testUser = new UserInfo();
        testUser.setCdsID("user123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");

        // Initialize approver user
        approverUser = new UserInfo();
        approverUser.setCdsID("approver456");
        approverUser.setFirstName("Jane");
        approverUser.setLastName("Smith");
        approverUser.setEmail("jane@example.com");

        // Initialize test department
        testDepartment = new Department();
        testDepartment.setDepartmentId(1L);
        testDepartment.setDepartmentName("IT");

        // Initialize test request (without approval initially)
        testRequest = new Request();
        testRequest.setRequestId(1L);
        testRequest.setRequestor(testUser);
        testRequest.setDepartment(testDepartment);
        testRequest.setRequestStatus("Submitted");
        testRequest.setRequestDate(LocalDate.now());
        testRequest.setJustification("Training needed");
        testRequest.setTAN_Number("TAN123");
        testRequest.setNoOfParticipants(10);
        testRequest.setApproval(null); // No approval initially

        // Initialize test approval
        testApproval = new Approval();
        testApproval.setApprovalId(1L);
        testApproval.setRequest(testRequest);
        testApproval.setApprovedBy(approverUser);
        testApproval.setApprovalStatus("Approved");
        testApproval.setApprovalNotes("Looks good");
        testApproval.setApprovalDate(LocalDate.now());

        // Initialize approval details
        testApprovalDetails = new NewApprovalDetails();
        testApprovalDetails.setRequestId(1L);
        testApprovalDetails.setApprovedBy("approver456");
        testApprovalDetails.setApprovalStatus("Approved");
        testApprovalDetails.setApprovalNotes("Looks good");
    }

    // ==================== ADD APPROVAL (NEW) TESTS ====================

    @Test
    void testAddApprovalWithRequestId_NewApproval_Success() throws Exception {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));
        when(approvalRepository.save(any(Approval.class))).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        ApprovalSubmissionResponse response = approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("Approved request with ID 1 successfully"));

        // Verify interactions
        verify(requestRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByCdsID("approver456");
        verify(approvalRepository, times(1)).save(any(Approval.class));
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testAddApprovalWithRequestId_NewApproval_CheckApprovalDetails() throws Exception {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));

        ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
        when(approvalRepository.save(approvalCaptor.capture())).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert - Verify the approval object that was saved
        Approval savedApproval = approvalCaptor.getValue();
        assertNotNull(savedApproval);
        assertEquals("Approved", savedApproval.getApprovalStatus());
        assertEquals("Looks good", savedApproval.getApprovalNotes());
        assertEquals(approverUser, savedApproval.getApprovedBy());
        assertEquals(testRequest, savedApproval.getRequest());
        assertEquals(LocalDate.now(), savedApproval.getApprovalDate());
    }

    @Test
    void testAddApprovalWithRequestId_NewApproval_UpdatesRequestStatus() throws Exception {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));
        when(approvalRepository.save(any(Approval.class))).thenReturn(testApproval);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(requestRepository.save(requestCaptor.capture())).thenReturn(testRequest);

        // Act
        approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert - Verify the request was updated
        Request savedRequest = requestCaptor.getValue();
        assertNotNull(savedRequest);
        assertEquals("Approved", savedRequest.getRequestStatus());
        assertNotNull(savedRequest.getApproval());
    }

    @Test
    void testAddApprovalWithRequestId_RequestNotFound() {
        // Arrange
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        NewApprovalDetails invalidDetails = new NewApprovalDetails();
        invalidDetails.setRequestId(999L);
        invalidDetails.setApprovedBy("approver456");
        invalidDetails.setApprovalStatus("Approved");

        // Act & Assert
        RequestNotFound exception = assertThrows(RequestNotFound.class,
            () -> approvalService.addApprovalWithRequestId(invalidDetails));

        assertEquals("Request not found.", exception.getMessage());
        verify(requestRepository, times(1)).findById(999L);
        verify(userRepository, never()).findByCdsID(anyString());
        verify(approvalRepository, never()).save(any(Approval.class));
    }

    @Test
    void testAddApprovalWithRequestId_UserNotFound() {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("invalidUser")).thenReturn(Optional.empty());

        NewApprovalDetails invalidDetails = new NewApprovalDetails();
        invalidDetails.setRequestId(1L);
        invalidDetails.setApprovedBy("invalidUser");
        invalidDetails.setApprovalStatus("Approved");

        // Act & Assert
        UserNotFound exception = assertThrows(UserNotFound.class,
            () -> approvalService.addApprovalWithRequestId(invalidDetails));

        assertTrue(exception.getMessage().contains("invalidUser"));
        assertTrue(exception.getMessage().contains("not found"));
        verify(requestRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByCdsID("invalidUser");
        verify(approvalRepository, never()).save(any(Approval.class));
    }

    // ==================== UPDATE EXISTING APPROVAL TESTS ====================

    @Test
    void testAddApprovalWithRequestId_UpdateExistingApproval_Success() throws Exception {
        // Arrange - Request already has an approval
        testRequest.setApproval(testApproval);

        NewApprovalDetails updateDetails = new NewApprovalDetails();
        updateDetails.setRequestId(1L);
        updateDetails.setApprovedBy("approver456");
        updateDetails.setApprovalStatus("Rejected");
        updateDetails.setApprovalNotes("Needs more information");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));
        when(approvalRepository.save(any(Approval.class))).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        ApprovalSubmissionResponse response = approvalService.addApprovalWithRequestId(updateDetails);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("Rejected request with ID 1 successfully"));

        verify(requestRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByCdsID("approver456");
        verify(approvalRepository, times(1)).save(any(Approval.class));
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testAddApprovalWithRequestId_UpdateExistingApproval_CheckUpdatedFields() throws Exception {
        // Arrange - Request already has an approval
        testRequest.setApproval(testApproval);

        NewApprovalDetails updateDetails = new NewApprovalDetails();
        updateDetails.setRequestId(1L);
        updateDetails.setApprovedBy("approver456");
        updateDetails.setApprovalStatus("Rejected");
        updateDetails.setApprovalNotes("Insufficient justification");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));

        ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
        when(approvalRepository.save(approvalCaptor.capture())).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        approvalService.addApprovalWithRequestId(updateDetails);

        // Assert - Verify the existing approval was updated
        Approval updatedApproval = approvalCaptor.getValue();
        assertNotNull(updatedApproval);
        assertEquals("Rejected", updatedApproval.getApprovalStatus());
        assertEquals("Insufficient justification", updatedApproval.getApprovalNotes());
        assertEquals(LocalDate.now(), updatedApproval.getApprovalDate());
        assertEquals(approverUser, updatedApproval.getApprovedBy());
    }

    // ==================== DIFFERENT APPROVAL STATUSES TESTS ====================

    @Test
    void testAddApprovalWithRequestId_ApprovedStatus() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalStatus("Approved");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));
        when(approvalRepository.save(any(Approval.class))).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        ApprovalSubmissionResponse response = approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("Approved"));
    }

    @Test
    void testAddApprovalWithRequestId_RejectedStatus() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalStatus("Rejected");
        testApprovalDetails.setApprovalNotes("Does not meet requirements");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));
        when(approvalRepository.save(any(Approval.class))).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        ApprovalSubmissionResponse response = approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("Rejected"));
    }

    @Test
    void testAddApprovalWithRequestId_WithApprovalNotes() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalNotes("Please proceed with training");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));

        ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
        when(approvalRepository.save(approvalCaptor.capture())).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert
        Approval savedApproval = approvalCaptor.getValue();
        assertEquals("Please proceed with training", savedApproval.getApprovalNotes());
    }

    @Test
    void testAddApprovalWithRequestId_WithoutApprovalNotes() throws Exception {
        // Arrange
        testApprovalDetails.setApprovalNotes(null);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));

        ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
        when(approvalRepository.save(approvalCaptor.capture())).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert
        Approval savedApproval = approvalCaptor.getValue();
        assertNull(savedApproval.getApprovalNotes());
    }

    // ==================== APPROVAL DATE TESTS ====================

    @Test
    void testAddApprovalWithRequestId_SetsApprovalDateToToday() throws Exception {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));

        ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
        when(approvalRepository.save(approvalCaptor.capture())).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert
        Approval savedApproval = approvalCaptor.getValue();
        assertEquals(LocalDate.now(), savedApproval.getApprovalDate());
    }

    // ==================== RESPONSE MESSAGE TESTS ====================

    @Test
    void testAddApprovalWithRequestId_ResponseMessageFormat() throws Exception {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));
        when(approvalRepository.save(any(Approval.class))).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        ApprovalSubmissionResponse response = approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert
        String expectedMessage = "Approved request with ID 1 successfully.";
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void testAddApprovalWithRequestId_DifferentRequestId_ResponseMessage() throws Exception {
        // Arrange
        testApprovalDetails.setRequestId(42L);
        testRequest.setRequestId(42L);

        when(requestRepository.findById(42L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));
        when(approvalRepository.save(any(Approval.class))).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        ApprovalSubmissionResponse response = approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert
        assertTrue(response.getMessage().contains("request with ID 42"));
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    void testAddApprovalWithRequestId_DifferentApprover() throws Exception {
        // Arrange
        UserInfo differentApprover = new UserInfo();
        differentApprover.setCdsID("approver789");
        differentApprover.setFirstName("Bob");
        differentApprover.setLastName("Johnson");

        testApprovalDetails.setApprovedBy("approver789");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver789")).thenReturn(Optional.of(differentApprover));

        ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
        when(approvalRepository.save(approvalCaptor.capture())).thenReturn(testApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert
        Approval savedApproval = approvalCaptor.getValue();
        assertEquals(differentApprover, savedApproval.getApprovedBy());
        assertEquals("approver789", savedApproval.getApprovedBy().getCdsID());
    }

    @Test
    void testAddApprovalWithRequestId_MultipleUpdates_KeepsSameApprovalObject() throws Exception {
        // Arrange - Request already has an approval
        Approval existingApproval = new Approval();
        existingApproval.setApprovalId(5L);
        existingApproval.setRequest(testRequest);
        existingApproval.setApprovedBy(approverUser);
        existingApproval.setApprovalStatus("Approved");
        existingApproval.setApprovalNotes("Initial approval");
        existingApproval.setApprovalDate(LocalDate.now().minusDays(1));

        testRequest.setApproval(existingApproval);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("approver456")).thenReturn(Optional.of(approverUser));

        ArgumentCaptor<Approval> approvalCaptor = ArgumentCaptor.forClass(Approval.class);
        when(approvalRepository.save(approvalCaptor.capture())).thenReturn(existingApproval);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        approvalService.addApprovalWithRequestId(testApprovalDetails);

        // Assert - Should update the existing approval, not create a new one
        Approval savedApproval = approvalCaptor.getValue();
        assertEquals(5L, savedApproval.getApprovalId()); // Same approval ID
        assertEquals("Approved", savedApproval.getApprovalStatus()); // Updated status
        assertEquals("Looks good", savedApproval.getApprovalNotes()); // Updated notes
    }
}

