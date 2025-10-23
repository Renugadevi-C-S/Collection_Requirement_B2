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