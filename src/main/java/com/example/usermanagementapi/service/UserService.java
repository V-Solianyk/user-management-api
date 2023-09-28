package com.example.usermanagementapi.service;

import com.example.usermanagementapi.model.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;

public interface UserService {
    User create(User user);

    User get(Long id);

    User update(User user, Long id);

    void delete(Long id);

    List<User> getAllUsersByBirthDateBetween(LocalDate from, LocalDate to, PageRequest pageRequest);
}
