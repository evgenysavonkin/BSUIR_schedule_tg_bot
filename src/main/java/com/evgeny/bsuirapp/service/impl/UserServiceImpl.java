package com.evgeny.bsuirapp.service.impl;

import com.evgeny.bsuirapp.models.User;
import com.evgeny.bsuirapp.repositories.UserRepository;
import com.evgeny.bsuirapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository scheduleRepository;

    @Autowired
    public UserServiceImpl(UserRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    @Override
    public void saveUser(User user) {
        scheduleRepository.save(user);
    }

    @Override
    public String getUserGroupByChatId(long chatId) {
        Optional<User> userOpt = scheduleRepository.getUserByChatId(chatId);
        if (userOpt.isEmpty()) {
            return "Не найдено";
        }
        User user = userOpt.get();
        return String.valueOf(user.getGroupId());
    }

    @Override
    public boolean isUserExists(long chatId) {
        return scheduleRepository.existsByChatId(chatId);
    }


}
