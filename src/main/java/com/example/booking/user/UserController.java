package com.example.booking.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("userSignupDTO", new UserSignupDTO());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute UserSignupDTO userSignupDTO, RedirectAttributes redirectAttributes, Model model) {
        try {
            User savedUser = userService.signup(userSignupDTO);
            redirectAttributes.addAttribute("userId", savedUser.getuId());
            return "redirect:/booking/main";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userLoginDTO", new UserLoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserLoginDTO userLoginDTO, RedirectAttributes redirectAttributes, Model model) {
        try {
            User user = userService.login(userLoginDTO);
            redirectAttributes.addAttribute("userId", user.getuId());
            return "redirect:/booking/main";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }
}

