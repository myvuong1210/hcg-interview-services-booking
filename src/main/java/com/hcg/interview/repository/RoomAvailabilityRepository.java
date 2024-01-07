package com.hcg.interview.repository;

import com.hcg.interview.entity.RoomAvailability;
import com.hcg.interview.entity.RoomRate;
import com.hcg.interview.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {
    List<RoomAvailability> findByRoomTypeAndDate(RoomType roomType, LocalDate date);
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.roomType = :roomType AND ra.date BETWEEN :startDate AND :endDate")
    List<RoomAvailability> findByRoomTypeAndDateBetween(RoomType roomType, LocalDate startDate, LocalDate endDate);
}
