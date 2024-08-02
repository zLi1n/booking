package com.example.booking.form;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/{formId}/structure")
    @ResponseBody
    public Map<String, Object> getFormStructure(@PathVariable Long formId) {
        return formService.getFormStructure(formId);
    }

    @GetMapping("/{formId}/structure-view")
    public String viewFormStructure(@PathVariable Long formId, Model model) {
        Form form = formService.getFormById(formId).orElseThrow(() -> new RuntimeException("Form not found"));
        model.addAttribute("form", form);
        return "formStructure";
    }

    @PostMapping("/{id}/delete")
    public String deleteForm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            formService.deleteForm(id);
            redirectAttributes.addFlashAttribute("message", "Form deleted successfully");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Form not found");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "This form cannot be deleted as it has associated bookings.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                    "An error occurred while deleting the form: " + e.getMessage());
        }
        return "redirect:/forms";
    }

    @GetMapping("/{id}/edit-title")
    public String editFormTitleForm(@PathVariable Long id, Model model) {
        Optional<Form> formOpt = formService.getFormById(id);
        if (formOpt.isPresent()) {
            model.addAttribute("form", formOpt.get());
            return "editFormTitle";
        }
        return "redirect:/forms";
    }

    @PostMapping("/{id}/edit-title")
    public String editFormTitle(@PathVariable Long id, @RequestParam String title,
            RedirectAttributes redirectAttributes) {
        Optional<Form> formOpt = formService.getFormById(id);
        if (formOpt.isPresent()) {
            Form form = formOpt.get();
            form.setTitle(title);
            formService.saveForm(form);
            redirectAttributes.addFlashAttribute("message", "Form title updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Form not found");
        }
        return "redirect:/forms";
    }
}
