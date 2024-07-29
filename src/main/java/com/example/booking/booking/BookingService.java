package com.example.booking.booking;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.booking.user.User;

import jakarta.transaction.Transactional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public Booking createBooking(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setDate(new Date());
        booking.setStatus("pending");
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public void saveBooking(Booking booking) {
        bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUser(user);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Transactional
    public void updateBookingStatus(Long bookingId, String status) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }
}
