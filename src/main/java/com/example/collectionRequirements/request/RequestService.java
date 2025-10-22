package com.example.collectionRequirements.request;

import java.util.List;

public interface RequestService {

    Request createRequest(Request newRequest) throws RequestException;
    Request getRequestById(long requestId)throws RequestException;
    List<Request> getAllRequests()throws RequestException;
    List<Request> getRequestByStatus(String status)throws RequestException;
}
