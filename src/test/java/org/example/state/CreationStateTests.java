package org.example.state;

import org.example.data.User;
import org.example.data.UserDao;
import org.example.util.Expected;
import org.example.util.InputVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.example.data.UserDao.Error.USER_NOT_FOUND;
import static org.example.util.InputVerifier.Error.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreationStateTests extends StateTestBase { ;
    CreationState state;
    Field blankField;

    @Override
    @BeforeEach
    protected void beforeEach() {
        super.beforeEach();
        state = new CreationState(contextMock);
        try {
            blankField = CreationState.class.getDeclaredField("blank");
            blankField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void enterName_correctInput() {
        Expected<String, InputVerifier.Error> parseResult = Expected.ofValue("t");
        inputVerifierMock.when(() -> InputVerifier.parseUserName(any())).thenReturn(parseResult);

        CliState.ProcessResult result = state.enterName("");

        assertAll(
                () -> assertNotNull(result.nextState()),
                () -> assertEquals("t", ((User) blankField.get(state)).getName())
        );
    }

    @Test
    void enterName_invalidInput() {
        Expected<String, InputVerifier.Error> parseResult = Expected.ofError(NAME_INVALID_CHARACTER);
        inputVerifierMock.when(() -> InputVerifier.parseUserName(any())).thenReturn(parseResult);

        CliState.ProcessResult result = state.enterName("");

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(NAME_INVALID_CHARACTER.toString()))
        );
    }

    @Test
    void enterAge_correctInput() {
        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofValue(1);
        inputVerifierMock.when(() -> InputVerifier.parseUserAge(any())).thenReturn(parseResult);

        CliState.ProcessResult result = state.enterAge("");

        assertAll(
                () -> assertNotNull(result.nextState()),
                () -> assertEquals(1, ((User) blankField.get(state)).getAge())
        );
    }

    @Test
    void enterAge_invalidInput() {
        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofError(AGE_NOT_IN_RANGE);
        inputVerifierMock.when(() -> InputVerifier.parseUserAge(any())).thenReturn(parseResult);

        CliState.ProcessResult result = state.enterAge("");

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(AGE_NOT_IN_RANGE.toString()))
        );
    }

    @Test
    void databaseAction_returnsValue() {
        Expected<Integer, UserDao.Error> daoResult = Expected.ofValue(1);
        doReturn(daoResult).when(userDaoMock).create(any());

        Expected<String, String> result = state.databaseAction(new User());

        assertTrue(result.hasValue());
    }

    @Test
    void databaseAction_returnsError() {
        Expected<Integer, UserDao.Error> daoResult = Expected.ofError(USER_NOT_FOUND);
        doReturn(daoResult).when(userDaoMock).create(any());

        Expected<String, String> result = state.databaseAction(new User());

        assertFalse(result.hasValue());
    }

    @Test
    void enterEmail_correctInput() {
        Expected<String, InputVerifier.Error> parseResult = Expected.ofValue("t");
        inputVerifierMock.when(() -> InputVerifier.parseUserEmail(any())).thenReturn(parseResult);

        CreationState stateSpy = spy(state);
        doReturn(Expected.ofValue("")).when(stateSpy).databaseAction(any());

        CliState.ProcessResult result = stateSpy.enterEmail("");

        assertAll(
                () -> assertEquals(MainMenuState.class, result.nextState().getClass()),
                () -> assertEquals("t", ((User) blankField.get(state)).getEmail())
        );
    }

    @Test
    void enterEmail_invalidInput() {
        Expected<String, InputVerifier.Error> parseResult = Expected.ofError(INVALID_EMAIL_FORMAT);
        inputVerifierMock.when(() -> InputVerifier.parseUserEmail(any())).thenReturn(parseResult);

        CreationState stateSpy = spy(state);

        CliState.ProcessResult result = stateSpy.enterEmail("");

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(INVALID_EMAIL_FORMAT.toString()))
        );
        verifyNoInteractions(userDaoMock);
    }

    @Test
    void enterEmail_daoFails() {
        Expected<String, InputVerifier.Error> parseResult = Expected.ofValue("");
        inputVerifierMock.when(() -> InputVerifier.parseUserEmail(any())).thenReturn(parseResult);

        CreationState stateSpy = spy(state);
        doReturn(Expected.ofError("")).when(stateSpy).databaseAction(any());

        CliState.ProcessResult result = stateSpy.enterEmail("");

        assertEquals(MainMenuState.class, result.nextState().getClass());
    }
}