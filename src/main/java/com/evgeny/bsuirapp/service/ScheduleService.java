package com.evgeny.bsuirapp.service;

import com.evgeny.bsuirapp.models.User;
import com.evgeny.bsuirapp.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public void saveUser(User user) {
        scheduleRepository.save(user);
    }

    public boolean isUserExists(Long chatId){
        return scheduleRepository.existsByChatId(chatId);
    }

    public String getUserGroupByChatId(Long chatId){
        Optional<User> userOpt = scheduleRepository.getUserByChatId(chatId);
        if (userOpt.isEmpty()){
            return "Не найдено";
        }
        User user = userOpt.get();
        return String.valueOf(user.getGroupId());

    }
}
