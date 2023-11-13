package com.evgeny.bsuirapp.bot;

import com.evgeny.bsuirapp.enums.ScheduleState;
import com.evgeny.bsuirapp.enums.UserState;
import com.evgeny.bsuirapp.models.User;
import com.evgeny.bsuirapp.service.ScheduleService;
import com.evgeny.bsuirapp.service.UserService;
import com.evgeny.bsuirapp.util.CommandsValidator;
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

    private static final String START = "/start";
    private static final String REG = "/reg";
    private static final String MY_SCHEDULE = "/my_schedule";
    private static final String OTHER_SCHEDULE = "/other_schedule";
    private static final String TODAY_SCHEDULE = "/today";
    private static final String TOMORROW_SCHEDULE = "/tomorrow";
    private static final String WEEK_SCHEDULE = "/week";
    private static final String RETURN_COMMAND = "/return";
    private final UserService userService;
    private final ScheduleService scheduleService;

    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, ScheduleState> userScheduleStates = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(BSUIRScheduleBot.class);
    private static boolean isRegistered;

    @Autowired
    public BSUIRScheduleBot(@Value("${bot.token}") String botToken,
                            UserService userService, ScheduleService scheduleService) {
        super(botToken);
        this.userService = userService;
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
        switch (message){

        };
    }

    private void handleRegCommand(SendMessage sendMessage, Update update, long chatId) {
        //processRegistration();
        boolean isRegistered = userService.isUserExists(chatId);
        if (isRegistered) {
            startCommandIfRegistered(sendMessage);
            return;
        }
        processRegistration(sendMessage, update, chatId);
    }

    private void handleStartCommand(SendMessage sendMessage, long chatId) {
        //Проверить существует ли пользователь
        //Если есть по этому chatId, то вернуть, что он уже зареган
        //Иначе вывести ему полное приветствие
        boolean isExists = userService.isUserExists(chatId);
        System.out.println("Result if exists " + isExists);
        if (isExists) {
            startCommandIfRegistered(sendMessage);
            return;
        }
        startCommand(sendMessage);
    }

    private void todayScheduleForUserGroup(SendMessage sendMessage, Long chatId) {
        String userGroupStr = userService.getUserGroupByChatId(chatId);
        if (userGroupStr.equals("Не найдено")) {
            sendMessageToClient(sendMessage, "Произошла ошибка, вашей группы не найдено");
            LOG.error("Unable to detect groupId for chatId = " + chatId);
        }
        int userGroup = Integer.parseInt(userGroupStr);
        String response = scheduleService.getFormattedScheduleByUserGroup(userGroup, "/today");
        sendMessageToClient(sendMessage, response);
    }

    private void tomorrowScheduleForUserGroup(SendMessage sendMessage, Long chatId) {
        var userGroupStr = userService.getUserGroupByChatId(chatId);
        if (userGroupStr.equals("Не найдено")) {
            sendMessageToClient(sendMessage, "Произошла ошибка, вашей группы не найдено");
            LOG.error("Unable to detect groupId for chatId = " + chatId);
            return;
        }
        int userGroup = Integer.parseInt(userGroupStr);
        String response = scheduleService.getFormattedScheduleByUserGroup(userGroup, "/tomorrow");
        sendMessageToClient(sendMessage, response);
    }

    private void userWithGroupViewScheduleMessage(SendMessage sendMessage, Long chatId) {
        var text = """
                Номер вашей группы %s
                Доступные команды:

                /today
                /tomorrow
                /week
                /return - для просмотра другого расписания

                """;
        var groupId = userService.getUserGroupByChatId(chatId);
        var formattedText = String.format(text, groupId);
        sendMessageToClient(sendMessage, formattedText);
        LOG.debug(groupId);
    }

    private void otherGroupsViewScheduleMessage(SendMessage sendMessage, Long chatId) {
        var text = """
                Доступные команды:

                /today
                /tomorrow
                /week
                /return - для просмотра другого расписания

                """;
        sendMessageToClient(sendMessage, text);
    }

    private void sendMessageToClient(SendMessage sendMessage, String text) {
        if (sendMessage == null) {
            return;
        }
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("sendMessageToClient: " + e);
            throw new RuntimeException(e);
        }
    }

    private void startCommand(SendMessage sendMessage) {
        var text = """
                Добро пожаловать!

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
        sendMessageToClient(sendMessage, text);
    }

    private void instructionsMessage(SendMessage sendMessage) {
        var text = """
                Доступные команды:
                                
                /reg
                /my_schedule
                /other_schedule
                                
                Приятного пользования!
                По всем вопросам обращайтесь к разработчику @eugenezkh
                """;
        sendMessageToClient(sendMessage, text);
    }

    private void startCommandIfRegistered(SendMessage sendMessage) {
        var text = """
                Вы уже были зарегистрированы!
                                       
                Доступные команды:
                                
                /reg
                /my_schedule
                /other_schedule
                                        
                Приятного пользования!
                По всем вопросам обращайтесь к разработчику @eugenezkh
                """;
        sendMessageToClient(sendMessage, text);
    }

    private void processRegistration(SendMessage sendMessage, Update update, Long chatId) {
        sendMessageToClient(sendMessage, "Введите, пожалуйста, номер группы");
        var groupIdStr = update.getMessage().getText();
        if (!CommandsValidator.isGroupNumber(groupIdStr)) {
            sendMessageToClient(sendMessage, "Произошла ошибка, проверьте правильность введенной группы");
        }
        var groupId = Integer.parseInt(groupIdStr);
        userService.saveUser(new User(chatId, groupId));
        sendMessageToClient(sendMessage, "Ваша группа была успешно за вами закреплена");
        instructionsMessage(sendMessage);
    }


    @Override
    public String getBotUsername() {
        return "BSUIR schedule";
    }
}
