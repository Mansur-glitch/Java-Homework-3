package org.example.util;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class SimpleMenu<R> implements CliMenu<Character, R> {
    public static class Error extends ErrorBase {
        public static final Error ENTRY_NOT_FOUND = new Error("Specified menu entry wasn't found!");
        public static final Error KEY_WRONG_FORMAT = new Error("Menu entries are specified with a single character!");

        protected Error(String message) {
            super(message);
        }
    }

    private final LinkedHashMap<Character, Entry<R>> entries = new LinkedHashMap<>();

    @Override
    public void addEntry(Character inputKey, String text, Supplier<R> callback) {
        entries.put(inputKey, new Entry<>(inputKey, text, callback));

    }

    @Override
    public Expected<R, ErrorBase> process(String input) {
        int inputLength = input.length();
        if (inputLength != 1) {
            return Expected.ofError(Error.KEY_WRONG_FORMAT);
        }
        char key = input.charAt(0);
        Entry<R> entry = entries.get(key);
        if (entry == null) {
            return Expected.ofError(Error.ENTRY_NOT_FOUND);
        }

        return Expected.ofValue(entry.callback().get());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<R> entry : entries.values()) {
            builder.append(entry.inputKey)
                    .append(") ")
                    .append(entry.text)
                    .append('\n');
        }
        return builder.toString();
    }

    public record Entry<R>(char inputKey, String text, Supplier<R> callback) {

    }
}
