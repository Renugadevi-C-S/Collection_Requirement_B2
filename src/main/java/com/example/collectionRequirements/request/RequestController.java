package com.example.collectionRequirements.request;



import com.example.DTOs.RequestUpdate;
import com.example.DTOs.RequestsViewDetails;
import com.example.DTOs.RequestDetails;
import com.example.DTOs.RequestSubmitResponse;
import com.example.department.DepartmentException;
import com.example.user.UserException;
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



    @PostMapping("/newRequest")
    public RequestSubmitResponse submitNewRequest(@RequestBody RequestDetails requestDetails) {
        return requestService.submitNewRequest(requestDetails);
    }

    @GetMapping("/all")
    public List<RequestsViewDetails> getAllRequests() throws RequestException
    {
        return requestService.getAllRequests();
    }


    @GetMapping("/requestor/{cdsId}")
    public List<RequestsViewDetails> getRequestByCdsId(@PathVariable String cdsId) throws RequestException {
        return requestService.getRequestByCdsId(cdsId);
    }

    @GetMapping("/status/{status}")
    public List<Request> getRequestByStatus(@PathVariable String status) throws RequestException {
        return requestService.getRequestByStatus(status);
    }

    @PutMapping("/update/{requestId}")
    public RequestSubmitResponse updateRequest(@PathVariable Long requestId, @RequestBody RequestUpdate requestUpdateDTO) throws UserException, DepartmentException, RequestException {
        return requestService.updateRequest(requestId, requestUpdateDTO);
    }

}
