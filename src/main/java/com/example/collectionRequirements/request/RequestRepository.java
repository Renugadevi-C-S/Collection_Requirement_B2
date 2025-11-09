package com.example.collectionRequirements.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request,Long> {

    List<Request> findByRequestStatus(String status);


    @Query("SELECT req FROM Request req WHERE req.requestor.cdsID = :cdsId")
    List<Request> findRequestsByRequestorCdsId(@Param("cdsId") String cdsId);

    List<Request> findByRequestStatusAndEventIsNull(String requestStatus);

    @Query("SELECT r FROM Request r WHERE " +
            "(r.requestStatus = 'Approved' AND r.event IS NULL) OR " +
            "(r.requestStatus = 'Linked' AND r.event.eventId = :eventId)")
    List<Request> findAvailableRequestsForEventEdit(@Param("eventId") Long eventId);
}
