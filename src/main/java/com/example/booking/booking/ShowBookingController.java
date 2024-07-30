package com.example.booking.booking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.answer.Answer;
import com.example.booking.date_answer.DateAnswer;
import com.example.booking.detail.Detail;
import com.example.booking.time_answer.TimeSlot;
import com.example.booking.user_select.UserSelect;

@RestController
@RequestMapping("/api/bookings")
public class ShowBookingController {
    @Autowired
    private BookingService bookingService;


    @GetMapping
    public List<Map<String, Object>> getBookings(@RequestParam(required = false) Long formId) {
        List<Booking> bookings;
        if (formId != null) {
            bookings = bookingService.getBookingsByFormId(formId);
        } else {
            bookings = bookingService.getAllBookings();
        }
        return convertBookingsToEvents(bookings);
    }



    private List<Map<String, Object>> convertBookingsToEvents(List<Booking> bookings) {
        List<Map<String, Object>> events = new ArrayList<>();
        for (Booking booking : bookings) {
            Date bookingDate = null;
            TimeSlot bookingTimeSlot = null;

            for (Detail detail : booking.getDetails()) {
                if (detail instanceof UserSelect) {
                    UserSelect userSelect = (UserSelect) detail;
                    Answer answer = userSelect.getAnswer();
                    if (answer instanceof DateAnswer) {
                        DateAnswer dateAnswer = (DateAnswer) answer;
                        bookingDate = dateAnswer.getDate();
                    } else if (answer instanceof TimeSlot) {
                        bookingTimeSlot = (TimeSlot) answer;
                    }
                }
            }

            if (bookingDate != null) {
                Map<String, Object> event = new HashMap<>();
                String formTitle = booking.getForm().getTitle();
                event.put("title", formTitle + " booking: " + booking.getUser().getName() + " (" + booking.getStatus() + ")");
                event.put("url", "/admin/booking/" + booking.getbId());

                if (bookingTimeSlot != null) {
                    event.put("start", combineDateTime(bookingDate, bookingTimeSlot.getStartTime()));
                    event.put("end", combineDateTime(bookingDate, bookingTimeSlot.getEndTime()));
                } else {
                    event.put("start", bookingDate);
                }

                events.add(event);
            }
        }
        return events;
    }

    private Date combineDateTime(Date date, Date time) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);

        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(time);

        dateCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        dateCalendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));

        return dateCalendar.getTime();
    }
}
