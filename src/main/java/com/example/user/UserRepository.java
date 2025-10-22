package com.example.user;

import com.example.collectionRequirements.approval.Approval;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserInfo, Long> {

}
