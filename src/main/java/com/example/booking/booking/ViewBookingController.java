package com.example.booking.booking;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.booking.detail.Detail;
import com.example.booking.detail.DetailService;
import com.example.booking.display.Display;
import com.example.booking.display.DisplayService;
import com.example.booking.display.FileStorageService;
import com.example.booking.form.Form;
import com.example.booking.form.FormService;
import com.example.booking.user.User;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class ViewBookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private DetailService detailService;
    @Autowired
    private FormService formService;
    @Autowired
    private DisplayService displayService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/calendar")
    public String showCalendar(@RequestParam(required = false) Long formId, Model model) {
        if (formId != null) {
            Optional<Form> formOpt = formService.getFormById(formId);
            if (formOpt.isPresent()) {
                Form form = formOpt.get();
                model.addAttribute("formTitle", form.getTitle());
                model.addAttribute("formId", formId);
            }
        }
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

    @GetMapping("/form/{formId}/edit-main-page")
    public String showEditMainPage(@PathVariable Long formId, Model model) {
        Optional<Form> formOptional = formService.getFormById(formId);
        if (formOptional.isPresent()) {
            Display display = displayService.getDisplayByFormId(formId);
            if (display == null) {
                display = new Display();
                display.setForm(formOptional.get());
            }
            model.addAttribute("display", display);
            return "editBookingMainPage";
        } else {
            return "redirect:/admin/forms";
        }
    }

    @PostMapping("/form/{formId}/edit-main-page")
    public String updateMainPage(@PathVariable Long formId,
            @Valid @ModelAttribute Display display,
            BindingResult bindingResult,
            @RequestParam("bannerFile") MultipartFile bannerFile) throws IOException {
        if (bindingResult.hasErrors()) {
            return "editBookingMainPage";
        }

        Optional<Form> formOptional = formService.getFormById(formId);
        if (formOptional.isPresent()) {
            display.setForm(formOptional.get());

            if (bannerFile != null && !bannerFile.isEmpty()) {
                String fileName = fileStorageService.storeFile(bannerFile);
                display.setBannerImagePath(fileName);
            }

            displayService.saveDisplay(display);
        }

        return "redirect:/forms";
    }

}
