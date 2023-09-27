package com.example.usermanagementapi.repository;

import com.example.usermanagementapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
