package com.example.collectionRequirements.request;


import com.example.DTOs.LCRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImplementation implements RequestService {

    private RequestRepository requestRepository;

    @Autowired
    public RequestServiceImplementation(RequestRepository requestRepository)
    {
        this.requestRepository = requestRepository;
    }

    public Request createRequest(Request newRequest) throws RequestException
    {
        newRequest.setRequestDate(LocalDate.now());
        newRequest.setRequestStatus("Submitted");
        return requestRepository.save(newRequest);
    }
    public LCRequestResponse getRequestById(long requestId) throws RequestException {
        Request fetchedRequest = requestRepository.findById(requestId)
                .orElseThrow(()->new RequestException("Request Not Found"));

        LCRequestResponse lcRequestResponse = new LCRequestResponse();

        lcRequestResponse.setRequestId(fetchedRequest.getRequestId());
        lcRequestResponse.setRequestStatus(fetchedRequest.getRequestStatus());
        lcRequestResponse.setRequestDate(fetchedRequest.getRequestDate());
        lcRequestResponse.setDepartment(fetchedRequest.getDepartment().getDepartmentName());
        lcRequestResponse.setJustification(fetchedRequest.getJustification());
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




}
