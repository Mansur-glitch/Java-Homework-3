package org.example.util;

public class ErrorBase {
    public static final ErrorBase UNEXPECTED_ERROR = new ErrorBase("Unexpected error");

    private final String message;

    protected ErrorBase(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}

