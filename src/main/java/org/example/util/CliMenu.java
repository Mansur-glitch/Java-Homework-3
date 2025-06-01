package org.example.util;

import java.util.function.Supplier;

public interface CliMenu<K, R> {
    void addEntry(K inputKey, String text, Supplier<R> callback);

    Expected<R, ErrorBase> process(String input);

    String toString();
}
