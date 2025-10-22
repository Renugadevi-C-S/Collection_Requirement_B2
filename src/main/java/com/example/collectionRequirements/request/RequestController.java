package com.example.collectionRequirements.request;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/requests")
public class RequestController {

    private RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/newRequest")
    public Request newrequest(@RequestBody Request newRequest) throws GeneralException
    {
        return requestService.createRequest(newRequest);
    }

    @GetMapping("/all")
    public List<Request> getAllRequests() throws GeneralException
    {
        return requestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public Request getRequestById(@PathVariable Long id)
    {
        return requestService.getRequestById(id);
    }

    @GetMapping("/{status}")
    public List<Request> getRequestByStatus(@PathVariable String status)
    {
        return requestService.getRequestByStatus(status);
    }


}
