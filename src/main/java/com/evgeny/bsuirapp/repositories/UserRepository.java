package com.evgeny.bsuirapp.repositories;

import com.evgeny.bsuirapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByChatId(Long chatId);
    Optional<User> getUserByChatId(Long chatId);
}
