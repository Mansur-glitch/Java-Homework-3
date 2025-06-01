import org.example.util.Expected;
import org.example.util.InputVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.example.util.InputVerifier.Error.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputVerifierTests {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void parseId_correctId(int id) {
        Expected<Integer, InputVerifier.Error> expected = Expected.ofValue(id);
        assertEquals(expected, InputVerifier.parseId(String.valueOf(id)));
    }

    @Test
    void parseId_emptyInput() {
        Expected<Integer, InputVerifier.Error> expected = Expected.ofError(EMPTY_ID);
        assertEquals(expected, InputVerifier.parseId(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcde", "123.123", "123'000", "123,123", " 123 ", "123 a"})
    void parseId_notInteger(String input) {
        Expected<Integer, InputVerifier.Error> expected = Expected.ofError(NOT_INTEGER_ID);
        assertEquals(expected, InputVerifier.parseId(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC", "abc", "_A_B_C_", "123", "1_2_3AbC", "size_32_string__________________"})
    void parseUserName_correctName(String name) {
        Expected<String, InputVerifier.Error> expected = Expected.ofValue(name);
        assertEquals(expected, InputVerifier.parseUserName(name));
    }

    @Test
    void parseUserName_emptyInput() {
        Expected<String, InputVerifier.Error> expected = Expected.ofError(EMPTY_NAME);
        assertEquals(expected, InputVerifier.parseUserName(""));
    }

    @Test
    void parseUserName_shortName() {
        Expected<String, InputVerifier.Error> expected = Expected.ofError(NAME_LENGTH_NOT_IN_RANGE);
        assertEquals(expected, InputVerifier.parseUserName("ab"));
    }

    @Test
    void parseUserName_longName() {
        Expected<String, InputVerifier.Error> expected = Expected.ofError(NAME_LENGTH_NOT_IN_RANGE);
        String longName = new String(new char[33]).replace('\0', 'a');
        assertEquals(expected, InputVerifier.parseUserName(longName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc!", "!abc", "!@#$%^&", "123@", "    "})
    void parseUserName_invalidCharacters(String name) {
        Expected<String, InputVerifier.Error> expected = Expected.ofError(NAME_INVALID_CHARACTER);
        assertEquals(expected, InputVerifier.parseUserName(name));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 33, 150})
    void parseUserAge_correctAge(int age) {
        Expected<Integer, InputVerifier.Error> expected = Expected.ofValue(age);
        assertEquals(expected, InputVerifier.parseUserAge(String.valueOf(age)));
    }

    @Test
    void parseUserAge_emptyInput() {
        Expected<Integer, InputVerifier.Error> expected = Expected.ofError(EMPTY_AGE);
        assertEquals(expected, InputVerifier.parseUserAge(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcde", "123.123", "123'000", "123,123", " 123 ", "123 a"})
    void parseUserAge_notInteger(String input) {
        Expected<Integer, InputVerifier.Error> expected = Expected.ofError(NOT_INTEGER_AGE);
        assertEquals(expected, InputVerifier.parseUserAge(input));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 151})
    void parseUserAge_notInRange(int age) {
        Expected<Integer, InputVerifier.Error> expected = Expected.ofError(AGE_NOT_IN_RANGE);
        assertEquals(expected, InputVerifier.parseUserAge(String.valueOf(age)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"simple@example.com", "very.common@example.com",
            "disposable.style.email.with+symbol@example.com", "other.email-with-hyphen@example.com",
            "fully-qualified-domain@example.com", "user.name+tag+sorting@example.com", "x@example.com",
            "example-indeed@strange-example.com", "example@s.example", "\"john..doe\"@example.org"})
    void parseUserEmail_correctEmail(String email) {
        Expected<String, InputVerifier.Error> expected = Expected.ofValue(email);
        assertEquals(expected, InputVerifier.parseUserEmail(email));
    }

    @Test
    void parseUserEmail_emptyInput() {
        Expected<String, InputVerifier.Error> expected = Expected.ofError(EMPTY_EMAIL);
        assertEquals(expected, InputVerifier.parseUserEmail(""));
    }

    @Test
    void parseUserEmail_longEmail() {
        Expected<String, InputVerifier.Error> expected = Expected.ofError(LONG_EMAIL);
        String longEmail = new String(new char[56]).replace('\0', 'a') + "@mail.com";
        assertEquals(expected, InputVerifier.parseUserEmail(longEmail));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "Abc.example.com", "A@b@c@example.com", "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com",
            "just\"not\"right@example.com", "this is\"not\\allowed@example.com",
            "this\\ still\\\"not\\\\allowed@example.com"})
    void parseUserEmail_invalidEmail(String email) {
        Expected<String, InputVerifier.Error> expected = Expected.ofError(INVALID_EMAIL_FORMAT);
        assertEquals(expected, InputVerifier.parseUserEmail(email));
    }
}
