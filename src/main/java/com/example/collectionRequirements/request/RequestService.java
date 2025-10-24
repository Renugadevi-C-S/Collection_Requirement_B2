package com.example.collectionRequirements.request;

import com.example.DTOs.LCRequestResponse;
import com.example.DTOs.RequestDetails;
import com.example.DTOs.RequestSubmitResponse;
import com.example.department.DepartmentException;
import com.example.user.UserException;

import java.util.List;

public interface RequestService {

    Request createRequest(Request newRequest, String requestorCdsId, String deptName) throws RequestException;
    LCRequestResponse getRequestById(long requestId)throws RequestException;
    List<Request> getAllRequests()throws RequestException;
    List<Request> getRequestByStatus(String status)throws RequestException;

    List<LCRequestResponse> getRequestByCdsId(String cdsId) throws UserException, RequestException;

    RequestSubmitResponse submitNewRequest(RequestDetails requestDetails) throws UserException, DepartmentException;
}
