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
    public List<Map<String, Object>> getBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        List<Map<String, Object>> events = new ArrayList<>();

        for (Booking booking : bookings) {
            for (Detail detail : booking.getDetails()) {
                if (detail instanceof UserSelect) {
                    UserSelect userSelect = (UserSelect) detail;
                    Answer answer = userSelect.getAnswer();

                    if (answer instanceof DateAnswer) {
                        DateAnswer dateAnswer = (DateAnswer) answer;
                        Map<String, Object> event = new HashMap<>();
                        event.put("title", "Booking: " + booking.getUser().getName() + " (" + booking.getStatus() + ")");
                        event.put("start", dateAnswer.getDate());
                        event.put("url", "/admin/booking/" + booking.getbId());
                        events.add(event);
                    } else if (answer instanceof TimeSlot) {
                        TimeSlot timeSlot = (TimeSlot) answer;
                        Map<String, Object> event = new HashMap<>();
                        event.put("title", "Booking: " + booking.getUser().getName() + " (" + booking.getStatus() + ")");
                        event.put("start", combineDateTime(booking.getDate(), timeSlot.getStartTime()));
                        event.put("end", combineDateTime(booking.getDate(), timeSlot.getEndTime()));
                        event.put("url", "/admin/booking/" + booking.getbId());
                        events.add(event);
                    }
                }
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
