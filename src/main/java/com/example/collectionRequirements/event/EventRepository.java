package com.example.collectionRequirements.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus (String status);

    List<Event> findByEventType (String eventType);

    List<Event> findByCreatedBy_CdsID(String cdsId);

}
