package com.example.booking.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User signup(UserSignupDTO userSignupDTO) {
        if (emailExists(userSignupDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(userSignupDTO.getName());
        user.setEmail(userSignupDTO.getEmail());
        user.setTel(userSignupDTO.getTel());
        user.setPassword(userSignupDTO.getPassword());
        return userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User login(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByEmail(userLoginDTO.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found with email: " + userLoginDTO.getEmail()));

        if (!user.getPassword().equals(userLoginDTO.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}