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

    public Request createRequest(Request newRequest, String requestorCdsId, String deptName) throws RequestException
    {
        newRequest.setRequestDate(LocalDate.now());
        newRequest.setRequestStatus("Submitted");
        newRequest.setGroupRequest(newRequest.getNoOfParticipants() >= 10);

        Department fetchDept = departmentRepository.findByDepartmentNameIgnoreCase(deptName);
        if(fetchDept == null)
            throw new DepartmentException("Department not found for "+deptName);

        Optional<UserInfo> requestor = userRepository.findByCdsID(requestorCdsId);

        if(requestor.isEmpty())
            throw new UserException("No user or requestor found for "+requestorCdsId);

        newRequest.setRequestor(requestor.get());
        newRequest.setDepartment(fetchDept);

        return requestRepository.save(newRequest);
    }
    public RequestsViewDetails getRequestById(long requestId) throws RequestException {
        Request fetchedRequest = requestRepository.findById(requestId)
                .orElseThrow(()->new RequestException("Request Not Found"));

        RequestsViewDetails requestsViewDetails = new RequestsViewDetails();

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
    public List<Request> getAllRequests() throws RequestException
    {
        return requestRepository.findAll();
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


}
