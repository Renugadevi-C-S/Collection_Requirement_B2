package com.example.user;

import com.example.collectionRequirements.approval.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByCdsID(String cdsID);

    boolean existsByCdsID(String cdsId);
}
