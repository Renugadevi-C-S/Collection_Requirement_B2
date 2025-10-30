package com.example.collectionRequirements.event;

import com.example.DTOs.EventDetails;
import com.example.DTOs.EventSubmitResponse;
import com.example.DTOs.EventViewDetails;
import com.example.user.UserInfo;
import com.example.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImplementation implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public EventServiceImplementation(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EventSubmitResponse createEvent(EventDetails eventDetails, String cdsId) throws EventException {
        if (eventDetails.getEventName() == null || eventDetails.getEventName().trim().isEmpty()) {
            throw new EventException("Event name cannot be empty.");
        }

        Optional<UserInfo> creator = userRepository.findByCdsID(cdsId);
        if (creator.isEmpty()) {
            throw new EventException("User with cdsID " + cdsId + " not found");
        }

        Event newEvent = new Event();
        newEvent.setEventName(eventDetails.getEventName());
        newEvent.setDescription(eventDetails.getDescription());
        newEvent.setDuration(eventDetails.getDuration());
        newEvent.setEventType(eventDetails.getEventType());
        newEvent.setFundingSource(eventDetails.getFundingSource());
        newEvent.setStatus(eventDetails.getStatus());
        newEvent.setCreatedBy(creator.get());

        eventRepository.save(newEvent);

        return new EventSubmitResponse("Event created successfully");
    }

    @Override
    public EventViewDetails getEventById(Long eventId) throws EventException {
        Optional<Event> findEvent = eventRepository.findById(eventId);
        if (findEvent.isEmpty()) {
            throw new EventException("Event with ID " + eventId + " not found");
        }

        return mapToEventViewDetails(findEvent.get());
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
            throw new EventException("Failed to retrieve all events.");
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
        if (eventDetails.getStatus() != null) {
            existingEvent.setStatus(eventDetails.getStatus());
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
        return eventViewDetails;
    }
}
