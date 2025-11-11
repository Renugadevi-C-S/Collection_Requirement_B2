package com.example.collectionRequirements.request;

import com.example.DTOs.*;
import com.example.collectionRequirements.approval.Approval;
import com.example.collectionRequirements.event.Event;
import com.example.department.Department;
import com.example.department.DepartmentException;
import com.example.department.DepartmentNotFound;
import com.example.department.DepartmentRepository;
import com.example.user.UserException;
import com.example.user.UserInfo;
import com.example.user.UserNotFound;
import com.example.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private RequestServiceImplementation requestService;

    private Request testRequest;
    private UserInfo testUser;
    private Department testDepartment;
    private RequestDetails testRequestDetails;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testUser = new UserInfo();
        testUser.setCdsID("user123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");

        testDepartment = new Department();
        testDepartment.setDepartmentId(1L);
        testDepartment.setDepartmentName("IT");

        testRequest = new Request();
        testRequest.setRequestId(1L);
        testRequest.setRequestor(testUser);
        testRequest.setDepartment(testDepartment);
        testRequest.setRequestStatus("Submitted");
        testRequest.setRequestDate(LocalDate.now());
        testRequest.setJustification("Test justification");
        testRequest.setTAN_Number("TAN123");
        testRequest.setCurriculumLink("http://curriculum.com");
        testRequest.setNoOfParticipants(5);
        testRequest.setGroupRequest(false);
        testRequest.setRequestedParticipants(Arrays.asList(testUser));

        testRequestDetails = new RequestDetails();
        testRequestDetails.setRequestorId("user123");
        testRequestDetails.setDepartment("IT");
        testRequestDetails.setJustification("Test justification");
        testRequestDetails.setTanNo("TAN123");
        testRequestDetails.setCurriculum("http://curriculum.com");
        testRequestDetails.setNoOfParticipants(5);
        testRequestDetails.setUsersCdsId(new String[]{"user123"});
    }

    // ==================== GET REQUEST BY ID TESTS ====================

    @Test
    void testGetRequestById_Success() throws RequestException {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));

        // Act
        RequestsViewDetails result = requestService.getRequestById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getRequestId());
        assertEquals("Submitted", result.getRequestStatus());
        assertEquals("IT", result.getDepartment());
        verify(requestRepository, times(1)).findById(1L);
        // userRepository.findByCdsID is called once in convertToRequestsViewDetails for requestor lookup
        verify(userRepository, times(1)).findByCdsID("user123");
    }

    @Test
    void testGetRequestById_NotFound() {
        // Arrange
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RequestNotFound.class, () -> requestService.getRequestById(999L));
        verify(requestRepository, times(1)).findById(999L);
    }

    // ==================== GET ALL REQUESTS TESTS ====================

    @Test
    void testGetAllRequests_Success() throws RequestException {
        // Arrange
        List<Request> requestList = Arrays.asList(testRequest);
        when(requestRepository.findAll()).thenReturn(requestList);
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));

        // Act
        List<RequestsViewDetails> result = requestService.getAllRequests();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Submitted", result.get(0).getRequestStatus());
        verify(requestRepository, times(1)).findAll();
        // userRepository.findByCdsID is called once per request in convertToRequestsViewDetails
        verify(userRepository, times(1)).findByCdsID("user123");
    }

    @Test
    void testGetAllRequests_EmptyList() {
        // Arrange
        when(requestRepository.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        assertThrows(RequestNotFound.class, () -> requestService.getAllRequests());
        verify(requestRepository, times(1)).findAll();
    }

    // ==================== GET REQUEST BY STATUS TESTS ====================

    @Test
    void testGetRequestByStatus_Success() throws RequestException {
        // Arrange
        List<Request> requestList = Arrays.asList(testRequest);
        when(requestRepository.findByRequestStatus("Submitted")).thenReturn(requestList);

        // Act
        List<Request> result = requestService.getRequestByStatus("Submitted");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Submitted", result.get(0).getRequestStatus());
        verify(requestRepository, times(1)).findByRequestStatus("Submitted");
    }

    @Test
    void testGetRequestByStatus_NoResults() throws RequestException {
        // Arrange
        when(requestRepository.findByRequestStatus("Approved")).thenReturn(Arrays.asList());

        // Act
        List<Request> result = requestService.getRequestByStatus("Approved");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestRepository, times(1)).findByRequestStatus("Approved");
    }

    // ==================== GET REQUEST BY CDS ID TESTS ====================

    @Test
    void testGetRequestByCdsId_Success() throws UserException, RequestException {
        // Arrange
        List<Request> requestList = Arrays.asList(testRequest);
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(requestRepository.findRequestsByRequestorCdsId("user123")).thenReturn(requestList);

        // Act
        List<RequestsViewDetails> result = requestService.getRequestByCdsId("user123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        // Called twice: once in getRequestByCdsId() and once in convertToRequestsViewDetails()
        verify(userRepository, times(2)).findByCdsID("user123");
        verify(requestRepository, times(1)).findRequestsByRequestorCdsId("user123");
    }

    @Test
    void testGetRequestByCdsId_UserNotFound() {
        // Arrange
        when(userRepository.findByCdsID("invalidUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFound.class, () -> requestService.getRequestByCdsId("invalidUser"));
        verify(userRepository, times(1)).findByCdsID("invalidUser");
    }

    @Test
    void testGetRequestByCdsId_RequestNotFound() {
        // Arrange
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(requestRepository.findRequestsByRequestorCdsId("user123")).thenReturn(Arrays.asList());

        // Act & Assert
        assertThrows(RequestNotFound.class, () -> requestService.getRequestByCdsId("user123"));
        verify(requestRepository, times(1)).findRequestsByRequestorCdsId("user123");
    }

    // ==================== SUBMIT NEW REQUEST TESTS ====================

    @Test
    void testSubmitNewRequest_Success() throws UserException, DepartmentException {
        // Arrange
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(departmentRepository.findByDepartmentNameIgnoreCase("IT")).thenReturn(testDepartment);
        when(userRepository.findByCdsIDIn(anyList())).thenReturn(Arrays.asList(testUser));
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        RequestSubmitResponse result = requestService.submitNewRequest(testRequestDetails);

        // Assert
        assertNotNull(result);
        assertEquals("New Request submitted successfully", result.getMessage());
        verify(userRepository, times(1)).findByCdsID("user123");
        verify(departmentRepository, times(1)).findByDepartmentNameIgnoreCase("IT");
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testSubmitNewRequest_UserNotFound() {
        // Arrange
        when(userRepository.findByCdsID("invalidUser")).thenReturn(Optional.empty());

        RequestDetails invalidDetails = new RequestDetails();
        invalidDetails.setRequestorId("invalidUser");
        invalidDetails.setDepartment("IT");

        // Act & Assert
        assertThrows(UserNotFound.class, () -> requestService.submitNewRequest(invalidDetails));
        verify(userRepository, times(1)).findByCdsID("invalidUser");
    }

    @Test
    void testSubmitNewRequest_DepartmentNotFound() {
        // Arrange
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(departmentRepository.findByDepartmentNameIgnoreCase("InvalidDept")).thenReturn(null);

        RequestDetails invalidDetails = new RequestDetails();
        invalidDetails.setRequestorId("user123");
        invalidDetails.setDepartment("InvalidDept");

        // Act & Assert
        assertThrows(DepartmentNotFound.class, () -> requestService.submitNewRequest(invalidDetails));
        verify(departmentRepository, times(1)).findByDepartmentNameIgnoreCase("InvalidDept");
    }

    @Test
    void testSubmitNewRequest_GroupRequest() throws UserException, DepartmentException {
        // Arrange
        RequestDetails groupRequestDetails = new RequestDetails();
        groupRequestDetails.setRequestorId("user123");
        groupRequestDetails.setDepartment("IT");
        groupRequestDetails.setNoOfParticipants(15);
        groupRequestDetails.setJustification("Group training");
        groupRequestDetails.setTanNo("TAN456");
        groupRequestDetails.setCurriculum("http://curriculum.com");
        groupRequestDetails.setUsersCdsId(new String[]{"user123"});

        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(departmentRepository.findByDepartmentNameIgnoreCase("IT")).thenReturn(testDepartment);
        when(userRepository.findByCdsIDIn(anyList())).thenReturn(Arrays.asList(testUser));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> {
            Request savedRequest = invocation.getArgument(0);
            assertTrue(savedRequest.getGroupRequest());
            return savedRequest;
        });

        // Act
        RequestSubmitResponse result = requestService.submitNewRequest(groupRequestDetails);

        // Assert
        assertNotNull(result);
        assertEquals("New Request submitted successfully", result.getMessage());
    }

    // ==================== UPDATE REQUEST TESTS ====================

    @Test
    void testUpdateRequest_Success() throws UserException, DepartmentException, RequestException {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(departmentRepository.findByDepartmentNameIgnoreCase("IT")).thenReturn(testDepartment);
        when(userRepository.findByCdsIDIn(anyList())).thenReturn(Arrays.asList(testUser));
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        // Act
        RequestSubmitResponse result = requestService.updateRequest(1L, testRequestDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Request updated successfully", result.getMessage());
        verify(requestRepository, times(1)).findById(1L);
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testUpdateRequest_NotFound() {
        // Arrange
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RequestNotFound.class, () -> requestService.updateRequest(999L, testRequestDetails));
        verify(requestRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateRequest_DepartmentNotFound() {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(departmentRepository.findByDepartmentNameIgnoreCase("InvalidDept")).thenReturn(null);

        RequestDetails invalidDetails = new RequestDetails();
        invalidDetails.setDepartment("InvalidDept");

        // Act & Assert
        assertThrows(DepartmentNotFound.class, () -> requestService.updateRequest(1L, invalidDetails));
    }

    @Test
    void testUpdateRequest_PartialUpdate() throws UserException, DepartmentException, RequestException {
        // Arrange
        RequestDetails partialDetails = new RequestDetails();
        partialDetails.setJustification("Updated justification");
        partialDetails.setNoOfParticipants(8);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> {
            Request updatedRequest = invocation.getArgument(0);
            assertEquals("Updated justification", updatedRequest.getJustification());
            assertEquals(8, updatedRequest.getNoOfParticipants());
            assertFalse(updatedRequest.getGroupRequest());
            return updatedRequest;
        });

        // Act
        RequestSubmitResponse result = requestService.updateRequest(1L, partialDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Request updated successfully", result.getMessage());
    }

    // ==================== DELETE REQUEST TESTS ====================

    @Test
    void testDeleteRequest_Success() throws RequestException {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> {
            Request deletedRequest = invocation.getArgument(0);
            assertEquals("Deleted", deletedRequest.getRequestStatus());
            return deletedRequest;
        });

        // Act
        RequestSubmitResponse result = requestService.deleteRequest(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Request deleted successfully", result.getMessage());
        verify(requestRepository, times(1)).findById(1L);
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testDeleteRequest_NotFound() {
        // Arrange
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RequestNotFound.class, () -> requestService.deleteRequest(999L));
        verify(requestRepository, times(1)).findById(999L);
    }

    // ==================== GET REQUEST STATISTICS TESTS ====================

    @Test
    void testGetRequestStatistics_Success() {
        // Arrange
        Request submitted = createRequestWithStatus("Submitted");
        Request approved = createRequestWithStatus("Approved");
        Request rejected = createRequestWithStatus("Rejected");
        Request inProgress = createRequestWithStatus("In-Progress");
        Request completed = createRequestWithStatus("Completed");
        Request deleted = createRequestWithStatus("Deleted");

        List<Request> allRequests = Arrays.asList(submitted, approved, rejected, inProgress, completed, deleted);
        when(requestRepository.findAll()).thenReturn(allRequests);

        // Act
        RequestStatistics stats = requestService.getRequestStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(6L, stats.getTotal());
        assertEquals(1L, stats.getSubmitted());
        assertEquals(1L, stats.getApproved());
        assertEquals(1L, stats.getRejected());
        assertEquals(1L, stats.getInProgress());
        assertEquals(1L, stats.getCompleted());
        assertEquals(1L, stats.getDeleted());
        verify(requestRepository, times(1)).findAll();
    }

    @Test
    void testGetRequestStatistics_EmptyList() {
        // Arrange
        when(requestRepository.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        assertThrows(RequestNotFound.class, () -> requestService.getRequestStatistics());
        verify(requestRepository, times(1)).findAll();
    }

    @Test
    void testGetRequestStatistics_MultipleOfSameStatus() {
        // Arrange
        Request submitted1 = createRequestWithStatus("Submitted");
        Request submitted2 = createRequestWithStatus("Submitted");
        Request approved = createRequestWithStatus("Approved");

        List<Request> allRequests = Arrays.asList(submitted1, submitted2, approved);
        when(requestRepository.findAll()).thenReturn(allRequests);

        // Act
        RequestStatistics stats = requestService.getRequestStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(3L, stats.getTotal());
        assertEquals(2L, stats.getSubmitted());
        assertEquals(1L, stats.getApproved());
        assertEquals(0L, stats.getRejected());
    }

    // ==================== HELPER METHODS ====================

    private Request createRequestWithStatus(String status) {
        Request request = new Request();
        request.setRequestId(System.nanoTime());
        request.setRequestStatus(status);
        request.setRequestor(testUser);
        request.setDepartment(testDepartment);
        request.setRequestDate(LocalDate.now());
        request.setJustification("Test");
        request.setNoOfParticipants(1);
        return request;
    }
}

