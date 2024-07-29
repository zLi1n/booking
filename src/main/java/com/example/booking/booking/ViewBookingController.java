package com.example.booking.booking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.booking.detail.Detail;
import com.example.booking.detail.DetailService;
import com.example.booking.user.User;

@Controller
@RequestMapping("/admin")
public class ViewBookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private DetailService detailService;

    @GetMapping("/calendar")
    public String showCalendar() {
        return "viewBooking";
    }

    @GetMapping("/booking/{bookingId}")
    public String showBookingDetails(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingService.getBookingById(bookingId);
        User user = booking.getUser();
        List<Detail> details = detailService.getDetailsByBooking(booking);
        booking.setDetails(details);

        model.addAttribute("booking", booking);
        model.addAttribute("user", user);
        return "viewBookingDetails";
    }

    @PostMapping("/booking/updateStatus")
    public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam String status) {
        bookingService.updateBookingStatus(bookingId, status);
        return "redirect:/admin/booking/" + bookingId;
    }
}
