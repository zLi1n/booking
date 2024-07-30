package com.example.booking.form;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/forms")
public class FormController {
    @Autowired
    private FormService formService;

    @GetMapping
    public String listForms(Model model) {
        List<Form> forms = formService.getAllForms();
        model.addAttribute("forms", forms);
        return "formList";
    }

    @GetMapping("/add")
    public String addFormForm(Model model) {
        model.addAttribute("form", new Form());
        return "addForm";
    }

    @PostMapping("/add")
    public String addForm(@ModelAttribute Form form) {
        Form savedForm = formService.saveForm(form);
        return "redirect:/questions/add?formId=" + savedForm.getfId();
    }
}
