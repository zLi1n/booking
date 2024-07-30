package com.example.booking.display;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisplayService {
    @Autowired
    private DisplayRepository displayRepository;

    public Display getDisplayByFormId(Long formId) {
        return displayRepository.findByFormFId(formId);
    }

    public Display saveDisplay(Display display) {
        return displayRepository.save(display);
    }
}
