package com.evgeny.bsuirapp.bot;

import com.evgeny.bsuirapp.models.User;
import com.evgeny.bsuirapp.service.ScheduleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class BSUIRScheduleBot extends TelegramLongPollingBot {
    //    @Value("${bot.name}")
//    private String botName;
//
//    @Value("${bot.token}")
//    private String botToken;
    private static final Logger LOG = Logger.getLogger(BSUIRScheduleBot.class);

    private static final String START = "/start";
    private static final String REG = "/reg";
    private static final String MY_SCHEDULE = "/my_schedule";
    private static final String OTHER_SCHEDULE = "/other_schedule";

    private final ScheduleService scheduleService;

    private enum UserState {
        IDLE,
        REGISTRATION,
        REGISTERED,
        CHECK_MY_SCHEDULE,
        CHECK_OTHER_SCHEDULE
    }

    private enum ScheduleState {
        MY,
        OTHER
    }

    private Map<Long, UserState> userStates = new HashMap<>();
    private Map<Long, ScheduleState> userScheduleStates = new HashMap<>();

    @Autowired
    public BSUIRScheduleBot(@Value("${bot.token}") String botToken, ScheduleService scheduleService) {
        super(botToken);
        this.scheduleService = scheduleService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        LOG.debug(update.getMessage().getText());
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var response = new SendMessage();
        response.setChatId(chatId);
        UserState userState = userStates.getOrDefault(chatId, UserState.IDLE);
        switch (message) {
            case START -> {
                var username = update.getMessage().getChat().getUserName();
                if (!scheduleService.isUserExists(chatId)) {
                    startCommand(response, username);
                } else {
                    startCommand(username, response);
                }
            }
            case REG -> {
                if (scheduleService.isUserExists(chatId)) {
                    sendMessageToClient(response, "Вы уже зарегистрированы");
                } else if (userState == userState.IDLE) {
                    regCommand(response, chatId);
                }
            }
            case MY_SCHEDULE -> {
                myScheduleCommand(chatId);
                userScheduleStates.put(chatId, ScheduleState.MY);
            }

            case OTHER_SCHEDULE -> {

            }
            default -> {
                switch (userState) {
                    case REGISTRATION -> processRegistration(response, update, chatId);
                    case CHECK_MY_SCHEDULE -> processMySchedule(response, update, chatId);
                }
            }
        }
    }

    private void myScheduleCommand(Long chatId) {
        userStates.put(chatId, UserState.CHECK_MY_SCHEDULE);
    }

    private void processMySchedule(SendMessage sendMessage, Update update, Long chatId) {
        var text = """
                Номер вашей группы %s
                Доступные команды:
                           
                /today
                /tomorrow
                /week
                                
                """;
        var groupId = scheduleService.getUserByChatId(chatId);
        var formattedText = String.format(text, groupId);
        sendMessageToClient(sendMessage, formattedText);
        LOG.debug(groupId);
    }

    private static boolean processGroup(String message, Long chatId) {
        if (message.length() != 6) {
            return false;
        }
        try {
            Integer.parseInt(message);
            return true;
        } catch (NumberFormatException e) {
            LOG.error("Number format exception for chatId " + chatId);
            return false;
        }
    }

    private void sendMessageToClient(SendMessage sendMessage, String text) {
        if (sendMessage == null) {
            return;
        }
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void startCommand(SendMessage sendMessage, String username) {
        var text = """
                Добро пожаловать, %s!
                                
                Данный телеграмм-бот предоставляет функционал по нахождению расписания занятий БГУИР по номеру группы.
                Вы можете зарегистрироваться, чтобы ваше расписание вашей группы было закреплено за вашим телеграмм-аккаунтом. 
                Для этого вам нужно ввести лишь номер своей группы, чтобы в следующий раз сразу найти своё расписание.
                Также вы можете найти расписание интересующей группы без регистрации.
                                
                Доступные команды:
                                
                /reg
                /my_schedule - для просмотра расписания своей группы
                /other_schedule - для просмотра расписания любой группы
                                      
                Приятного пользования!
                По всем вопросам обращайтесь к разработчику @eugenezkh
                """;
        var formattedText = String.format(text, username);
        sendMessageToClient(sendMessage, formattedText);
    }

    private void startCommand(String username, SendMessage sendMessage) {
        var text = """
                %s, вы уже были зарегистрированы!
                                       
                Доступные команды:
                                
                /reg
                /my_schedule
                /other_schedule
                                        
                Приятного пользования!
                По всем вопросам обращайтесь к разработчику @eugenezkh
                """;
        var formattedText = String.format(text, username);
        sendMessageToClient(sendMessage, formattedText);
    }

    private void regCommand(SendMessage sendMessage, Long chatId) {
        sendMessageToClient(sendMessage, "Для регистрации введите номер своей группы");
        userStates.put(chatId, UserState.REGISTRATION);
    }

    private void processRegistration(SendMessage sendMessage, Update update, Long chatId) {
        var groupIdStr = update.getMessage().getText();
        if (!processGroup(groupIdStr, chatId)) {
            sendMessageToClient(sendMessage, "Произошла ошибка, проверьте правильность введенной группы");
        }
        var username = update.getMessage().getChat().getUserName();
        var groupId = Integer.parseInt(groupIdStr);
        scheduleService.saveUser(new User(username, chatId, groupId));
        sendMessageToClient(sendMessage, "Ваша группа была успешно за вами закреплена");
        userStates.put(chatId, UserState.REGISTERED);
    }


    @Override
    public String getBotUsername() {
        return "BSUIR schedule";
    }
}
