package com.example.UserManagementAPI.repository;

import com.example.UserManagementAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
