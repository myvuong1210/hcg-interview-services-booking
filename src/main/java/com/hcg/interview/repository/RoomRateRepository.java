package com.hcg.interview.repository;

import com.hcg.interview.entity.RoomRate;
import com.hcg.interview.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRateRepository extends JpaRepository<RoomRate, Long> {
    List<RoomRate> findByRoomTypeAndDateBetween(RoomType roomType, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM RoomRate r WHERE r.roomType.roomTypeId = :roomTypeId " +
            "AND r.ratePlan.ratePlanId = :ratePlanId AND r.date BETWEEN :startDate AND :endDate")
    List<RoomRate> findByRoomTypeIdAndRatePlanIdAndDateBetween(
            @Param("roomTypeId") Long roomTypeId,
            @Param("ratePlanId") String ratePlanId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}

