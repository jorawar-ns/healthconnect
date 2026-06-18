package com.healthconnect.claims.repository;

import com.healthconnect.claims.entity.ClaimLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimLineItemRepository extends JpaRepository<ClaimLineItem, Long> {

    List<ClaimLineItem> findByClaimId(Long claimId);
}
