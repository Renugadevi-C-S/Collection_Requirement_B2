package com.example.collectionRequirements.event;

import com.example.DTOs.EventDetails;
import com.example.DTOs.EventSubmitResponse;
import com.example.DTOs.EventViewDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8080"})
@RequestMapping("api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/newEvent")
    public ResponseEntity<?> createEvent(@RequestBody EventDetails eventDetails) {
        try {
            EventSubmitResponse response = eventService.createEvent(eventDetails);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllEvents() {
        try {
            List<EventViewDetails> events = eventService.getAllEvents();
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable Long eventId) {
        try {
            EventViewDetails event = eventService.getEventById(eventId);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/editEvent/{eventId}")
    public ResponseEntity<?> editEvent(@PathVariable Long eventId, @RequestBody EventDetails eventDetails) {
        try {
            EventSubmitResponse response = eventService.editEvent(eventId, eventDetails);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        try {
            EventSubmitResponse response = eventService.deleteEvent(eventId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getEventsByStatus(@PathVariable String status) {
        try {
            List<EventViewDetails> events = eventService.getEventsByStatus(status);
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<?> getEventsByType(@PathVariable String eventType) {
        try {
            List<EventViewDetails> events = eventService.getEventsByType(eventType);
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
