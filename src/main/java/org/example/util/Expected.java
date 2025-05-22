package org.example.util;

public record Expected<T, E>(boolean hasValue, T value, E error) {
    public static <T, E> Expected<T, E> ofValue(T value) {
        return new Expected<>(true, value, null);
    }

    public static <T, E> Expected<T, E> ofError(E error) {
        return new Expected<>(false, null, error);
    }
}
