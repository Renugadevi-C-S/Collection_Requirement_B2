package com.example.collectionRequirements.request;


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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestServiceImplementation implements RequestService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public RequestServiceImplementation(RequestRepository requestRepository, UserRepository userRepository, DepartmentRepository departmentRepository)
    {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    /**
     * Helper method to convert Request entity to RequestsViewDetails DTO
     * Ensures all fields are properly set with null checks
     */
    private RequestsViewDetails convertToRequestsViewDetails(Request request) {
        RequestsViewDetails requestsViewDetails = new RequestsViewDetails();

        requestsViewDetails.setRequestId(request.getRequestId());
        requestsViewDetails.setRequestStatus(request.getRequestStatus());
        requestsViewDetails.setRequestDate(request.getRequestDate());
        requestsViewDetails.setCurriculum(request.getCurriculumLink());
        requestsViewDetails.setTanNo(request.getTAN_Number());
        requestsViewDetails.setJustification(request.getJustification());
        requestsViewDetails.setNoOfParticipants(request.getNoOfParticipants());

        // Set requestor
        if(request.getRequestor() != null)
            requestsViewDetails.setRequestedBy(request.getRequestor().getCdsID());

        // Set department
        if(request.getDepartment() != null)
            requestsViewDetails.setDepartment(request.getDepartment().getDepartmentName());

        // Set event name
        if(request.getEvent() != null)
            requestsViewDetails.setEventName(request.getEvent().getEventName());
        else
            requestsViewDetails.setEventName("EventNotCreated");

        // Set approved by
        if(request.getApproval() != null ){
            requestsViewDetails.setApprovedBy(request.getApproval().getApprovedBy().getCdsID());
            requestsViewDetails.setApprovalNotes(request.getApproval().getApprovalNotes());
        }

        else{
            requestsViewDetails.setApprovedBy("Not Approved Yet");
            requestsViewDetails.setApprovalNotes("Not Approved Yet");
        }


        return requestsViewDetails;
    }

    public RequestsViewDetails getRequestById(long requestId) throws RequestException {
        Request fetchedRequest = requestRepository.findById(requestId)
                .orElseThrow(()->new RequestException("Request Not Found"));

        return convertToRequestsViewDetails(fetchedRequest);
    }
    public List<RequestsViewDetails> getAllRequests() throws RequestException
    {
        List<Request> requestList=requestRepository.findAll();

        if(requestList.isEmpty())
        {
            throw new RequestException("Request Not Found");
        }

        return requestList.stream()
                .map(this::convertToRequestsViewDetails)
                .collect(Collectors.toList());
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

        return fetchedRequests.stream()
                .map(this::convertToRequestsViewDetails)
                .toList();
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
    public RequestSubmitResponse updateRequest(Long requestId, RequestsViewDetails requestUpdateDetails) throws UserException, DepartmentException, RequestException {

        // Find existing request
        Request existingRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestException("Request Not Found with ID: " + requestId));

        // Update department if provided
        if (requestUpdateDetails.getDepartment() != null && !requestUpdateDetails.getDepartment().isEmpty()) {
            Department fetchedDepartment = departmentRepository.findByDepartmentNameIgnoreCase(requestUpdateDetails.getDepartment());
            if (fetchedDepartment == null) {
                throw new DepartmentException("Department not found for name: " + requestUpdateDetails.getDepartment());
            }
            existingRequest.setDepartment(fetchedDepartment);
        }

        // Update justification if provided
        if (requestUpdateDetails.getJustification() != null) {
            existingRequest.setJustification(requestUpdateDetails.getJustification());
        }

        // Update TAN number if provided
        if (requestUpdateDetails.getTanNo() != null) {
            existingRequest.setTAN_Number(requestUpdateDetails.getTanNo());
        }

        // Update number of participants if provided
        if (requestUpdateDetails.getNoOfParticipants() != null) {
            existingRequest.setNoOfParticipants(requestUpdateDetails.getNoOfParticipants());
            // Update group request flag based on participants
            existingRequest.setGroupRequest(requestUpdateDetails.getNoOfParticipants() >= 10);
        }

        // Update curriculum link if provided
        if (requestUpdateDetails.getCurriculum() != null) {
            existingRequest.setCurriculumLink(requestUpdateDetails.getCurriculum());
        }


        // Save updated request
        requestRepository.save(existingRequest);

        return new RequestSubmitResponse("Request updated successfully");
    }




}
