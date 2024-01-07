package com.hcg.interview.repository;

import com.hcg.interview.entity.Guest;
import com.hcg.interview.entity.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long>, JpaSpecificationExecutor<RatePlan> {
}
