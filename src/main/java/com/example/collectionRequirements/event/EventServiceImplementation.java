package com.example.collectionRequirements.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImplementation implements EventService {

    private final EventRepository eventRepository; // Inject the repository

    @Autowired
    public EventServiceImplementation(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event createEvent(Event newEvent) throws EventException {
        if (newEvent.getEventName() == null || newEvent.getEventName().trim().isEmpty()) {
            throw new EventException("Event name cannot be empty.");
        }
        return eventRepository.save(newEvent);
    }

    @Override
    public Event getEventById(Long eventId) throws EventException {
        Optional<Event> findEvent = eventRepository.findById(eventId);
        if(findEvent.isEmpty())
        {
            throw new EventException("Event with ID " + eventId + " not found");
        }
        return findEvent.get();
    }

    @Override
    public List<Event> getAllEvents() throws EventException {

        try {
            return eventRepository.findAll();
        } catch (Exception e) {
            throw new EventException("Failed to retrieve all events.");
        }
    }

    @Override
    public Event editEvent(Long eventId, Event updatedEvent) throws EventException {
        Optional<Event> existingEventOpt = eventRepository.findById(eventId);
        if (existingEventOpt.isEmpty()) {
            throw new EventException("Event with ID " + eventId + " not found");
        }
        Event existingEvent = existingEventOpt.get();
        if (updatedEvent.getEventName() != null && !updatedEvent.getEventName().trim().isEmpty()) {
            existingEvent.setEventName(updatedEvent.getEventName());
        }
        if (updatedEvent.getDescription() != null) {
            existingEvent.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getParticipantsCount() != null) {
            existingEvent.setParticipantsCount(updatedEvent.getParticipantsCount());
        }
        if (updatedEvent.getDuration() != null) {
            existingEvent.setDuration(updatedEvent.getDuration());
        }
        if (updatedEvent.getEventType() != null) {
            existingEvent.setEventType(updatedEvent.getEventType());
        }
        if (updatedEvent.getFundingSource() != null) {
            existingEvent.setFundingSource(updatedEvent.getFundingSource());
        }
        if (updatedEvent.getStatus() != null) {
            existingEvent.setStatus(updatedEvent.getStatus());
        }
        return eventRepository.save(existingEvent);
    }

    @Override
    public void deleteEvent(Long eventId) throws EventException {

        if (!eventRepository.existsById(eventId)) {
            throw new EventException("Event with ID " + eventId + " not found for deletion.");
        }
        eventRepository.deleteById(eventId);
    }

    @Override
    public List<Event> getEventsByStatus(String status) throws EventException{
        if (status== null || status.trim().isEmpty()) {
            throw new EventException("Event status cannot be empty");
        }

        List<Event> findEvent = eventRepository.findByStatus(status);
        if(findEvent.isEmpty())
        {
            throw new EventException("Event with status " + status + " not found");
        }
        return findEvent;
    }

    @Override
    public List<Event> getEventsByType(String eventType) throws EventException {
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new EventException("Event type cannot be empty");
        }

        List<Event> findEvent = eventRepository.findByEventType(eventType);

        if (findEvent.isEmpty()) {
            throw new EventException("Event with type " + eventType + " not found");
        }
        return findEvent;
    }
}