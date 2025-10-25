package com.example.collectionRequirements.request;



import com.example.DTOs.RequestsViewDetails;
import com.example.DTOs.RequestDetails;
import com.example.DTOs.RequestSubmitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8080"})
@RequestMapping("api/requests")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

//    @PostMapping("/newRequest/{requestorCdsId}/{deptName}")
//    public Request newRequest(@RequestBody Request newRequest, @PathVariable String requestorCdsId ,@PathVariable String deptName) throws RequestException {
//        return requestService.createRequest(newRequest, requestorCdsId, deptName);
//    }

    @PostMapping("/newRequest")
    public RequestSubmitResponse submitNewRequest(@RequestBody RequestDetails requestDetails) {
        return requestService.submitNewRequest(requestDetails);
    }

    @GetMapping("/all")
    public List<Request> getAllRequests() throws RequestException
    {
        return requestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public RequestsViewDetails getRequestById(@PathVariable Long requestId) throws RequestException {
        return requestService.getRequestById(requestId);
    }

    @GetMapping("/requestor/{cdsId}")
    public List<RequestsViewDetails> getRequestByCdsId(@PathVariable String cdsId) throws RequestException {
        return requestService.getRequestByCdsId(cdsId);
    }

    @GetMapping("/status/{status}")
    public List<Request> getRequestByStatus(@PathVariable String status) throws RequestException {
        return requestService.getRequestByStatus(status);
    }


}
