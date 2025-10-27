package com.example.collectionRequirements.request;

import com.example.DTOs.RequestUpdate;
import com.example.DTOs.RequestsViewDetails;
import com.example.DTOs.RequestDetails;
import com.example.DTOs.RequestSubmitResponse;
import com.example.department.DepartmentException;
import com.example.user.UserException;

import java.util.List;

public interface RequestService {

    RequestsViewDetails getRequestById(long requestId)throws RequestException;
    List<RequestsViewDetails> getAllRequests()throws RequestException;
    List<Request> getRequestByStatus(String status)throws RequestException;

    List<RequestsViewDetails> getRequestByCdsId(String cdsId) throws UserException, RequestException;

    RequestSubmitResponse submitNewRequest(RequestDetails requestDetails) throws UserException, DepartmentException;
    RequestSubmitResponse updateRequest(Long requestId, RequestUpdate requestUpdateDTO) throws UserException, DepartmentException, RequestException;
}
