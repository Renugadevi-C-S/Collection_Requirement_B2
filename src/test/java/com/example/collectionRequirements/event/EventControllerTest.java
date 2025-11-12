package com.example.collectionRequirements.event;

import com.example.DTOs.*;
import com.example.collectionRequirements.request.RequestException;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private EventViewDetails testEventView;
    private EventDetails testEventDetails;
    private EventSubmitResponse testResponse;
    private AvailableRequest testAvailableRequest;
    private EventStatistics testStatistics;

    @BeforeEach
    void setUp() {
        // Initialize test event view
        testEventView = new EventViewDetails();
        testEventView.setEventId(1L);
        testEventView.setEventName("Spring Boot Training");
        testEventView.setDescription("Advanced training");
        testEventView.setDuration(5);
        testEventView.setEventType("Training");
        testEventView.setFundingSource("Internal");
        testEventView.setStatus("Planned");
        testEventView.setCreatedBy("user123");
        testEventView.setCreatedDate(LocalDate.now());
        testEventView.setParticipantsCount(10);

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

        // Initialize response
        testResponse = new EventSubmitResponse("Operation successful");

        // Initialize available request
        testAvailableRequest = new AvailableRequest();
        testAvailableRequest.setRequestId(1L);
        testAvailableRequest.setTanNumber("TAN123");
        testAvailableRequest.setNoOfParticipants(10);
        testAvailableRequest.setRequestDate(LocalDate.now());
        testAvailableRequest.setJustification("Training needed");
        testAvailableRequest.setRequestedBy("user123");
        testAvailableRequest.setDepartment("IT");

        // Initialize statistics
        testStatistics = new EventStatistics();
        testStatistics.setTotal(5L);
        testStatistics.setPlanned(2L);
        testStatistics.setInProgress(1L);
        testStatistics.setCompleted(1L);
        testStatistics.setCancelled(1L);
        testStatistics.setDeleted(0L);
    }

    // ==================== POST /create TESTS ====================

    @Test
    void testCreateEvent_Success() throws Exception {
        // Arrange
        EventSubmitResponse createResponse = new EventSubmitResponse(
            "Event created successfully with 1 linked request(s) and 10 total participants.");
        when(eventService.createEvent(any(EventDetails.class))).thenReturn(createResponse);

        // Act & Assert
        mockMvc.perform(post("/api/events/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEventDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(createResponse.getMessage()));

        verify(eventService, times(1)).createEvent(any(EventDetails.class));
    }

    @Test
    void testCreateEvent_EmptyEventName() throws Exception {
        // Arrange
        when(eventService.createEvent(any(EventDetails.class)))
                .thenThrow(new EventException("Event name cannot be empty."));

        // Act & Assert
        mockMvc.perform(post("/api/events/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEventDetails)))
                .andExpect(status().isBadRequest());

        verify(eventService, times(1)).createEvent(any(EventDetails.class));
    }

    @Test
    void testCreateEvent_UserNotFound() throws Exception {
        // Arrange
        when(eventService.createEvent(any(EventDetails.class)))
                .thenThrow(new com.example.user.UserNotFound("User not found"));

        // Act & Assert
        mockMvc.perform(post("/api/events/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEventDetails)))
                .andExpect(status().isNotFound()); // UserNotFound is handled by global error handler returning 404

        verify(eventService, times(1)).createEvent(any(EventDetails.class));
    }

    @Test
    void testCreateEvent_RequestNotApproved() throws Exception {
        // Arrange
        when(eventService.createEvent(any(EventDetails.class)))
                .thenThrow(new com.example.collectionRequirements.request.RequestNotApproved("Request not approved"));

        // Act & Assert
        mockMvc.perform(post("/api/events/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEventDetails)))
                .andExpect(status().isBadRequest()); // RequestNotApproved is handled by global error handler returning 400

        verify(eventService, times(1)).createEvent(any(EventDetails.class));
    }

    // ==================== GET /all TESTS ====================

    @Test
    void testGetAllEvents_Success() throws Exception {
        // Arrange
        List<EventViewDetails> eventList = Arrays.asList(testEventView);
        when(eventService.getAllEvents()).thenReturn(eventList);

        // Act & Assert
        mockMvc.perform(get("/api/events/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(1))
                .andExpect(jsonPath("$[0].eventName").value("Spring Boot Training"))
                .andExpect(jsonPath("$[0].status").value("Planned"));

        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    void testGetAllEvents_EmptyList() throws Exception {
        // Arrange
        when(eventService.getAllEvents()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/events/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    void testGetAllEvents_Exception() throws Exception {
        // Arrange
        when(eventService.getAllEvents())
                .thenThrow(new EventException("Failed to retrieve events"));

        // Act & Assert
        mockMvc.perform(get("/api/events/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(eventService, times(1)).getAllEvents();
    }

    // ==================== GET /{eventId} TESTS ====================

    @Test
    void testGetEventById_Success() throws Exception {
        // Arrange
        when(eventService.getEventById(1L)).thenReturn(testEventView);

        // Act & Assert
        mockMvc.perform(get("/api/events/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(1))
                .andExpect(jsonPath("$.eventName").value("Spring Boot Training"))
                .andExpect(jsonPath("$.status").value("Planned"))
                .andExpect(jsonPath("$.createdBy").value("user123"));

        verify(eventService, times(1)).getEventById(1L);
    }

    @Test
    void testGetEventById_NotFound() throws Exception {
        // Arrange
        when(eventService.getEventById(999L))
                .thenThrow(new EventNotFound("Event not found"));

        // Act & Assert
        mockMvc.perform(get("/api/events/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getEventById(999L);
    }

    // ==================== GET /creator/{cdsID} TESTS ====================

    @Test
    void testGetEventsByCdsID_Success() throws Exception {
        // Arrange
        List<EventViewDetails> eventList = Arrays.asList(testEventView);
        when(eventService.getEventsByCdsID("user123")).thenReturn(eventList);

        // Act & Assert
        mockMvc.perform(get("/api/events/creator/user123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(1))
                .andExpect(jsonPath("$[0].createdBy").value("user123"));

        verify(eventService, times(1)).getEventsByCdsID("user123");
    }

    @Test
    void testGetEventsByCdsID_NotFound() throws Exception {
        // Arrange
        when(eventService.getEventsByCdsID("invalidUser"))
                .thenThrow(new EventNotFound("No events found"));

        // Act & Assert
        mockMvc.perform(get("/api/events/creator/invalidUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getEventsByCdsID("invalidUser");
    }

    // ==================== PATCH /editEvent/{eventId} TESTS ====================

    @Test
    void testEditEvent_Success() throws Exception {
        // Arrange
        EventSubmitResponse updateResponse = new EventSubmitResponse("Event updated successfully");
        when(eventService.editEvent(eq(1L), any(EventDetails.class))).thenReturn(updateResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/events/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEventDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Event updated successfully"));

        verify(eventService, times(1)).editEvent(eq(1L), any(EventDetails.class));
    }

    @Test
    void testEditEvent_NotFound() throws Exception {
        // Arrange
        when(eventService.editEvent(eq(999L), any(EventDetails.class)))
                .thenThrow(new EventNotFound("Event not found"));

        // Act & Assert
        mockMvc.perform(patch("/api/events/editEvent/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEventDetails)))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).editEvent(eq(999L), any(EventDetails.class));
    }

    @Test
    void testEditEvent_CannotSetDeleted() throws Exception {
        // Arrange
        when(eventService.editEvent(eq(1L), any(EventDetails.class)))
                .thenThrow(new EventException("Cannot set status to 'Deleted' through EDIT endpoint"));

        // Act & Assert
        mockMvc.perform(patch("/api/events/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEventDetails)))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).editEvent(eq(1L), any(EventDetails.class));
    }

    // ==================== DELETE /{eventId} TESTS ====================

    @Test
    void testDeleteEvent_Success() throws Exception {
        // Arrange
        EventSubmitResponse deleteResponse = new EventSubmitResponse("Event deleted successfully");
        when(eventService.deleteEvent(1L)).thenReturn(deleteResponse);

        // Act & Assert
        mockMvc.perform(delete("/api/events/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Event deleted successfully"));

        verify(eventService, times(1)).deleteEvent(1L);
    }

    @Test
    void testDeleteEvent_NotFound() throws Exception {
        // Arrange
        when(eventService.deleteEvent(999L))
                .thenThrow(new EventNotFound("Event with ID 999 not found for deletion."));

        // Act & Assert
        mockMvc.perform(delete("/api/events/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // EventNotFound is handled by global error handler returning 404

        verify(eventService, times(1)).deleteEvent(999L);
    }

    // ==================== GET /status/{status} TESTS ====================

    @Test
    void testGetEventsByStatus_Success() throws Exception {
        // Arrange
        List<EventViewDetails> eventList = Arrays.asList(testEventView);
        when(eventService.getEventsByStatus("Planned")).thenReturn(eventList);

        // Act & Assert
        mockMvc.perform(get("/api/events/status/Planned")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Planned"));

        verify(eventService, times(1)).getEventsByStatus("Planned");
    }

    @Test
    void testGetEventsByStatus_NotFound() throws Exception {
        // Arrange
        when(eventService.getEventsByStatus("Completed"))
                .thenThrow(new EventNotFound("Event with status Completed not found"));

        // Act & Assert
        mockMvc.perform(get("/api/events/status/Completed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getEventsByStatus("Completed");
    }

    @Test
    void testGetEventsByStatus_EmptyStatus() throws Exception {
        // Arrange - Using a space string that will be validated as empty by service
        when(eventService.getEventsByStatus(" "))
                .thenThrow(new EventException("Event status cannot be empty"));

        // Act & Assert
        mockMvc.perform(get("/api/events/status/ ")  // Space as path variable
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Controller catches EventException and returns 404

        verify(eventService, times(1)).getEventsByStatus(" ");
    }

    // ==================== GET /type/{eventType} TESTS ====================

    @Test
    void testGetEventsByType_Success() throws Exception {
        // Arrange
        List<EventViewDetails> eventList = Arrays.asList(testEventView);
        when(eventService.getEventsByType("Training")).thenReturn(eventList);

        // Act & Assert
        mockMvc.perform(get("/api/events/type/Training")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("Training"));

        verify(eventService, times(1)).getEventsByType("Training");
    }

    @Test
    void testGetEventsByType_NotFound() throws Exception {
        // Arrange
        when(eventService.getEventsByType("Workshop"))
                .thenThrow(new EventNotFound("Event with type Workshop not found"));

        // Act & Assert
        mockMvc.perform(get("/api/events/type/Workshop")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getEventsByType("Workshop");
    }

    // ==================== GET /availableRequests TESTS ====================

    @Test
    void testGetAvailableRequestsForEvent_Success() throws Exception {
        // Arrange
        List<AvailableRequest> requestList = Arrays.asList(testAvailableRequest);
        when(eventService.getAvailableRequestsForEvent()).thenReturn(requestList);

        // Act & Assert
        mockMvc.perform(get("/api/events/availableRequests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(1))
                .andExpect(jsonPath("$[0].tanNumber").value("TAN123"));

        verify(eventService, times(1)).getAvailableRequestsForEvent();
    }

    @Test
    void testGetAvailableRequestsForEvent_EmptyList() throws Exception {
        // Arrange
        when(eventService.getAvailableRequestsForEvent()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/events/availableRequests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(eventService, times(1)).getAvailableRequestsForEvent();
    }

    @Test
    void testGetAvailableRequestsForEvent_Exception() throws Exception {
        // Arrange
        when(eventService.getAvailableRequestsForEvent())
                .thenThrow(new EventException("Failed to retrieve available requests"));

        // Act & Assert
        mockMvc.perform(get("/api/events/availableRequests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(eventService, times(1)).getAvailableRequestsForEvent();
    }

    // ==================== GET /availableRequests/{eventId} TESTS ====================

    @Test
    void testGetAvailableRequestsForEventEdit_Success() throws Exception {
        // Arrange
        List<AvailableRequest> requestList = Arrays.asList(testAvailableRequest);
        when(eventService.getAvailableRequestsForEvent(1L)).thenReturn(requestList);

        // Act & Assert
        mockMvc.perform(get("/api/events/availableRequests/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(1));

        verify(eventService, times(1)).getAvailableRequestsForEvent(1L);
    }

    @Test
    void testGetAvailableRequestsForEventEdit_Exception() throws Exception {
        // Arrange
        when(eventService.getAvailableRequestsForEvent(999L))
                .thenThrow(new EventException("Failed to retrieve available requests"));

        // Act & Assert
        mockMvc.perform(get("/api/events/availableRequests/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(eventService, times(1)).getAvailableRequestsForEvent(999L);
    }

    // ==================== GET /statistics TESTS ====================

    @Test
    void testGetEventStatistics_Success() throws Exception {
        // Arrange
        when(eventService.getEventStatistics()).thenReturn(testStatistics);

        // Act & Assert
        mockMvc.perform(get("/api/events/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.planned").value(2))
                .andExpect(jsonPath("$.inProgress").value(1))
                .andExpect(jsonPath("$.completed").value(1))
                .andExpect(jsonPath("$.cancelled").value(1));

        verify(eventService, times(1)).getEventStatistics();
    }

    @Test
    void testGetEventStatistics_EmptyStatistics() throws Exception {
        // Arrange
        EventStatistics emptyStats = new EventStatistics();
        emptyStats.setTotal(0L);
        emptyStats.setPlanned(0L);
        emptyStats.setInProgress(0L);
        emptyStats.setCompleted(0L);
        emptyStats.setCancelled(0L);
        emptyStats.setDeleted(0L);

        when(eventService.getEventStatistics()).thenReturn(emptyStats);

        // Act & Assert
        mockMvc.perform(get("/api/events/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));

        verify(eventService, times(1)).getEventStatistics();
    }
}

