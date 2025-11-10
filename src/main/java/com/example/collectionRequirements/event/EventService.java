package com.example.collectionRequirements.event;

import com.example.DTOs.*;
import com.example.collectionRequirements.request.RequestException;


import java.util.List;

public interface EventService {

    EventSubmitResponse createEvent(EventDetails eventDetails) throws EventException;

    EventViewDetails getEventById(Long eventId) throws EventException;

    List<EventViewDetails> getEventsByCdsID(String cdsID) throws EventException;

    List<EventViewDetails> getAllEvents() throws EventException;

    EventSubmitResponse editEvent(Long eventId, EventDetails eventDetails) throws EventException, RequestException;

    EventSubmitResponse deleteEvent(Long eventId) throws EventException;

    List<EventViewDetails> getEventsByStatus(String status) throws EventException;

    List<EventViewDetails> getEventsByType(String eventType) throws EventException;

    List<AvailableRequest> getAvailableRequestsForEvent() throws EventException;

    List<AvailableRequest> getAvailableRequestsForEvent(Long eventId) throws EventException;

    EventStatistics getEventStatistics();


}
