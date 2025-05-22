package org.example.util;

public class InputVerifier {
    public static Expected<Integer, String> parseId(String input) {
        if (input.isEmpty()) {
            return Expected.ofError("Id can't be EMPTY!");
        }
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return Expected.ofError("Id is an integer number!");
        }
        return Expected.ofValue(id);
    }

    public static Expected<String, String> parseUserName(String input) {
        if (input.isEmpty()) {
            return Expected.ofError("Name can't be EMPTY!");
        }
        return Expected.ofValue(input);
    }

    public static Expected<Integer, String> parseUserAge(String input) {
        if (input.isEmpty()) {
            return Expected.ofError("Age can't be EMPTY!");
        }
        int age;
        try {
            age = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return Expected.ofError("Age is an integer number!");
        }
        if (age < 0 || age > 150) {
            return Expected.ofError("Age value must be inside [0, 150] range!");
        }
        return Expected.ofValue(age);
    }

    public static Expected<String, String> parseUserEmail(String input) {
        if (input.isEmpty()) {
            return Expected.ofError("Email can't be EMPTY!");
        }
        return Expected.ofValue(input);
    }
}
