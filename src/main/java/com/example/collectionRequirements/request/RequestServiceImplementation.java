package com.example.collectionRequirements.request;


import com.example.DTOs.RequestUpdate;
import com.example.DTOs.RequestsViewDetails;
import com.example.DTOs.RequestDetails;
import com.example.DTOs.RequestSubmitResponse;
import com.example.department.Department;
import com.example.department.DepartmentException;
import com.example.department.DepartmentRepository;
import com.example.user.UserException;
import com.example.user.UserInfo;
import com.example.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.stream.LangCollectors.collect;

@Service
public class RequestServiceImplementation implements RequestService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private RequestRepository requestRepository;

    @Autowired
    public RequestServiceImplementation(RequestRepository requestRepository, UserRepository userRepository, DepartmentRepository departmentRepository)
    {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }


    public RequestsViewDetails getRequestById(long requestId) throws RequestException {
        Request fetchedRequest = requestRepository.findById(requestId)
                .orElseThrow(()->new RequestException("Request Not Found"));

        RequestsViewDetails requestsViewDetails = new RequestsViewDetails();

        requestsViewDetails.setRequestedBy(fetchedRequest.getRequestor().getCdsID());
        requestsViewDetails.setRequestId(fetchedRequest.getRequestId());
        requestsViewDetails.setRequestStatus(fetchedRequest.getRequestStatus());
        requestsViewDetails.setRequestDate(fetchedRequest.getRequestDate());
        if(fetchedRequest.getDepartment()!=null)
            requestsViewDetails.setDepartment(fetchedRequest.getDepartment().getDepartmentName());
        requestsViewDetails.setJustification(fetchedRequest.getJustification());
        if(fetchedRequest.getEvent()!=null)
            requestsViewDetails.setEventName(fetchedRequest.getEvent().getEventName());
        requestsViewDetails.setNoOfParticipants(fetchedRequest.getNoOfParticipants());

        return requestsViewDetails;
    }
    public List<RequestsViewDetails> getAllRequests() throws RequestException
    {
        List<Request> requestList=requestRepository.findAll();

        if(requestList.isEmpty())
        {
            throw new RequestException("Request Not Found");
        }

        return requestList.stream().map(req->{

            RequestsViewDetails requestsViewDetails = new RequestsViewDetails();
            requestsViewDetails.setRequestId(req.getRequestId());
            requestsViewDetails.setRequestStatus(req.getRequestStatus());
            requestsViewDetails.setRequestDate(req.getRequestDate());
            requestsViewDetails.setJustification(req.getJustification());
            if(req.getDepartment()!=null)
                requestsViewDetails.setDepartment(req.getDepartment().getDepartmentName());
            if(req.getEvent()!=null)
                requestsViewDetails.setEventName(req.getEvent().getEventName());
            else
                requestsViewDetails.setEventName("EventNotCreated");
            requestsViewDetails.setNoOfParticipants(req.getNoOfParticipants());
            if(req.getRequestor()!=null)
                requestsViewDetails.setRequestedBy(req.getRequestor().getCdsID());
            return requestsViewDetails;
        }).collect(Collectors.toList());
    }
    public List<Request> getRequestByStatus(String status) throws RequestException
    {
        return requestRepository.findByRequestStatus(status);
    }

    @Override
    public List<RequestsViewDetails> getRequestByCdsId(String cdsId) throws UserException, RequestException {
        Optional<UserInfo> fetchedUser = userRepository.findByCdsID(cdsId);

        if(fetchedUser.isEmpty())
            throw new UserException("User not found");

        List<Request> fetchedRequests = requestRepository.findRequestsByRequestorCdsId(cdsId);

        if(fetchedRequests.isEmpty())
            throw new RequestException("Request Not Found");

        return fetchedRequests
            .stream()
            .map((request)->{
                RequestsViewDetails requestsViewDetails = new RequestsViewDetails();

                requestsViewDetails.setRequestId(request.getRequestId());
                requestsViewDetails.setRequestStatus(request.getRequestStatus());
                requestsViewDetails.setRequestDate(request.getRequestDate());
                if(request.getDepartment()!=null)
                    requestsViewDetails.setDepartment(request.getDepartment().getDepartmentName());
                if(request.getEvent()!=null)
                    requestsViewDetails.setEventName(request.getEvent().getEventName());
                else
                    requestsViewDetails.setEventName("EventNotCreated");
                requestsViewDetails.setJustification(request.getJustification());
                requestsViewDetails.setNoOfParticipants(request.getNoOfParticipants());

                return requestsViewDetails;
            }).toList();
    }

    @Override
    public RequestSubmitResponse submitNewRequest(RequestDetails requestDetails) throws UserException, DepartmentException {
        Request newRequest = new Request();

        UserInfo requestor = userRepository.findByCdsID(requestDetails.getRequestorId())
                .orElseThrow(()->new UserException("User not found for cdsId: "+requestDetails.getRequestorId()));

        newRequest.setRequestor(requestor);

        Department fetchedDepartment = departmentRepository.findByDepartmentNameIgnoreCase(requestDetails.getDepartment());
        if(fetchedDepartment == null)
            throw new DepartmentException("Department not found for name: "+requestDetails.getDepartment());


        newRequest.setDepartment(fetchedDepartment);
        newRequest.setRequestDate(LocalDate.now());
        newRequest.setGroupRequest(requestDetails.getNoOfParticipants() >= 10);
        newRequest.setNoOfParticipants(requestDetails.getNoOfParticipants());
        newRequest.setRequestStatus("Submitted");
        newRequest.setJustification(requestDetails.getJustification());
        newRequest.setTAN_Number(requestDetails.getTanNo());
        newRequest.setCurriculumLink(requestDetails.getCurriculum());

        requestRepository.save(newRequest);

        return new RequestSubmitResponse("New Request Submitted Successfully ");
    }

    @Override
    public RequestSubmitResponse updateRequest(Long requestId, RequestUpdate requestUpdateDTO) throws UserException, DepartmentException, RequestException {

        // Find existing request
        Request existingRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestException("Request Not Found with ID: " + requestId));

        // Update department if provided
        if (requestUpdateDTO.getDepartment() != null && !requestUpdateDTO.getDepartment().isEmpty()) {
            Department fetchedDepartment = departmentRepository.findByDepartmentNameIgnoreCase(requestUpdateDTO.getDepartment());
            if (fetchedDepartment == null) {
                throw new DepartmentException("Department not found for name: " + requestUpdateDTO.getDepartment());
            }
            existingRequest.setDepartment(fetchedDepartment);
        }

        // Update justification if provided
        if (requestUpdateDTO.getJustification() != null) {
            existingRequest.setJustification(requestUpdateDTO.getJustification());
        }

        // Update TAN number if provided
        if (requestUpdateDTO.getTanNo() != null) {
            existingRequest.setTAN_Number(requestUpdateDTO.getTanNo());
        }

        // Update number of participants if provided
        if (requestUpdateDTO.getNoOfParticipants() != null) {
            existingRequest.setNoOfParticipants(requestUpdateDTO.getNoOfParticipants());
            // Update group request flag based on participants
            existingRequest.setGroupRequest(requestUpdateDTO.getNoOfParticipants() >= 10);
        }

        // Update curriculum link if provided
        if (requestUpdateDTO.getCurriculum() != null) {
            existingRequest.setCurriculumLink(requestUpdateDTO.getCurriculum());
        }

        // Update request status if provided
        if (requestUpdateDTO.getRequestStatus() != null) {
            existingRequest.setRequestStatus(requestUpdateDTO.getRequestStatus());
        }

        // Save updated request
        requestRepository.save(existingRequest);

        return new RequestSubmitResponse("Request updated successfully");
    }




}
