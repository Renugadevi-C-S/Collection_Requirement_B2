package com.example.collectionRequirements.event;

import com.example.collectionRequirements.approval.Approval;
import com.example.department.Department;
import com.example.region.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus (String status);
    List<Event> findByEventType (String eventType);
}
