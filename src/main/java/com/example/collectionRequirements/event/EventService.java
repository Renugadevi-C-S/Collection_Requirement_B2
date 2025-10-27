package com.example.collectionRequirements.event;

import com.example.DTOs.EventDetails;
import com.example.DTOs.EventSubmitResponse;
import com.example.DTOs.EventViewDetails;

import java.util.List;

public interface EventService {

    EventSubmitResponse createEvent(EventDetails eventDetails) throws EventException;

    EventViewDetails getEventById(Long eventId) throws EventException;

    List<EventViewDetails> getAllEvents() throws EventException;

    EventSubmitResponse editEvent(Long eventId, EventDetails eventDetails) throws EventException;

    EventSubmitResponse deleteEvent(Long eventId) throws EventException;

    List<EventViewDetails> getEventsByStatus(String status) throws EventException;

    List<EventViewDetails> getEventsByType(String eventType) throws EventException;

}
