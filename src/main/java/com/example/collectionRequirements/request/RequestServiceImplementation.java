package com.example.collectionRequirements.request;


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

    public Request createRequest(Request newRequest) throws GeneralException
    {
        newRequest.setRequestDate(LocalDate.now());
        newRequest.setRequestStatus("Submitted");
        return requestRepository.save(newRequest);
    }
    public Request getRequestById(long requestId) throws GeneralException
    {
        Optional<Request> findRequest=requestRepository.findById(requestId);
        if(findRequest.isEmpty())
        {
            System.out.println("Request Not Found");
        }
        return findRequest.get();
    }
    public List<Request> getAllRequests() throws GeneralException
    {
        return requestRepository.findAll();
    }
    public List<Request> getRequestByStatus(String status) throws GeneralException
    {
        return requestRepository.findByRequestStatus(status);
    }




}
