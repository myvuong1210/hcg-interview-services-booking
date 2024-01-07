package com.hcg.interview.repository;

import com.hcg.interview.entity.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, String> {
    Optional<RatePlan> findById(String id);
}
