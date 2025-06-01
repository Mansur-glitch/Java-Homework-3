import org.example.util.LogUtility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.logging.Logger;

import static org.mockito.Mockito.mockStatic;

public class TestBase {
    static final String LOG_DIRECTORY = "test-logs";
    static {
        File logDirectoryFile = new File(LOG_DIRECTORY);
        if (! logDirectoryFile.exists()) {
            if(! logDirectoryFile.mkdir()) {
                throw new RuntimeException("Failed to create folder for test logs");
            }
        }
    }
    Logger logger = LogUtility.createLogger(LOG_DIRECTORY + '/' + getClass().getName() + ".txt");
    MockedStatic<LogUtility> logUtilityMock;

    @BeforeEach
    void beforeEach() {
        logUtilityMock = mockStatic(LogUtility.class);
        logUtilityMock.when(LogUtility::getStaticLogger).thenReturn(this.logger);
    }

    @AfterEach
    void afterEach() {
        logUtilityMock.close();
    }
}
