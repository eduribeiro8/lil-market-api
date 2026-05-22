package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.OneTimeJobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OneTimeJobExecutionRepository extends JpaRepository<OneTimeJobExecution, String> {
}
