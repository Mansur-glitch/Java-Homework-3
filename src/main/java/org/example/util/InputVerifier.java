package org.example.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputVerifier {
    public static class Error extends ErrorBase {
        public static final Error EMPTY_ID = new Error("Id can't be EMPTY!");
        public static final Error NOT_INTEGER_ID = new Error("Id is an integer number!");
        public static final Error EMPTY_NAME = new Error("Name can't be EMPTY!");
        public static final Error NAME_LENGTH_NOT_IN_RANGE = new Error("Name must contain from 3 to 32 characters");
        public static final Error NAME_INVALID_CHARACTER = new Error("Name must contain only English letters, digits and underscore");
        public static final Error EMPTY_AGE = new Error("Age can't be EMPTY!");
        public static final Error NOT_INTEGER_AGE = new Error("Age is an integer number!");
        public static final Error AGE_NOT_IN_RANGE = new Error("Age value must be inside [0, 150] range!");
        public static final Error EMPTY_EMAIL = new Error("Email can't be EMPTY!");
        public static final Error LONG_EMAIL = new Error("Email must contain a maximum of 64 characters");
        public static final Error INVALID_EMAIL_FORMAT = new Error("Invalid email format");

        protected Error(String message) {
            super(message);
        }
    }
    static private final Pattern NAME_PATTERN = Pattern.compile("\\w+");
    static private final Pattern RFC5322 = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");

    public static Expected<Integer, Error> parseId(String input) {
        if (input.isEmpty()) {
            return Expected.ofError(Error.EMPTY_ID);
        }
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return Expected.ofError(Error.NOT_INTEGER_ID);
        }
        return Expected.ofValue(id);
    }

    public static Expected<String, Error> parseUserName(String input) {
        if (input.isEmpty()) {
            return Expected.ofError(Error.EMPTY_NAME);
        }
        if (input.length() < 3 || input.length() > 32) {
            return Expected.ofError(Error.NAME_LENGTH_NOT_IN_RANGE);
        }
        if (! NAME_PATTERN.matcher(input).matches()) {
            return Expected.ofError(Error.NAME_INVALID_CHARACTER);
        }
        return Expected.ofValue(input);
    }

    public static Expected<Integer, Error> parseUserAge(String input) {
        if (input.isEmpty()) {
            return Expected.ofError(Error.EMPTY_AGE);
        }
        int age;
        try {
            age = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return Expected.ofError(Error.NOT_INTEGER_AGE);
        }
        if (age < 0 || age > 150) {
            return Expected.ofError(Error.AGE_NOT_IN_RANGE);
        }
        return Expected.ofValue(age);
    }

    public static Expected<String, Error> parseUserEmail(String input) {
        if (input.isEmpty()) {
            return Expected.ofError(Error.EMPTY_EMAIL);
        }
        if (input.length() > 64) {
            return Expected.ofError(Error.LONG_EMAIL);
        }
        if (! RFC5322.matcher(input).matches()) {
            return Expected.ofError(Error.INVALID_EMAIL_FORMAT);
        }
        return Expected.ofValue(input);
    }
}
