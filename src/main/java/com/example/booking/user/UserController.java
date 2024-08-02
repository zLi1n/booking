package com.example.booking.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booking.form.Form;
import com.example.booking.form.FormService;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private FormService formService;

    @GetMapping("/signup")
public String showSignupForm(@RequestParam(required = false) Long formId, Model model) {
    model.addAttribute("userSignupDTO", new UserSignupDTO());
    model.addAttribute("formId", formId);
    return "signup";
}

@GetMapping("/login")
public String showLoginForm(@RequestParam(required = false) Long formId, Model model) {
    model.addAttribute("userLoginDTO", new UserLoginDTO());
    model.addAttribute("formId", formId);
    return "login";
}
@PostMapping("/signup")
public String signup(@ModelAttribute UserSignupDTO userSignupDTO, 
                     @RequestParam(required = false) Long formId,
                     RedirectAttributes redirectAttributes, 
                     Model model) {
    try {
        User savedUser = userService.signup(userSignupDTO);
        redirectAttributes.addAttribute("userId", savedUser.getuId());
        
        if (formId != null) {
            redirectAttributes.addAttribute("formId", formId);
        } else {
            Form firstForm = formService.getFirstForm();
            if (firstForm != null) {
                redirectAttributes.addAttribute("formId", firstForm.getfId());
            }
        }
        
        return "redirect:/booking/main";
    } catch (RuntimeException e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "signup";
    }
}

@PostMapping("/login")
public String login(@ModelAttribute UserLoginDTO userLoginDTO, 
                    @RequestParam(required = false) Long formId,
                    RedirectAttributes redirectAttributes, 
                    Model model) {
    try {
        User user = userService.login(userLoginDTO);
        redirectAttributes.addAttribute("userId", user.getuId());
        
        if (formId != null) {
            redirectAttributes.addAttribute("formId", formId);
        } else {
            Form firstForm = formService.getFirstForm();
            if (firstForm != null) {
                redirectAttributes.addAttribute("formId", firstForm.getfId());
            }
        }
        
        return "redirect:/booking/main";
    } catch (RuntimeException e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "login";
    }
}
}

