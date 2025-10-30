package com.example.collectionRequirements.approval;

import com.example.DTOs.ApprovalSubmissionResponse;
import com.example.DTOs.NewApprovalDetails;
import com.example.collectionRequirements.request.Request;
import com.example.collectionRequirements.request.RequestException;
import com.example.collectionRequirements.request.RequestRepository;
import com.example.user.UserException;
import com.example.user.UserInfo;
import com.example.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ApprovalServiceImplementation implements ApprovalService {


    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ApprovalRepository approvalRepository;

    public ApprovalServiceImplementation(RequestRepository requestRepository, UserRepository userRepository, ApprovalRepository approvalRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.approvalRepository = approvalRepository;
    }

    @Override
    public ApprovalSubmissionResponse addApprovalWithRequestId(NewApprovalDetails newApprovalDetails) throws RequestException {



        Request associatedRequest = requestRepository.findById(newApprovalDetails.getRequestId())
                .orElseThrow(() -> new RequestException("Request with ID " + newApprovalDetails.getRequestId() + " not found."));

        UserInfo approver = userRepository.findByCdsID(newApprovalDetails.getApprovedBy())
                .orElseThrow(() -> new UserException("User with CDS ID " + newApprovalDetails.getApprovedBy() + " not found."));

        Approval newApproval;

        if(associatedRequest.getApproval() != null) {
            newApproval = associatedRequest.getApproval();
        } else {
            newApproval = new Approval();
        }
        newApproval.setRequest(associatedRequest);
        newApproval.setApprovalStatus(newApprovalDetails.getApprovalStatus());
        newApproval.setApprovalNotes(newApprovalDetails.getApprovalNotes());
        newApproval.setApprovalDate(LocalDate.now());
        newApproval.setApprovedBy(approver);

        associatedRequest.setApproval(newApproval);
        associatedRequest.setRequestStatus(newApprovalDetails.getApprovalStatus());

        approvalRepository.save(newApproval);
        requestRepository.save(associatedRequest);


        return new ApprovalSubmissionResponse(newApprovalDetails.getApprovalStatus() + " request with ID " + newApprovalDetails.getRequestId() + " successfully.");
    }
}
