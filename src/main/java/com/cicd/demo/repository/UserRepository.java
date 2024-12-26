package com.cicd.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cicd.demo.model.*;

    public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

