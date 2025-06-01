import org.example.data.User;
import org.example.data.UserDao;
import org.example.state.CliState;
import org.example.state.CreationState;
import org.example.state.MainMenuState;
import org.example.state.UpdatingState;
import org.example.util.Expected;
import org.example.util.InputVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.example.data.UserDao.Error.USER_NOT_FOUND;
import static org.example.util.InputVerifier.Error.NOT_INTEGER_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpdatingStateTests extends StateTestBase {
    UpdatingState state;
    Field blankField;

    @Override
    @BeforeEach
    void beforeEach() {
        super.beforeEach();
        state = new UpdatingState(contextMock);
        try {
            blankField = CreationState.class.getDeclaredField("blank");
            blankField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void enterId_correctInput() {
        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofValue(1);
        Expected<User, UserDao.Error> daoResult = Expected.ofValue(new User());

        inputVerifierMock.when(() -> InputVerifier.parseId(any())).thenReturn(parseResult);
        doReturn(daoResult).when(userDaoMock).getById(anyInt());

        CliState.ProcessResult result = state.enterId("");

        assertAll(
                () -> assertNotNull(result.nextState()),
                () -> assertEquals(1, ((User) blankField.get(state)).getId())
        );
    }

    @Test
    void enterId_returnToMenu() {
        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofValue(0);
        inputVerifierMock.when(() -> InputVerifier.parseId(any())).thenReturn(parseResult);

        CliState.ProcessResult result = state.enterId("");

        assertEquals(MainMenuState.class, result.nextState().getClass());
        verifyNoInteractions(userDaoMock);
    }

    @Test
    void enterId_invalidInput() {
        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofError(NOT_INTEGER_ID);
        inputVerifierMock.when(() -> InputVerifier.parseId(any())).thenReturn(parseResult);

        CliState.ProcessResult result = state.enterId("");

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(NOT_INTEGER_ID.toString()))
        );
        verifyNoInteractions(userDaoMock);
    }

    @Test
    void enterId_daoFails() {
        Expected<Integer, InputVerifier.Error> parseResult = Expected.ofValue(1);
        Expected<User, UserDao.Error> daoResult = Expected.ofError(USER_NOT_FOUND);

        inputVerifierMock.when(() -> InputVerifier.parseId(any())).thenReturn(parseResult);
        doReturn(daoResult).when(userDaoMock).getById(anyInt());

        CliState.ProcessResult result = state.enterId("");

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(USER_NOT_FOUND.toString()))
        );
    }

    @Test
    void databaseAction_returnsValue() {
        Expected<Integer, UserDao.Error> daoResult = Expected.ofValue(1);
        doReturn(daoResult).when(userDaoMock).update(any());

        Expected<String, String> result = state.databaseAction(new User());

        assertTrue(result.hasValue());
    }

    @Test
    void databaseAction_returnsError() {
        Expected<Integer, UserDao.Error> daoResult = Expected.ofError(USER_NOT_FOUND);
        doReturn(daoResult).when(userDaoMock).update(any());

        Expected<String, String> result = state.databaseAction(new User());

        assertFalse(result.hasValue());
    }
}
