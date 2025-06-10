package org.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SimpleMenuTests {
    static final SimpleMenu.Entry<Boolean> entryA = new SimpleMenu.Entry<>('a', "Text a", () -> true);
    static final SimpleMenu.Entry<Boolean> entryB = new SimpleMenu.Entry<>('b', "Text b", () -> false);

    LinkedHashMap<Character, SimpleMenu.Entry<Boolean>> entriesMock;
    SimpleMenu<Boolean> menu;

    @BeforeEach
    void beforeEach() throws Exception {
        Field entriesField = SimpleMenu.class.getDeclaredField("entries");
        entriesField.setAccessible(true);

        menu = new SimpleMenu<>();
        entriesMock = mock();
        entriesField.set(menu, entriesMock);
    }

    @Test
    void addEntry_add2Entries() {
        menu.addEntry(entryA.inputKey(), entryA.text(), entryA.callback());
        menu.addEntry(entryB.inputKey(), entryB.text(), entryB.callback());

        verify(entriesMock, times(1)).put(entryA.inputKey(), entryA);
        verify(entriesMock, times(1)).put(entryB.inputKey(), entryB);
        verifyNoMoreInteractions(entriesMock);
    }

    @Test
    void process_get2CorrectEntries() {
        doReturn(entryA).when(entriesMock).get(entryA.inputKey());
        doReturn(entryB).when(entriesMock).get(entryB.inputKey());

        Expected<Boolean, ErrorBase> expectedA = Expected.ofValue(true);
        Expected<Boolean, ErrorBase> expectedB = Expected.ofValue(false);

        assertEquals(expectedA, menu.process("a"));
        assertEquals(expectedB, menu.process("b"));

        verify(entriesMock, times(1)).get('a');
        verify(entriesMock, times(1)).get('b');
        verifyNoMoreInteractions(entriesMock);
    }

    @Test
    void process_absentKey() {
        doReturn(null).when(entriesMock).get('a');
        Expected<Boolean, SimpleMenu.Error> expected = Expected.ofError(SimpleMenu.Error.ENTRY_NOT_FOUND);

        assertEquals(expected, menu.process("a"));

        verify(entriesMock, times(1)).get('a');
        verifyNoMoreInteractions(entriesMock);
    }

    @Test
    void process_stringWithSize4_wrongFormatError() {
        Expected<Boolean, SimpleMenu.Error> expected = Expected.ofError(SimpleMenu.Error.KEY_WRONG_FORMAT);
        assertEquals(expected, menu.process("word"));
    }

    @Test
    void process_stringWithSize0_wrongFormatError() {
        Expected<Boolean, SimpleMenu.Error> expected = Expected.ofError(SimpleMenu.Error.KEY_WRONG_FORMAT);
        assertEquals(expected, menu.process(""));
    }

    @Test
    void toString_2Entries() {
        Collection<SimpleMenu.Entry<Boolean>> values = List.of(entryA, entryB);
        doReturn(values).when(entriesMock).values();

        String expectedString = "a) Text a\nb) Text b\n";
        assertEquals(expectedString, menu.toString());

        verify(entriesMock, times(1)).values();
        verifyNoMoreInteractions(entriesMock);
    }
}
