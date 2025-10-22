package com.example.collectionRequirements.event;

import com.example.collectionRequirements.approval.Approval;
import com.example.department.Department;
import com.example.region.Region;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRepository extends JpaRepository<Event, Long> {

}
