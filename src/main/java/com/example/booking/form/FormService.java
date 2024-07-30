package com.example.booking.form;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormService {
    @Autowired
    private FormRepository formRepository;

    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    public Form saveForm(Form form) {
        return formRepository.save(form);
    }

    public Optional<Form> getFormById(Long id) {
        return formRepository.findById(id);
    }

    public Form getFirstForm() {
        List<Form> forms = formRepository.findAll();
        return forms.isEmpty() ? null : forms.get(0);
    }
}
