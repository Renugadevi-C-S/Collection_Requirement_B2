package com.example.collectionRequirements.event;

import com.example.DTOs.*;
import com.example.collectionRequirements.approval.Approval;
import com.example.collectionRequirements.request.*;
import com.example.department.Department;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private EventServiceImplementation eventService;

    private Event testEvent;
    private UserInfo testUser;
    private Request testRequest;
    private EventDetails testEventDetails;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        // Initialize test user
        testUser = new UserInfo();
        testUser.setCdsID("user123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");

        // Initialize test department
        testDepartment = new Department();
        testDepartment.setDepartmentId(1L);
        testDepartment.setDepartmentName("IT");

        // Initialize test request (Approved status)
        testRequest = new Request();
        testRequest.setRequestId(1L);
        testRequest.setRequestor(testUser);
        testRequest.setDepartment(testDepartment);
        testRequest.setRequestStatus("Approved");
        testRequest.setRequestDate(LocalDate.now());
        testRequest.setJustification("Training needed");
        testRequest.setTAN_Number("TAN123");
        testRequest.setNoOfParticipants(10);
        testRequest.setEvent(null);

        // Initialize test event
        testEvent = new Event();
        testEvent.setEventId(1L);
        testEvent.setEventName("Spring Boot Training");
        testEvent.setDescription("Advanced training");
        testEvent.setDuration(5);
        testEvent.setEventType("Training");
        testEvent.setFundingSource("Internal");
        testEvent.setStatus("Planned");
        testEvent.setCreatedBy(testUser);
        testEvent.setCreatedDate(LocalDate.now());
        testEvent.setParticipantsCount(10);
        testEvent.setRequests(Arrays.asList(testRequest));

        // Initialize test event details
        testEventDetails = new EventDetails();
        testEventDetails.setEventName("Spring Boot Training");
        testEventDetails.setDescription("Advanced training");
        testEventDetails.setDuration(5);
        testEventDetails.setEventType("Training");
        testEventDetails.setFundingSource("Internal");
        testEventDetails.setStatus("Planned");
        testEventDetails.setCreatedBy("user123");
        testEventDetails.setRequestIds(Arrays.asList(1L));
    }

    // ==================== CREATE EVENT TESTS ====================

    @Test
    void testCreateEvent_Success() throws EventException {
        // Arrange
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        EventSubmitResponse response = eventService.createEvent(testEventDetails);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("Event created successfully"));
        verify(userRepository, times(1)).findByCdsID("user123");
        verify(requestRepository, times(1)).findById(1L);
        verify(eventRepository, times(2)).save(any(Event.class)); // Saved twice: once for event, once after linking
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testCreateEvent_EmptyEventName() {
        // Arrange
        testEventDetails.setEventName("");

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.createEvent(testEventDetails));
        assertEquals("Event name cannot be empty.", exception.getMessage());
    }

    @Test
    void testCreateEvent_NullEventName() {
        // Arrange
        testEventDetails.setEventName(null);

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.createEvent(testEventDetails));
        assertEquals("Event name cannot be empty.", exception.getMessage());
    }

    @Test
    void testCreateEvent_EmptyCreatorId() {
        // Arrange
        testEventDetails.setCreatedBy("");

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.createEvent(testEventDetails));
        assertEquals("Creator ID is required.", exception.getMessage());
    }

    @Test
    void testCreateEvent_NullCreatorId() {
        // Arrange
        testEventDetails.setCreatedBy(null);

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.createEvent(testEventDetails));
        assertEquals("Creator ID is required.", exception.getMessage());
    }

    @Test
    void testCreateEvent_NoRequestIds() {
        // Arrange
        testEventDetails.setRequestIds(null);

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.createEvent(testEventDetails));
        assertEquals("At least one approved request must be linked to the event.", exception.getMessage());
    }

    @Test
    void testCreateEvent_EmptyRequestIds() {
        // Arrange
        testEventDetails.setRequestIds(Collections.emptyList());

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.createEvent(testEventDetails));
        assertEquals("At least one approved request must be linked to the event.", exception.getMessage());
    }

    @Test
    void testCreateEvent_UserNotFound() {
        // Arrange
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFound exception = assertThrows(UserNotFound.class,
            () -> eventService.createEvent(testEventDetails));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testCreateEvent_RequestNotFound() {
        // Arrange
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act & Assert
        assertThrows(RequestNotFound.class, () -> eventService.createEvent(testEventDetails));
    }

    @Test
    void testCreateEvent_RequestNotApproved() {
        // Arrange
        testRequest.setRequestStatus("Submitted");
        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act & Assert
        assertThrows(RequestNotApproved.class, () -> eventService.createEvent(testEventDetails));
    }

    @Test
    void testCreateEvent_RequestAlreadyLinked() {
        // Arrange
        Event anotherEvent = new Event();
        anotherEvent.setEventId(2L);
        testRequest.setEvent(anotherEvent);

        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act & Assert
        assertThrows(RequestAlreadyLinked.class, () -> eventService.createEvent(testEventDetails));
    }

    @Test
    void testCreateEvent_MultipleRequests() throws EventException {
        // Arrange
        Request request2 = new Request();
        request2.setRequestId(2L);
        request2.setRequestStatus("Approved");
        request2.setNoOfParticipants(5);
        request2.setRequestor(testUser);
        request2.setDepartment(testDepartment);

        testEventDetails.setRequestIds(Arrays.asList(1L, 2L));

        when(userRepository.findByCdsID("user123")).thenReturn(Optional.of(testUser));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(requestRepository.findById(2L)).thenReturn(Optional.of(request2));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        EventSubmitResponse response = eventService.createEvent(testEventDetails);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("2 linked request(s)"));
        assertTrue(response.getMessage().contains("15 total participants"));
        verify(requestRepository, times(2)).save(any(Request.class));
    }

    // ==================== GET EVENT BY ID TESTS ====================

    @Test
    void testGetEventById_Success() throws EventException {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act
        EventViewDetails result = eventService.getEventById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getEventId());
        assertEquals("Spring Boot Training", result.getEventName());
        assertEquals("Planned", result.getStatus());
        assertEquals("user123", result.getCreatedBy());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void testGetEventById_NotFound() {
        // Arrange
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFound.class, () -> eventService.getEventById(999L));
        verify(eventRepository, times(1)).findById(999L);
    }

    // ==================== GET EVENTS BY CDS ID TESTS ====================

    @Test
    void testGetEventsByCdsID_Success() throws EventException {
        // Arrange
        List<Event> events = Arrays.asList(testEvent);
        when(eventRepository.findByCreatedBy_CdsID("user123")).thenReturn(events);

        // Act
        List<EventViewDetails> result = eventService.getEventsByCdsID("user123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Spring Boot Training", result.get(0).getEventName());
        verify(eventRepository, times(1)).findByCreatedBy_CdsID("user123");
    }

    @Test
    void testGetEventsByCdsID_NoEventsFound() {
        // Arrange
        when(eventRepository.findByCreatedBy_CdsID("user123")).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(EventNotFound.class, () -> eventService.getEventsByCdsID("user123"));
        verify(eventRepository, times(1)).findByCreatedBy_CdsID("user123");
    }

    // ==================== GET ALL EVENTS TESTS ====================

    @Test
    void testGetAllEvents_Success() throws EventException {
        // Arrange
        List<Event> events = Arrays.asList(testEvent);
        when(eventRepository.findAll()).thenReturn(events);

        // Act
        List<EventViewDetails> result = eventService.getAllEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEvents_EmptyList() throws EventException {
        // Arrange
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<EventViewDetails> result = eventService.getAllEvents();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventRepository, times(1)).findAll();
    }

    // ==================== GET EVENTS BY STATUS TESTS ====================

    @Test
    void testGetEventsByStatus_Success() throws EventException {
        // Arrange
        List<Event> events = Arrays.asList(testEvent);
        when(eventRepository.findByStatus("Planned")).thenReturn(events);

        // Act
        List<EventViewDetails> result = eventService.getEventsByStatus("Planned");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Planned", result.get(0).getStatus());
        verify(eventRepository, times(1)).findByStatus("Planned");
    }

    @Test
    void testGetEventsByStatus_EmptyStatus() {
        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.getEventsByStatus(""));
        assertEquals("Event status cannot be empty", exception.getMessage());
    }

    @Test
    void testGetEventsByStatus_NullStatus() {
        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.getEventsByStatus(null));
        assertEquals("Event status cannot be empty", exception.getMessage());
    }

    @Test
    void testGetEventsByStatus_NotFound() {
        // Arrange
        when(eventRepository.findByStatus("Completed")).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(EventNotFound.class, () -> eventService.getEventsByStatus("Completed"));
    }

    // ==================== GET EVENTS BY TYPE TESTS ====================

    @Test
    void testGetEventsByType_Success() throws EventException {
        // Arrange
        List<Event> events = Arrays.asList(testEvent);
        when(eventRepository.findByEventType("Training")).thenReturn(events);

        // Act
        List<EventViewDetails> result = eventService.getEventsByType("Training");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Training", result.get(0).getEventType());
        verify(eventRepository, times(1)).findByEventType("Training");
    }

    @Test
    void testGetEventsByType_EmptyType() {
        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.getEventsByType(""));
        assertEquals("Event type cannot be empty", exception.getMessage());
    }

    @Test
    void testGetEventsByType_NullType() {
        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.getEventsByType(null));
        assertEquals("Event type cannot be empty", exception.getMessage());
    }

    @Test
    void testGetEventsByType_NotFound() {
        // Arrange
        when(eventRepository.findByEventType("Workshop")).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(EventNotFound.class, () -> eventService.getEventsByType("Workshop"));
    }

    // ==================== EDIT EVENT TESTS ====================

    @Test
    void testEditEvent_Success() throws EventException, RequestException {
        // Arrange
        EventDetails updateDetails = new EventDetails();
        updateDetails.setEventName("Updated Training");
        updateDetails.setDescription("Updated description");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        EventSubmitResponse response = eventService.editEvent(1L, updateDetails);

        // Assert
        assertNotNull(response);
        assertEquals("Event updated successfully", response.getMessage());
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testEditEvent_NotFound() {
        // Arrange
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFound.class, () -> eventService.editEvent(999L, testEventDetails));
        verify(eventRepository, times(1)).findById(999L);
    }

    @Test
    void testEditEvent_StatusDeleted() {
        // Arrange
        testEventDetails.setStatus("Deleted");
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.editEvent(1L, testEventDetails));
        assertTrue(exception.getMessage().contains("Cannot set status to 'Deleted'"));
    }

    @Test
    void testEditEvent_CompleteEvent() throws EventException, RequestException {
        // Arrange
        testRequest.setEvent(testEvent);
        testEvent.setRequests(Arrays.asList(testRequest));

        EventDetails completeDetails = new EventDetails();
        completeDetails.setStatus("Completed");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        EventSubmitResponse response = eventService.editEvent(1L, completeDetails);

        // Assert
        assertNotNull(response);
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testEditEvent_CancelEvent() throws EventException, RequestException {
        // Arrange
        testRequest.setEvent(testEvent);
        testEvent.setRequests(Arrays.asList(testRequest));

        EventDetails cancelDetails = new EventDetails();
        cancelDetails.setStatus("Cancelled");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        EventSubmitResponse response = eventService.editEvent(1L, cancelDetails);

        // Assert
        assertNotNull(response);
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testEditEvent_CannotCompleteCancelledEvent() {
        // Arrange
        testEvent.setStatus("Cancelled");
        EventDetails completeDetails = new EventDetails();
        completeDetails.setStatus("Completed");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.editEvent(1L, completeDetails));
        assertTrue(exception.getMessage().contains("Cannot complete a cancelled event"));
    }

    @Test
    void testEditEvent_CannotCancelCompletedEvent() {
        // Arrange
        testEvent.setStatus("Completed");
        EventDetails cancelDetails = new EventDetails();
        cancelDetails.setStatus("Cancelled");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.editEvent(1L, cancelDetails));
        assertTrue(exception.getMessage().contains("Cannot cancel a completed event"));
    }

    @Test
    void testEditEvent_EmptyRequestIds() {
        // Arrange
        EventDetails updateDetails = new EventDetails();
        updateDetails.setRequestIds(Collections.emptyList());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act & Assert
        EventException exception = assertThrows(EventException.class,
            () -> eventService.editEvent(1L, updateDetails));
        assertTrue(exception.getMessage().contains("At least one approved request must be linked"));
    }

    // ==================== DELETE EVENT TESTS ====================

    @Test
    void testDeleteEvent_Success() throws EventException {
        // Arrange
        testRequest.setEvent(testEvent);
        testEvent.setRequests(Arrays.asList(testRequest));
        testEvent.setStatus("Planned");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        EventSubmitResponse response = eventService.deleteEvent(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Event deleted successfully", response.getMessage());
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testDeleteEvent_NotFound() {
        // Arrange
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFound.class, () -> eventService.deleteEvent(999L));
        verify(eventRepository, times(1)).findById(999L);
    }

    @Test
    void testDeleteEvent_InProgressStatus() throws EventException {
        // Arrange
        testRequest.setEvent(testEvent);
        testEvent.setRequests(Arrays.asList(testRequest));
        testEvent.setStatus("In Progress");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        EventSubmitResponse response = eventService.deleteEvent(1L);

        // Assert
        assertNotNull(response);
        verify(requestRepository, times(1)).save(any(Request.class)); // Request should be unlinked
    }

    @Test
    void testDeleteEvent_CompletedStatus() throws EventException {
        // Arrange
        testEvent.setStatus("Completed");
        testEvent.setRequests(Collections.emptyList());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        EventSubmitResponse response = eventService.deleteEvent(1L);

        // Assert
        assertNotNull(response);
        verify(requestRepository, never()).save(any(Request.class)); // No unlinking for completed events
    }

    // ==================== GET AVAILABLE REQUESTS TESTS ====================

    @Test
    void testGetAvailableRequestsForEvent_Success() throws EventException {
        // Arrange
        List<Request> approvedRequests = Arrays.asList(testRequest);
        when(requestRepository.findByRequestStatusAndEventIsNull("Approved")).thenReturn(approvedRequests);

        // Act
        List<AvailableRequest> result = eventService.getAvailableRequestsForEvent();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestRepository, times(1)).findByRequestStatusAndEventIsNull("Approved");
    }

    @Test
    void testGetAvailableRequestsForEvent_EmptyList() throws EventException {
        // Arrange
        when(requestRepository.findByRequestStatusAndEventIsNull("Approved")).thenReturn(Collections.emptyList());

        // Act
        List<AvailableRequest> result = eventService.getAvailableRequestsForEvent();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAvailableRequestsForEventEdit_WithEventId() throws EventException {
        // Arrange
        List<Request> availableRequests = Arrays.asList(testRequest);
        when(requestRepository.findAvailableRequestsForEventEdit(1L)).thenReturn(availableRequests);

        // Act
        List<AvailableRequest> result = eventService.getAvailableRequestsForEvent(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestRepository, times(1)).findAvailableRequestsForEventEdit(1L);
    }

    @Test
    void testGetAvailableRequestsForEventEdit_NullEventId() throws EventException {
        // Arrange
        List<Request> approvedRequests = Arrays.asList(testRequest);
        when(requestRepository.findByRequestStatusAndEventIsNull("Approved")).thenReturn(approvedRequests);

        // Act
        List<AvailableRequest> result = eventService.getAvailableRequestsForEvent(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestRepository, times(1)).findByRequestStatusAndEventIsNull("Approved");
    }

    // ==================== GET EVENT STATISTICS TESTS ====================

    @Test
    void testGetEventStatistics_Success() {
        // Arrange
        Event planned = createEventWithStatus("Planned");
        Event inProgress = createEventWithStatus("In-Progress");
        Event completed = createEventWithStatus("Completed");
        Event cancelled = createEventWithStatus("Cancelled");
        Event deleted = createEventWithStatus("Deleted");

        List<Event> allEvents = Arrays.asList(planned, inProgress, completed, cancelled, deleted);
        when(eventRepository.findAll()).thenReturn(allEvents);

        // Act
        EventStatistics stats = eventService.getEventStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(5L, stats.getTotal());
        assertEquals(1L, stats.getPlanned());
        assertEquals(1L, stats.getInProgress());
        assertEquals(1L, stats.getCompleted());
        assertEquals(1L, stats.getCancelled());
        assertEquals(1L, stats.getDeleted());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testGetEventStatistics_EmptyList() {
        // Arrange
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        EventStatistics stats = eventService.getEventStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(0L, stats.getTotal());
        assertEquals(0L, stats.getPlanned());
        assertEquals(0L, stats.getInProgress());
    }

    @Test
    void testGetEventStatistics_MultipleOfSameStatus() {
        // Arrange
        Event planned1 = createEventWithStatus("Planned");
        Event planned2 = createEventWithStatus("Planned");
        Event completed = createEventWithStatus("Completed");

        List<Event> allEvents = Arrays.asList(planned1, planned2, completed);
        when(eventRepository.findAll()).thenReturn(allEvents);

        // Act
        EventStatistics stats = eventService.getEventStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(3L, stats.getTotal());
        assertEquals(2L, stats.getPlanned());
        assertEquals(1L, stats.getCompleted());
        assertEquals(0L, stats.getCancelled());
    }

    // ==================== HELPER METHODS ====================

    private Event createEventWithStatus(String status) {
        Event event = new Event();
        event.setEventId(System.nanoTime());
        event.setEventName("Test Event");
        event.setStatus(status);
        event.setCreatedBy(testUser);
        event.setCreatedDate(LocalDate.now());
        event.setEventType("Training");
        return event;
    }
}

