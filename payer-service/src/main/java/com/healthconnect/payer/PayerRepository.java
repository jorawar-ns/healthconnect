package com.healthconnect.payer;

import com.healthconnect.payer.entity.Payer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PayerRepository extends JpaRepository<Payer, String>, JpaSpecificationExecutor<Payer> {
    boolean existsByPayerId(String payerId);
    Optional<Payer> findByPayerId(String payerId);
}
