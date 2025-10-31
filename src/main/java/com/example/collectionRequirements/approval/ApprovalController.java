package com.example.collectionRequirements.approval;

import com.example.DTOs.ApprovalSubmissionResponse;
import com.example.DTOs.NewApprovalDetails;
import com.example.collectionRequirements.request.RequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8080"})
@RequestMapping("api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    @Autowired
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping("/newApproval")
    public ApprovalSubmissionResponse addApproval(@RequestBody NewApprovalDetails newApprovalDetails) throws RequestException {

        return approvalService.addApprovalWithRequestId(newApprovalDetails);
    }
}
