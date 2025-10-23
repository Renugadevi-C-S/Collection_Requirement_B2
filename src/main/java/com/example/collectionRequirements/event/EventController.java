package com.example.collectionRequirements.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/newEvent")
    public ResponseEntity<?> createEvent(@RequestBody Event newEvent) {
        try {
            Event createdEvent = eventService.createEvent(newEvent);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }






    @GetMapping("/status/{status}")
    public ResponseEntity<?> getEventsByStatus(@PathVariable String status) {
        try {
            List<Event> events = eventService.getEventsByStatus(status);
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<?> getEventsByType(@PathVariable String eventType) {
        try {
            List<Event> events = eventService.getEventsByType(eventType);
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (EventException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
