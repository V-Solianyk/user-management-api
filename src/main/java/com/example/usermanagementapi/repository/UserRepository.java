package com.example.usermanagementapi.repository;

import com.example.usermanagementapi.model.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByBirthDateBetween(LocalDate fromDate, LocalDate toDate, Pageable pageable);
}
