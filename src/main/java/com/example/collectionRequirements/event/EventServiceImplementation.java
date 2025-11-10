package com.example.collectionRequirements.event;

import com.example.DTOs.*;
import com.example.collectionRequirements.request.Request;
import com.example.collectionRequirements.request.RequestRepository;
import com.example.user.UserInfo;
import com.example.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImplementation implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public EventServiceImplementation(EventRepository eventRepository, UserRepository userRepository,RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public EventSubmitResponse createEvent(EventDetails eventDetails) throws EventException {
        if (eventDetails.getEventName() == null || eventDetails.getEventName().trim().isEmpty()) {
            throw new EventException("Event name cannot be empty.");
        }

        if (eventDetails.getCreatedBy() == null || eventDetails.getCreatedBy().trim().isEmpty()) {
            throw new EventException("Creator ID is required.");
        }

        if (eventDetails.getRequestIds() == null || eventDetails.getRequestIds().isEmpty()) {
            throw new EventException("At least one approved request must be linked to the event.");
        }

        Optional<UserInfo> creator = userRepository.findByCdsID(eventDetails.getCreatedBy());
        if (creator.isEmpty()) {
            throw new EventException("User with cdsID " + eventDetails.getCreatedBy() + " not found");
        }

        Event newEvent = mapToEvent(eventDetails, creator.get());

        newEvent.setCreatedDate(LocalDate.now());

        newEvent = eventRepository.save(newEvent);

        // Handle linking requests to event
        int totalParticipants = 0;
        if (eventDetails.getRequestIds() != null && !eventDetails.getRequestIds().isEmpty()) {
            for (Long requestId : eventDetails.getRequestIds()) {
                Optional<Request> requestOpt = requestRepository.findById(requestId);

                if (requestOpt.isEmpty()) {
                    throw new EventException("Request with ID " + requestId + " not found");
                }

                Request request = requestOpt.get();

                // Validate request is approved and not already linked to an event
                if (!"Approved".equalsIgnoreCase(request.getRequestStatus())) {
                    throw new EventException("Request " + requestId + " is not approved");
                }

                if (request.getEvent() != null) {
                    throw new EventException("Request " + requestId + " is already linked to another event");
                }

                // Link request to event
                request.setEvent(newEvent);

                // Set request status to "Linked" when event is created
                request.setRequestStatus("Linked");
                requestRepository.save(request);

                // Sum up participants
                if (request.getNoOfParticipants() != null) {
                    totalParticipants += request.getNoOfParticipants();
                }
            }
        }

        // Set participants count (from linked requests or 0 if none)
        newEvent.setParticipantsCount(totalParticipants);
        eventRepository.save(newEvent);

        return new EventSubmitResponse("Event created successfully with " +
                (eventDetails.getRequestIds() != null ? eventDetails.getRequestIds().size() : 0) +
                " linked request(s) and " + totalParticipants + " total participants.");
    }

    @Override
    public List<AvailableRequest> getAvailableRequestsForEvent(Long eventId) throws EventException {
        try {
            List<Request> requests;

            if (eventId == null) {
                // For creating NEW event - only show Approved requests with no event link
                requests = requestRepository.findByRequestStatusAndEventIsNull("Approved");
            } else {
                requests = requestRepository.findAvailableRequestsForEventEdit(eventId);
            }

            return requests.stream()
                    .map(this::mapToAvailableRequest)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EventException("Failed to retrieve available requests for event: " + e.getMessage());
        }
    }

    //Get available requests that are approved to link with events
    @Override
    public List<AvailableRequest> getAvailableRequestsForEvent() throws EventException {
        try {
            List<Request> requests = requestRepository.findByRequestStatusAndEventIsNull("Approved");

            return requests.stream()
                    .map(this::mapToAvailableRequest)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EventException("Failed to retrieve available requests for event: " + e.getMessage());
        }
    }

    @Override
    public EventViewDetails getEventById(Long eventId) throws EventException {
        Optional<Event> findEvent = eventRepository.findById(eventId);
        if (findEvent.isEmpty()) {
            throw new EventException("Event with ID " + eventId + " not found");
        }

        Event event = findEvent.get();
        EventViewDetails eventViewDetails = new EventViewDetails();

        eventViewDetails.setEventId(event.getEventId());
        eventViewDetails.setEventName(event.getEventName());
        eventViewDetails.setDescription(event.getDescription());
        eventViewDetails.setDuration(event.getDuration());
        eventViewDetails.setEventType(event.getEventType());
        eventViewDetails.setFundingSource(event.getFundingSource());
        eventViewDetails.setParticipantsCount(event.getParticipantsCount());
        eventViewDetails.setStatus(event.getStatus());

        if (event.getCreatedBy() != null) {
            eventViewDetails.setCreatedBy(event.getCreatedBy().getCdsID());
        }

        eventViewDetails.setCreatedDate(event.getCreatedDate());
        eventViewDetails.setCompletedBy(event.getCompletedBy());
        eventViewDetails.setCompletionNotes(event.getCompletionNotes());
        eventViewDetails.setCompletedDate(event.getCompletedDate());
        eventViewDetails.setCancelledBy(event.getCancelledBy());
        eventViewDetails.setCancellationNotes(event.getCancellationNotes());
        eventViewDetails.setCancelledDate(event.getCancelledDate());

        // Map linked requests
        if (event.getRequests() != null && !event.getRequests().isEmpty()) {
            List<RequestsViewDetails> linkedRequests = event.getRequests().stream()
                    .map(this::mapRequestToViewDetails)
                    .collect(Collectors.toList());
            eventViewDetails.setLinkedRequests(linkedRequests);
        }

        return eventViewDetails;
    }

    @Override
    public List<EventViewDetails> getEventsByCdsID(String cdsID) throws EventException {

        List<Event> findEvents = eventRepository.findByCreatedBy_CdsID(cdsID);

        if (findEvents.isEmpty()) {
            throw new EventException("No events found for user with cdsID " + cdsID);
        }

        return findEvents.stream()
                .map(this::mapToEventViewDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventViewDetails> getAllEvents() throws EventException {
        try {
            List<Event> events = eventRepository.findAll();
            return events.stream()
                    .map(this::mapToEventViewDetails)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EventException("Failed to retrieve all events: " + e.getMessage());
        }
    }

    @Override
    public EventSubmitResponse editEvent(Long eventId, EventDetails eventDetails) throws EventException {
        Optional<Event> existingEventOpt = eventRepository.findById(eventId);
        if (existingEventOpt.isEmpty()) {
            throw new EventException("Event with ID " + eventId + " not found");
        }

        Event existingEvent = existingEventOpt.get();

        if (eventDetails.getStatus() != null && "Deleted".equalsIgnoreCase(eventDetails.getStatus())) {
            throw new EventException("Cannot set status to 'Deleted' through EDIT endpoint. Use Delete endpoint instead.");
        }

        // Prevent status changes for Completed/Cancelled events
        String currentStatus = existingEvent.getStatus();
        String newStatus = eventDetails.getStatus();

        if (currentStatus != null && newStatus != null) {
            // Prevent completing a cancelled event
            if ("Cancelled".equalsIgnoreCase(currentStatus) && "Completed".equalsIgnoreCase(newStatus)) {
                throw new EventException("Cannot complete a cancelled event. Cancelled events cannot be changed.");
            }
            // Prevent cancelling a completed event
            if ("Completed".equalsIgnoreCase(currentStatus) && "Cancelled".equalsIgnoreCase(newStatus)) {
                throw new EventException("Cannot cancel a completed event. Completed events cannot be cancelled.");
            }
        }

        //Update only provided fields (persisting existing values if not provided)
        if (eventDetails.getEventName() != null && !eventDetails.getEventName().trim().isEmpty()) {
            existingEvent.setEventName(eventDetails.getEventName());
        }
        if (eventDetails.getDescription() != null) {
            existingEvent.setDescription(eventDetails.getDescription());
        }
        if (eventDetails.getDuration() != null) {
            existingEvent.setDuration(eventDetails.getDuration());
        }
        if (eventDetails.getEventType() != null) {
            existingEvent.setEventType(eventDetails.getEventType());
        }
        if (eventDetails.getFundingSource() != null) {
            existingEvent.setFundingSource(eventDetails.getFundingSource());
        }

        // Handle status changes for Completed/Cancelled
        if (eventDetails.getStatus() != null) {
            String previousStatus = existingEvent.getStatus();
            existingEvent.setStatus(eventDetails.getStatus());

            if ("Completed".equalsIgnoreCase(eventDetails.getStatus())) {
                List<Request> linkedRequests = existingEvent.getRequests();
                if (linkedRequests != null && !linkedRequests.isEmpty()) {
                    for (Request request : linkedRequests) {
                        request.setRequestStatus("Completed");
                        requestRepository.save(request);
                    }
                }
            }

            // When event is cancelled, unlink requests and set status to "Approved"
            if ("Cancelled".equalsIgnoreCase(eventDetails.getStatus())) {
                List<Request> linkedRequests = existingEvent.getRequests();
                if (linkedRequests != null && !linkedRequests.isEmpty()) {
                    for (Request request : linkedRequests) {
                        // Unlink request from event
                        request.setEvent(null);
                        // Set status back to "Approved"
                        request.setRequestStatus("Approved");
                        requestRepository.save(request);
                    }
                }
            }
        }

        if (eventDetails.getCompletedBy() != null) {
            existingEvent.setCompletedBy(eventDetails.getCompletedBy());
        }
        if (eventDetails.getCompletionNotes() != null) {
            existingEvent.setCompletionNotes(eventDetails.getCompletionNotes());
        }
        if (eventDetails.getCompletedDate() != null) {
            existingEvent.setCompletedDate(LocalDate.parse(eventDetails.getCompletedDate()));
        }

        if (eventDetails.getCancelledBy() != null) {
            existingEvent.setCancelledBy(eventDetails.getCancelledBy());
        }
        if (eventDetails.getCancellationNotes() != null) {
            existingEvent.setCancellationNotes(eventDetails.getCancellationNotes());
        }
        if (eventDetails.getCancelledDate() != null) {
            existingEvent.setCancelledDate(LocalDate.parse(eventDetails.getCancelledDate()));
        }

        if (eventDetails.getRequestIds() != null) {

            if (eventDetails.getRequestIds().isEmpty()) {
                throw new EventException("At least one approved request must be linked to the event.");
            }

            List<Request> currentlyLinkedRequests = existingEvent.getRequests();
            List<Long> newRequestIds = eventDetails.getRequestIds();

            if (currentlyLinkedRequests != null && !currentlyLinkedRequests.isEmpty()) {
                for (Request linkedRequest : currentlyLinkedRequests) {
                    if (!newRequestIds.contains(linkedRequest.getRequestId())) {
                        linkedRequest.setEvent(null);
                        linkedRequest.setRequestStatus("Approved");
                        requestRepository.save(linkedRequest);
                    }
                }
            }

            // Link new requests and calculate participants
            int totalParticipants = 0;
            for (Long requestId : eventDetails.getRequestIds()) {
                Optional<Request> requestOpt = requestRepository.findById(requestId);
                if (requestOpt.isEmpty()) {
                    throw new EventException("Request with ID " + requestId + " not found");
                }

                Request request = requestOpt.get();

                //  Allow both "Approved" and "Linked" (to this event)
                boolean isApproved = "Approved".equalsIgnoreCase(request.getRequestStatus());
                boolean isLinkedToThisEvent = "Linked".equalsIgnoreCase(request.getRequestStatus())
                        && request.getEvent() != null
                        && request.getEvent().getEventId().equals(eventId);

                if (!isApproved && !isLinkedToThisEvent) {
                    throw new EventException("Request " + requestId + " is not approved or already linked to this event");
                }

                if (request.getEvent() != null && !request.getEvent().getEventId().equals(eventId)) {
                    throw new EventException("Request " + requestId + " is already linked to another event");
                }

                request.setEvent(existingEvent);

                // Set request status to "Linked" when linking to event during edit
                if (!"Completed".equalsIgnoreCase(existingEvent.getStatus()) &&
                        !"Cancelled".equalsIgnoreCase(existingEvent.getStatus())) {
                    request.setRequestStatus("Linked");
                }
                requestRepository.save(request);

                if (request.getNoOfParticipants() != null) {
                    totalParticipants += request.getNoOfParticipants();
                }
            }
            existingEvent.setParticipantsCount(totalParticipants);
        }

        eventRepository.save(existingEvent);

        return new EventSubmitResponse("Event updated successfully");
    }

    @Override
    public EventSubmitResponse deleteEvent(Long eventId) throws EventException {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new EventException("Event with ID " + eventId + " not found for deletion.");
        }

        Event event = eventOpt.get();

        // Check if event status is "In Progress" or "Planned" before unlinking requests
        String currentStatus = event.getStatus();
        if ("In Progress".equalsIgnoreCase(currentStatus) || "Planned".equalsIgnoreCase(currentStatus)) {
            // Unlink all requests linked to this event
            List<Request> linkedRequests = event.getRequests();
            if (linkedRequests != null && !linkedRequests.isEmpty()) {
                for (Request request : linkedRequests) {
                    request.setEvent(null);
                    request.setRequestStatus("Approved");
                    requestRepository.save(request);
                }
            }
        }

        // Set event status to "Deleted"
        event.setStatus("Deleted");
        eventRepository.save(event);

        return new EventSubmitResponse("Event deleted successfully");
    }

    @Override
    public List<EventViewDetails> getEventsByStatus(String status) throws EventException {
        if (status == null || status.trim().isEmpty()) {
            throw new EventException("Event status cannot be empty");
        }

        List<Event> findEvent = eventRepository.findByStatus(status);
        if (findEvent.isEmpty()) {
            throw new EventException("Event with status " + status + " not found");
        }

        return findEvent.stream()
                .map(this::mapToEventViewDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventViewDetails> getEventsByType(String eventType) throws EventException {
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new EventException("Event type cannot be empty");
        }

        List<Event> findEvent = eventRepository.findByEventType(eventType);

        if (findEvent.isEmpty()) {
            throw new EventException("Event with type " + eventType + " not found");
        }

        return findEvent.stream()
                .map(this::mapToEventViewDetails)
                .collect(Collectors.toList());
    }

    private Event mapToEvent(EventDetails eventDetails, UserInfo creator) {
        Event event = new Event();
        event.setEventName(eventDetails.getEventName());
        event.setDescription(eventDetails.getDescription());
        event.setDuration(eventDetails.getDuration());
        event.setEventType(eventDetails.getEventType());
        event.setFundingSource(eventDetails.getFundingSource());
        event.setStatus(eventDetails.getStatus());
        event.setCreatedBy(creator);
        return event;
    }

    private EventViewDetails mapToEventViewDetails(Event event) {
        EventViewDetails eventViewDetails = new EventViewDetails();
        eventViewDetails.setEventId(event.getEventId());
        eventViewDetails.setEventName(event.getEventName());
        eventViewDetails.setDuration(event.getDuration());
        eventViewDetails.setEventType(event.getEventType());
        eventViewDetails.setStatus(event.getStatus());

        if (event.getCreatedBy() != null) {
            eventViewDetails.setCreatedBy(event.getCreatedBy().getCdsID());
        }

        eventViewDetails.setCreatedDate(event.getCreatedDate());
        eventViewDetails.setCompletedBy(event.getCompletedBy());
        eventViewDetails.setCompletionNotes(event.getCompletionNotes());
        eventViewDetails.setCompletedDate(event.getCompletedDate());
        eventViewDetails.setCancelledBy(event.getCancelledBy());
        eventViewDetails.setCancellationNotes(event.getCancellationNotes());
        eventViewDetails.setCancelledDate(event.getCancelledDate());

        return eventViewDetails;
    }

    private RequestsViewDetails mapRequestToViewDetails(Request request) {
        RequestsViewDetails viewDetails = new RequestsViewDetails();
        viewDetails.setRequestId(request.getRequestId());
        viewDetails.setTanNo(request.getTAN_Number());
        viewDetails.setNoOfParticipants(request.getNoOfParticipants());
        viewDetails.setRequestDate(request.getRequestDate());
        viewDetails.setJustification(request.getJustification());
        viewDetails.setRequestStatus(request.getRequestStatus());
        viewDetails.setCurriculum(request.getCurriculumLink());

        if (request.getRequestor() != null) {
            viewDetails.setRequestedBy(request.getRequestor().getCdsID());
        }

        if (request.getDepartment() != null) {
            viewDetails.setDepartment(request.getDepartment().getDepartmentName());
        }

        if (request.getApproval() != null) {
            if (request.getApproval().getApprovedBy() != null) {
                viewDetails.setApprovedBy(request.getApproval().getApprovedBy().getCdsID());
            }
            viewDetails.setApprovalNotes(request.getApproval().getApprovalNotes());
        }

        return viewDetails;
    }

    private AvailableRequest mapToAvailableRequest(Request request) {
        AvailableRequest availableRequest = new AvailableRequest();
        availableRequest.setRequestId(request.getRequestId());
        availableRequest.setTanNumber(request.getTAN_Number());
        availableRequest.setNoOfParticipants(request.getNoOfParticipants());
        availableRequest.setRequestDate(request.getRequestDate());
        availableRequest.setJustification(request.getJustification());

        if (request.getRequestor() != null) {
            availableRequest.setRequestedBy(request.getRequestor().getCdsID());
        }

        if (request.getDepartment() != null) {
            availableRequest.setDepartment(request.getDepartment().getDepartmentName());
        }

        return availableRequest;
    }

    @Override
    public EventStatistics getEventStatistics() {
        EventStatistics stats = new EventStatistics();

        // Get all events
        List<Event> allEvents = eventRepository.findAll();

        // Total count
        stats.setTotal((long) allEvents.size());

        // Count by status
        stats.setPlanned(allEvents.stream().filter(e -> "Planned".equalsIgnoreCase(e.getStatus())).count());
        stats.setInProgress(allEvents.stream().filter(e -> "In Progress".equalsIgnoreCase(e.getStatus())).count());
        stats.setCompleted(allEvents.stream().filter(e -> "Completed".equalsIgnoreCase(e.getStatus())).count());
        stats.setCancelled(allEvents.stream().filter(e -> "Cancelled".equalsIgnoreCase(e.getStatus())).count());
        stats.setDeleted(allEvents.stream().filter(e -> "Deleted".equalsIgnoreCase(e.getStatus())).count());

        return stats;
    }
}

