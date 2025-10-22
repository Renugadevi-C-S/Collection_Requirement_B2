package com.example.collectionRequirements.request;

import java.security.GeneralSecurityException;
import java.util.List;

public interface RequestService {

    Request createRequest(Request newRequest) throws GeneralException;
    Request getRequestById(long requestId)throws GeneralException;
    List<Request> getAllRequests()throws GeneralException;
    List<Request> getRequestByStatus(String status)throws GeneralException;
}
