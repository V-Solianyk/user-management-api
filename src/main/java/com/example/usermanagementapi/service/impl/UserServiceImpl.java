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
        return saveValidUser(user);
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with"
                        + " this id: " + id));
    }

    @Override
    public User update(User user, Long id) {
        get(id);
        user.setId(id);
        return saveValidUser(user);
    }

    @Override
    public User particularUpdateUser(User user, Long id) {
        User oldUser = get(id);
        updateUserFieldsIfProvided(user, oldUser);
        return saveValidUser(oldUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsersByBirthDateBetween(LocalDate from, LocalDate to,
                                                    PageRequest pageRequest) {
        return userRepository.findByBirthDateBetween(from, to, pageRequest);
    }

    private User saveValidUser(User user) {
        if (isValidUserAge(user)) {
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("Can't register the user who is younger"
                + " than 18 years old");
    }

    private boolean isValidUserAge(User user) {
        LocalDate minimumRegistrationAge = LocalDate.now().minusYears(userAgeLimit);
        return user.getBirthDate().isEqual(minimumRegistrationAge)
                || user.getBirthDate().isBefore(minimumRegistrationAge);
    }

    private void updateUserFieldsIfProvided(User user, User oldUser) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getAddress() != null && !user.getAddress().isBlank()) {
            oldUser.setAddress(user.getAddress());
        }
        if (user.getFirstName() != null && !user.getFirstName().isBlank()) {
            oldUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null && !user.getLastName().isBlank()) {
            oldUser.setLastName(user.getLastName());
        }
        if (user.getBirthDate() != null) {
            oldUser.setBirthDate(user.getBirthDate());
        }
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            oldUser.setPhoneNumber(user.getPhoneNumber());
        }
    }
}
