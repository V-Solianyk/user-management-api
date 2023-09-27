package com.example.usermanagementapi.service.impl;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import com.example.usermanagementapi.service.UserService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${user.age.limit}")
    private int userAgeLimit;
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        LocalDate minimumRegistrationAge = LocalDate.now().minusYears(userAgeLimit);
        if (user.getBirthDate().isBefore(minimumRegistrationAge)) { //todo think about months
            return userRepository.save(user);
        }
        throw new RuntimeException("Can't register the user who is younger than 18 years old");
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with this id: " + id));
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
            oldUser.setAddress(user.getPhoneNumber());
        }
        return userRepository.save(oldUser);
    }

    @Override
    public void delete(Long id) {
        get(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsersByPriceBetween(LocalDate from, LocalDate to, PageRequest pageRequest) {
        return userRepository.findByBirthDateBetween(from, to, pageRequest);
    }
}
