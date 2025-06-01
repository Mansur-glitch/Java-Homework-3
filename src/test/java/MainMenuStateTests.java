import org.example.CliContext;
import org.example.data.User;
import org.example.data.UserDao;
import org.example.state.CliState;
import org.example.state.MainMenuState;
import org.example.util.CliMenu;
import org.example.util.ErrorBase;
import org.example.util.Expected;
import org.example.util.MainMenuEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.function.Supplier;

import static org.example.data.UserDao.Error.USER_NOT_FOUND;
import static org.example.util.MainMenuEntry.EXIT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainMenuStateTests extends StateTestBase{
    CliMenu<Character, CliState.ProcessResult> menuMock;
    MainMenuState state;

    @BeforeEach
    void reset() {
        menuMock = mock();
        state = new MainMenuState(contextMock, menuMock);
    }

    @Test
    void testConstructorMenuOrder() {
        InOrder menuOrder = inOrder(menuMock);
        for (MainMenuEntry entry: MainMenuEntry.values()) {
            menuOrder.verify(menuMock).addEntry(eq(entry.getKey()), eq(entry.getText()), any());
        }
        menuOrder.verifyNoMoreInteractions();
    }

    @Test
    void testConstructorExitEntry() {
        CliMenu<Character, CliState.ProcessResult> differentMenuMock = mock();
        CliContext differentContextMock = mock();

        doAnswer(invocation -> {
            Supplier<CliState.ProcessResult> callback =  invocation.getArgument(2);
            CliState.ProcessResult result = callback.get();
            assertAll(
                    () -> assertNull(result.nextState()),
                    () -> assertTrue(result.textResponse().contains("Exiting"))
            );
            verify(differentContextMock, times(1)).setExitFlag(true);
            return null;
        })
                .when(differentMenuMock).addEntry(eq(EXIT.getKey()), any(), any());

        MainMenuState differentState = new MainMenuState(differentContextMock, differentMenuMock);
        verify(menuMock, times(1)).addEntry(eq(EXIT.getKey()), any(), any());
    }

    @Test
    void process_menuReturnsValue() {
        Expected<CliState.ProcessResult, String> menuOutput = Expected.ofValue(null);
        doReturn(menuOutput).when(menuMock).process("test");

        assertNull(state.process("test"));
    }

    @Test
    void process_menuReturnsError() {
        Expected<CliState.ProcessResult, ErrorBase> menuOutput = Expected.ofError(ErrorBase.UNEXPECTED_ERROR);
        doReturn(menuOutput).when(menuMock).process("test");

        CliState.ProcessResult actualResult = state.process("test");

        assertAll(
                () -> assertNull(actualResult.nextState()),
                () -> assertTrue(actualResult.textResponse().contains(ErrorBase.UNEXPECTED_ERROR.toString()))
        );
    }

    @Test
    void printAllUsers_finishesCorrectly() {
        User user1 = new User();
        user1.setId(1);
        User user2 = new User();
        user2.setId(2);

        List<User> userList = List.of(user1, user2);

        Expected<List<User>, String> expectedUsers = Expected.ofValue(userList);
        doReturn(expectedUsers).when(userDaoMock).findAll();

        CliState.ProcessResult result = state.printAllUsers();

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(user1.toString())),
                () -> assertTrue(result.textResponse().contains(user2.toString()))
        );
    }

    @Test
    void printAllUsers_DaoReturnsError() {
        Expected<List<User>, UserDao.Error> expectedUsers = Expected.ofError(USER_NOT_FOUND);
        doReturn(expectedUsers).when(userDaoMock).findAll();
        doReturn("").when(menuMock).toString();

        CliState.ProcessResult result = state.printAllUsers();

        assertAll(
                () -> assertNull(result.nextState()),
                () -> assertTrue(result.textResponse().contains(USER_NOT_FOUND.toString()))
        );
    }
}