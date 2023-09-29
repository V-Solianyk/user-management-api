package com.example.usermanagementapi.service.impl;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import com.example.usermanagementapi.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final int userAgeLimit;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(@Value("${user.age.limit}") int userAgeLimit,
                           UserRepository userRepository) {
        this.userAgeLimit = userAgeLimit;
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        if (userRepository.findUserByEmail(user.getEmail()).isEmpty()) {
            if (isUserEligibleToRegister(user)) {
                return userRepository.save(user);
            }
            throw new IllegalArgumentException("Can't register the user who is younger"
                    + " than 18 years old");
        }
        throw new IllegalArgumentException("A user with this email already exists.");
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with"
                        + " this id: " + id));
    }

    @Override
    public User update(User user, Long id) {
        User oldUser = get(id);
        oldUser.setFirstName(user.getFirstName());
        oldUser.setLastName(user.getLastName());
        oldUser.setEmail(user.getEmail());
        oldUser.setBirthDate(user.getBirthDate());
        if (user.getAddress() != null) {
            oldUser.setAddress(user.getAddress());
        }
        if (user.getPhoneNumber() != null) {
            oldUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (isUserEligibleToRegister(user)) {
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("Can't register the user who is younger"
                + " than 18 years old");
    }

    @Override
    public void delete(Long id) {
        get(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsersByBirthDateBetween(LocalDate from, LocalDate to,
                                                    PageRequest pageRequest) {
        return userRepository.findByBirthDateBetween(from, to, pageRequest);
    }

    private boolean isUserEligibleToRegister(User user) {
        LocalDate minimumRegistrationAge = LocalDate.now().minusYears(userAgeLimit);
        return user.getBirthDate().isBefore(minimumRegistrationAge);
    }
}
