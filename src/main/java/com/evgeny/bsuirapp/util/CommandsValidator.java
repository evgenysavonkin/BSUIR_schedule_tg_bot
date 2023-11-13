package com.evgeny.bsuirapp.util;

import java.util.List;


public class CommandsValidator {
    private static List<String> availableCommandsList;

    static {
        availableCommandsList = List.of("/start", "/reg", "/my_schedule", "/other_schedule",
                "/today", "/tomorrow", "/week", "/return");
    }

    private CommandsValidator() {

    }

    public static boolean isValidCommand(String command) {
        return availableCommandsList.contains(command);
    }

    public static boolean isGroupNumber(String message) {
        if (message.length() != 6) {
            return false;
        }
        try {
            Integer.parseInt(message);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
