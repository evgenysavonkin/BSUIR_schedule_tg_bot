package com.evgeny.bsuirapp.service;

import com.evgeny.bsuirapp.models.User;

public interface UserService {
    void saveUser(User user);
    String getUserGroupByChatId(long chatId);
    boolean isUserExists(long chatId);
}
