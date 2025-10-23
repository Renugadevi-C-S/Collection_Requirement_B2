package com.example.collectionRequirements.event;
import java.util.List;

public interface EventService {

    Event createEvent(Event newEvent) throws EventException;

    Event getEventById(Long eventId) throws EventException;

    List<Event> getAllEvents() throws EventException;

    Event editEvent(Long eventId, Event updateEvent) throws EventException;

    void deleteEvent(Long eventId) throws EventException;

    List<Event> getEventsByStatus(String status) throws EventException;

    List<Event> getEventsByType(String eventType) throws EventException;

}
