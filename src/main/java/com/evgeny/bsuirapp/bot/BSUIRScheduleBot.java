package com.evgeny.bsuirapp.bot;

import com.evgeny.bsuirapp.enums.ScheduleState;
import com.evgeny.bsuirapp.enums.UserState;
import com.evgeny.bsuirapp.models.User;
import com.evgeny.bsuirapp.service.ScheduleService;
import com.evgeny.bsuirapp.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class BSUIRScheduleBot extends TelegramLongPollingBot {

    private static final String START = "/start";
    private static final String REG = "/reg";
    private static final String MY_SCHEDULE = "/my_schedule";
    private static final String OTHER_SCHEDULE = "/other_schedule";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

    private final UserService userService;
    private final ScheduleService scheduleService;

    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, ScheduleState> userScheduleStates = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(BSUIRScheduleBot.class);

    @Autowired
    public BSUIRScheduleBot(@Value("${bot.token}") String botToken, UserService userService, ScheduleService scheduleService) {
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
        UserState userState = userStates.getOrDefault(chatId, UserState.IDLE);
        switch (message) {
            case START -> {
                LOG.debug("current Userstate is " + userState);
                var username = update.getMessage().getChat().getUserName();
                System.out.println("userName is " + username);
                if (!userService.isUserExists(chatId)) {
                    startCommand(response, username);
                } else {
                    startCommand(username, response);
                }
            }
            case REG -> {
                LOG.debug("current Userstate is " + userState);
                if (userService.isUserExists(chatId)) {
                    sendMessageToClient(response, "Вы уже зарегистрированы");
                    instructionsMessage(response);
                } else if (userState == UserState.IDLE) {
                    userStates.put(chatId, UserState.REGISTRATION);
                    sendMessageToClient(response, "Для регистрации введите номер своей группы");
                }
            }
            case MY_SCHEDULE -> {
                LOG.debug("current Userstate is " + userState);
                sendMessageToClient(response, "Вы выбрали опцию /my_schedule");
                userStates.put(chatId, UserState.CHECK_MY_SCHEDULE);
                userScheduleStates.put(chatId, ScheduleState.MY);
                myScheduleMessage(response, chatId);
            }

            case OTHER_SCHEDULE -> {
                sendMessageToClient(response, "Вы выбрали опцию /other_schedule");
                userStates.put(chatId, UserState.CHECK_OTHER_SCHEDULE);
                userScheduleStates.put(chatId, ScheduleState.OTHER);
                otherScheduleMessage(response, chatId);
            }
            default -> {
                if (update.getMessage().getText().equals("/return")) {
                    instructionsMessage(response);
                    break;
                } else if (!isValidCommand(update.getMessage().getText())) {
                    sendMessageToClient(response, "Команда не распознана");
                    instructionsMessage(response);
                    break;
                }
                switch (userState) {
                    case REGISTRATION -> {
                        LOG.debug("current Userstate is " + userState);
                        processRegistration(response, update, chatId);

                    }
                    case CHECK_MY_SCHEDULE -> {
                        String command = update.getMessage().getText();
                        switch (command){
                            case "/today" ->{
                                LOG.debug(userState);
                                todayScheduleForUserGroup(response ,chatId);
                            }
                            case "/tomorrow" ->{
                                LOG.debug(userState);
                                tomorrowScheduleForUserGroup(response, chatId);
                            }
                            case "/week" ->{

                            }
                        }

                    }
                }
            }
        }
    }

    private void todayScheduleForUserGroup(SendMessage sendMessage ,Long chatId){
        var userGroup = userService.getUserGroupByChatId(chatId);
        if (userGroup.equals("Не найдено")){
            sendMessageToClient(sendMessage, "Произошла ошибка, вашей группы не найдено");
            LOG.error("Unable to detect groupId for chatId = " + chatId);
        }
        var currDate = LocalDate.now();
        var formattedDate = currDate.format(formatter);
    }

    private void tomorrowScheduleForUserGroup(SendMessage sendMessage ,Long chatId){
        var userGroup = userService.getUserGroupByChatId(chatId);
        if (userGroup.equals("Не найдено")){
            sendMessageToClient(sendMessage, "Произошла ошибка, вашей группы не найдено");
            LOG.error("Unable to detect groupId for chatId = " + chatId);
        }
        var currDate = LocalDate.now().plusDays(1);
        var formattedDate = currDate.format(formatter);
    }

    private static boolean isValidCommand(String command){
        boolean isGroup;
        isGroup = processGroup(command);
        return (command.equals("/today") || command.equals("/tomorrow") || command.equals("/week") || isGroup);
    }

    // /my_schedule
    private void processMySchedule(SendMessage sendMessage, Update update, Long chatId) {
        sendMessageToClient(sendMessage, "Метод для вытаскивания расписания");
//        var text = """
//                Номер вашей группы %s
//                Доступные команды:
//
//                /today
//                /tomorrow
//                /week
//
//                """;
//        var groupId = scheduleService.getUserByChatId(chatId);
//        var formattedText = String.format(text, groupId);
//        sendMessageToClient(sendMessage, formattedText);
//        LOG.debug(groupId);
//        //TODO: доделать просмотр расписания по группе клиента
    }

    private void myScheduleMessage(SendMessage sendMessage, Long chatId) {
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
    private void otherScheduleMessage(SendMessage sendMessage, Long chatId) {
        var text = """
                Доступные команды:

                /today
                /tomorrow
                /week
                /return - для просмотра другого расписания

                """;
        sendMessageToClient(sendMessage, text);
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
    private static boolean processGroup(String message) {
        if (message.length() != 6) {
            return false;
        }
        try {
            Integer.parseInt(message);
            return true;
        } catch (NumberFormatException e) {
            LOG.error("Number format exception");
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
            LOG.error("sendMessageToClient: " + e);
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

    private void processRegistration(SendMessage sendMessage, Update update, Long chatId) {
        var groupIdStr = update.getMessage().getText();
        if (!processGroup(groupIdStr, chatId)) {
            sendMessageToClient(sendMessage, "Произошла ошибка, проверьте правильность введенной группы");
        }
        var username = update.getMessage().getChat().getUserName();
        var groupId = Integer.parseInt(groupIdStr);
        userService.saveUser(new User(username, chatId, groupId));
        sendMessageToClient(sendMessage, "Ваша группа была успешно за вами закреплена");
        userStates.put(chatId, UserState.REGISTERED);
        instructionsMessage(sendMessage);
    }


    @Override
    public String getBotUsername() {
        return "BSUIR schedule";
    }
}
