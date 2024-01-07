package com.hcg.interview.service;

import com.hcg.interview.entity.Booking;
import com.hcg.interview.entity.Guest;
import com.hcg.interview.entity.RatePlan;
import com.hcg.interview.entity.RoomAvailability;
import com.hcg.interview.entity.RoomRate;
import com.hcg.interview.entity.RoomType;
import com.hcg.interview.repository.BookingRepository;
import com.hcg.interview.repository.GuestRepository;
import com.hcg.interview.repository.RatePlanRepository;
import com.hcg.interview.repository.RoomAvailabilityRepository;
import com.hcg.interview.repository.RoomRateRepository;
import com.hcg.interview.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RatePlanRepository ratePlanRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @Autowired
    private RoomRateRepository roomRateRepository;

    @Autowired
    private AvailabilityService roomAvailabilityService;

    public Booking createBooking(Long roomTypeId, String ratePlanId, LocalDate arrivalDate, LocalDate departureDate,
                                 String firstName, String lastName, String guestEmail) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new EntityNotFoundException("RoomType not found with ID" + roomTypeId));
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new EntityNotFoundException("RoomPlan not found with ID" + ratePlanId));

        if (!roomAvailabilityService.isRoomTypeAvailable(roomType, arrivalDate)) {
            throw new RuntimeException("Selected room type is not available for the specified dates");
        }

        BigDecimal totalPrice = calculateTotalPrice(roomTypeId, ratePlanId, arrivalDate, departureDate);

        validateGuestInformation(firstName, lastName, guestEmail);
        Guest guest = createGuest(firstName, lastName, guestEmail);

        Booking booking = new Booking();
        booking.setRoomType(roomType);
        booking.setRatePlan(ratePlan);
        booking.setPrice(totalPrice);
        booking.getGuests().add(guest);
        bookingRepository.save(booking);

        updateRoomAvailability(roomType, arrivalDate, departureDate);
        return booking;
    }

    private BigDecimal calculateTotalPrice(Long roomTypeId, String ratePlanId, LocalDate startDate, LocalDate endDate) {
        List<RoomRate> roomRates = roomRateRepository.findByRoomTypeIdAndRatePlanIdAndDateBetween(roomTypeId, ratePlanId, startDate, endDate);
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (RoomRate roomRate : roomRates) {
            totalPrice = totalPrice.add(roomRate.getPrice());
        }
        return totalPrice;
    }

    private Guest createGuest(String firstName, String lastName, String guestEmail) {
        Guest guest = new Guest();
        guest.setFirstName(firstName);
        guest.setLastName(lastName);
        guest.setEmail(guestEmail);
        return guestRepository.save(guest);
    }

    private void validateGuestInformation(String firstName, String lastName, String guestEmail) {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() ||
                guestEmail == null || guestEmail.isEmpty()) {
            throw new RuntimeException("Invalid guest information. Please provide a valid name and email.");
        }
    }

    private void updateRoomAvailability(RoomType roomType, LocalDate startDate, LocalDate endDate) {
        List<RoomAvailability> availabilities = roomAvailabilityRepository.findByRoomTypeAndDateBetween(roomType, startDate, endDate);
        for (RoomAvailability availability : availabilities) {
            availability.setAvailableRooms(availability.getAvailableRooms() - 1);
            roomAvailabilityRepository.save(availability);
        }
    }

    public void changeBookingDates(Long bookingId, LocalDate newArrivalDate, LocalDate newDepartureDate) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        RatePlan ratePlan = new RatePlan();
        ratePlan.setStartDate(newArrivalDate);
        ratePlan.setEndDate(newDepartureDate);
        booking.setBookingNumber(bookingId);
        booking.setRatePlan(ratePlan);
        bookingRepository.save(booking);
        roomAvailabilityService.updateRoomAvailability(booking.getRoomType(), newArrivalDate, newDepartureDate);
    }

    public void cancelBooking(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isEmpty()) {
            throw new RuntimeException("Booking not found with booking number: " + id);
        }
        bookingRepository.delete(booking.get());
        updateRoomAvailabilityAfterCancellation(booking.get());
    }

    private void updateRoomAvailabilityAfterCancellation(Booking booking) {
        RoomType roomType = booking.getRoomType();
        LocalDate startDate = booking.getRatePlan().getStartDate();
        LocalDate endDate = booking.getRatePlan().getEndDate().minusDays(1);
        roomAvailabilityService.updateRoomAvailability(roomType, startDate, endDate);
    }
}
