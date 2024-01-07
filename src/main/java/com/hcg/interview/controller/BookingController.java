package com.hcg.interview.controller;

import com.hcg.interview.dto.BookingDTO;
import com.hcg.interview.entity.Booking;
import com.hcg.interview.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("booking")
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO) {
        try {
            Long roomTypeId = bookingDTO.getRoomType().getRoomTypeId();
            String ratePlanId = bookingDTO.getRatePlan().getRatePlanId();
            LocalDate arrivalDate = LocalDate.parse(bookingDTO.getStartDate());
            LocalDate departureDate = LocalDate.parse(bookingDTO.getEndDate());
            String firstName = bookingDTO.getGuest().getFirstName();
            String lastName = bookingDTO.getGuest().getLastName();
            String guestEmail = bookingDTO.getGuest().getEmail();
            Booking booking = bookingService.createBooking(roomTypeId, ratePlanId, arrivalDate, departureDate, firstName, lastName, guestEmail);
            return new ResponseEntity<>(booking, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("change-booking/{bookingId}/change-dates")
    public ResponseEntity<String> changeBookingDates(
            @PathVariable Long bookingId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate newArrivalDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate newDepartureDate) {
        try {
            bookingService.changeBookingDates(bookingId, newArrivalDate, newDepartureDate);
            return ResponseEntity.ok("Booking dates changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("cancel-booking/{bookingNumber}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingNumber) {
        try {
            bookingService.cancelBooking(bookingNumber);
            return new ResponseEntity<>("Booking canceled successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
