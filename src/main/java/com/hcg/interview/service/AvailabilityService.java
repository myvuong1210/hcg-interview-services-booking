package com.hcg.interview.service;

import com.hcg.interview.entity.RoomAvailability;
import com.hcg.interview.entity.RoomType;
import com.hcg.interview.repository.RoomAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityService {

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;

    public boolean isRoomTypeAvailable(RoomType roomType, LocalDate date) {
        List<RoomAvailability> availabilities = roomAvailabilityRepository.findByRoomTypeAndDate(roomType, date);
        return availabilities.stream().allMatch(availability -> availability.getAvailableRooms() > 0);
    }

    public void updateRoomAvailability(RoomType roomType, LocalDate startDate, LocalDate endDate) {
        List<RoomAvailability> availabilities = roomAvailabilityRepository.findByRoomTypeAndDateBetween(roomType, startDate, endDate);
        for (RoomAvailability availability : availabilities) {
            availability.setDate(startDate);
            availability.setAvailableRooms(availability.getAvailableRooms() - 1);
            roomAvailabilityRepository.save(availability);
        }
    }
}
