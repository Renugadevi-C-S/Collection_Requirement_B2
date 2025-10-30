package com.example.collectionRequirements.approval;

import com.example.DTOs.ApprovalSubmissionResponse;
import com.example.DTOs.NewApprovalDetails;
import com.example.collectionRequirements.request.RequestException;

public interface ApprovalService {

    ApprovalSubmissionResponse addApprovalWithRequestId(NewApprovalDetails newApprovalDetails) throws RequestException;


}
