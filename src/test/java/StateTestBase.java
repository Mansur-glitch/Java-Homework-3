import org.example.CliContext;
import org.example.data.UserDao;
import org.example.util.InputVerifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public class StateTestBase extends TestBase {
    MockedStatic<InputVerifier> inputVerifierMock;
    CliContext contextMock;
    UserDao userDaoMock;

    @Override
    @BeforeEach
    void beforeEach() {
        super.beforeEach();
        inputVerifierMock = mockStatic(InputVerifier.class);

        contextMock = mock();
        userDaoMock = mock();
        doReturn(userDaoMock).when(contextMock).getUserDao();
    }

    @Override
    @AfterEach
    void afterEach() {
        super.afterEach();
        inputVerifierMock.close();
    }
}
