package com.example.booking.display;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplayRepository extends JpaRepository<Display, Long> {
    Display findByFormFId(Long formId);
}
