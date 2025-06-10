package org.example.state;

import org.example.data.User;
import org.example.data.UserDao;
import org.example.util.Expected;
import org.example.util.InputVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.example.data.UserDao.Error.USER_NOT_FOUND;
import static org.example.util.InputVerifier.Error.NOT_INTEGER_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;

public class SearchingStateTests extends StateTestBase {
    SearchingState state;

    @Override
    @BeforeEach
    protected void beforeEach() {
        super.beforeEach();
        state = new SearchingState(contextMock);
    }


    @Test
    void process_correctInput() {
        User testUser = new User(1, "testName", 1, "testEmail");

        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofValue(1);
        Expected<User, UserDao.Error> daoResult = Expected.ofValue(testUser);

        inputVerifierMock.when(() -> InputVerifier.parseId(any())).thenReturn(parseResult);
        doReturn(daoResult).when(userDaoMock).getById(anyInt());

        CliState.ProcessResult result = state.process("");

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(testUser.toString()))
        );
    }

    @Test
    void process_returnToMenu() {
        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofValue(0);
        inputVerifierMock.when(() -> InputVerifier.parseId(any())).thenReturn(parseResult);

        CliState.ProcessResult result = state.process("");

        assertEquals(MainMenuState.class, result.nextState().getClass());
        verifyNoInteractions(userDaoMock);
    }

    @Test
    void process_invalidInput() {
        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofError(NOT_INTEGER_ID);
        inputVerifierMock.when(() -> InputVerifier.parseId(any())).thenReturn(parseResult);

        CliState.ProcessResult result = state.process("");

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(NOT_INTEGER_ID.toString()))
        );
        verifyNoInteractions(userDaoMock);
    }

    @Test
    void process_daoFails() {
        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofValue(1);
        Expected<User, UserDao.Error> daoResult = Expected.ofError(USER_NOT_FOUND);

        inputVerifierMock.when(() -> InputVerifier.parseId(any())).thenReturn(parseResult);
        doReturn(daoResult).when(userDaoMock).getById(anyInt());

        CliState.ProcessResult result = state.process("");

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(USER_NOT_FOUND.toString()))
        );
    }
}
