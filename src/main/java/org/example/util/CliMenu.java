package org.example.util;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class CliMenu<R> {
    private final LinkedHashMap<Character, Entry<R>> entries = new LinkedHashMap<>();

    public void addEntry(char inputKey, String text, Supplier<R> callback) {
        entries.put(inputKey, new Entry<>(inputKey, text, callback));
    }

    public String buildMenuString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<R> entry : entries.values()) {
            builder.append(entry.inputKey)
                    .append(") ")
                    .append(entry.text)
                    .append('\n');
        }
        return builder.toString();
    }

    public Expected<R, String> process(String input) {
        int inputLength = input.length();
        if (inputLength != 1) {
            return Expected.ofError("Menu entries are specified with a single character!");
        }
        char key = input.charAt(0);
        Entry<R> entry = entries.get(key);
        if (entry == null) {
            return Expected.ofError("Specified menu entry wasn't found!");
        }

        return Expected.ofValue(entry.callback.get());
    }

    public record Entry<R>(char inputKey, String text, Supplier<R> callback) {

    }
}
