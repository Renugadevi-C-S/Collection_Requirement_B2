package com.example.collectionRequirements.request;


import com.example.DTOs.LCRequestResponse;
import com.example.department.Department;
import com.example.department.DepartmentException;
import com.example.department.DepartmentRepository;
import com.example.user.UserException;
import com.example.user.UserInfo;
import com.example.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
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

        if(!requestor.get().getRole().equals("LC") &&
                !requestor.get().getRole().equals("L&DSPoC"))
            throw new RequestException("Only LC requestors can be requested for LC");

        newRequest.setRequestor(requestor.get());
        newRequest.setDepartment(fetchDept);

        return requestRepository.save(newRequest);
    }
    public LCRequestResponse getRequestById(long requestId) throws RequestException {
        Request fetchedRequest = requestRepository.findById(requestId)
                .orElseThrow(()->new RequestException("Request Not Found"));

        LCRequestResponse lcRequestResponse = new LCRequestResponse();

        lcRequestResponse.setRequestId(fetchedRequest.getRequestId());
        lcRequestResponse.setRequestStatus(fetchedRequest.getRequestStatus());
        lcRequestResponse.setRequestDate(fetchedRequest.getRequestDate());
        if(fetchedRequest.getDepartment()!=null)
            lcRequestResponse.setDepartment(fetchedRequest.getDepartment().getDepartmentName());
        lcRequestResponse.setJustification(fetchedRequest.getJustification());
        if(fetchedRequest.getEvent()!=null)
            lcRequestResponse.setEventName(fetchedRequest.getEvent().getEventName());
        lcRequestResponse.setNoOfParticipants(fetchedRequest.getNoOfParticipants());

        return lcRequestResponse;
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
    public List<LCRequestResponse> getRequestByCdsId(String cdsId) throws UserException, RequestException {
        Optional<UserInfo> fetchedUser = userRepository.findByCdsID(cdsId);

        if(fetchedUser.isEmpty())
            throw new UserException("User not found");

        List<Request> fetchedRequests = requestRepository.findRequestsByRequestorCdsId(cdsId);

        if(fetchedRequests.isEmpty())
            throw new RequestException("Request Not Found");

        return fetchedRequests
            .stream()
            .map((request)->{
                LCRequestResponse lcRequestResponse = new LCRequestResponse();

                lcRequestResponse.setRequestId(request.getRequestId());
                lcRequestResponse.setRequestStatus(request.getRequestStatus());
                lcRequestResponse.setRequestDate(request.getRequestDate());
                if(request.getDepartment()!=null)
                    lcRequestResponse.setDepartment(request.getDepartment().getDepartmentName());
                if(request.getEvent()!=null)
                    lcRequestResponse.setEventName(request.getEvent().getEventName());
                lcRequestResponse.setJustification(request.getJustification());
                lcRequestResponse.setNoOfParticipants(request.getNoOfParticipants());

                return lcRequestResponse;
            }).toList();
    }


}
