package com.hasan.smartcontactmanager.repositories;

import com.hasan.smartcontactmanager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
