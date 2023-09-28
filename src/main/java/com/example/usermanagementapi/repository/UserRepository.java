package com.example.usermanagementapi.repository;

import com.example.usermanagementapi.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    List<User> findByBirthDateBetween(LocalDate fromDate, LocalDate toDate, Pageable pageable);
}
