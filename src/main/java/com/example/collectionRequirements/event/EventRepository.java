package com.example.collectionRequirements.event;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus (String status);

    List<Event> findByEventType (String eventType);

    List<Event> findByCreatedBy_CdsID(String cdsId);

}
