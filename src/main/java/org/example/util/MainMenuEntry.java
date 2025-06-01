package org.example.util;

public enum MainMenuEntry {
    CREATE('1', "Create new user"),
    PRINT_ALL('2', "Print all users"),
    GET_BY_ID('3', "Get user by id"),
    UPDATE('4', "Update user"),
    DELETE('5', "Delete user"),
    EXIT('0', "Exit");

    private final char key;
    private final String text;

    MainMenuEntry(char key, String text) {
        this.key = key;
        this.text = text;
    }
    public char getKey() {
        return key;
    }
    public String getText() {
        return text;
    }
}
