import org.example.data.User;
import org.example.data.UserDao;
import org.example.util.Expected;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDaoTests extends TestBase {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    static Connection getDbConnection() throws Exception
    {
        return DriverManager.getConnection(postgres.getJdbcUrl(),
                postgres.getUsername(), postgres.getPassword());
    }

    static User testUser1 = new User(1, "user1", 1, "email1");
    static User testUser2 = new User(2, "user2", 2, "email2");

    SessionFactory sessionFactory;
    SessionFactory sessionFactoryMock;
    Session sessionMock;
    Transaction transactionMock;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Override
    @BeforeEach
    void beforeEach() {
        super.beforeEach();
        try(Connection connection = getDbConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS user_account");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sessionFactory = new Configuration().configure()
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .buildSessionFactory();

        sessionFactoryMock = mock();
        sessionMock = mock();
        transactionMock = mock();
        doReturn(sessionMock).when(sessionFactoryMock).openSession();
        doReturn(transactionMock).when(sessionMock).getTransaction();
    }

    void insertTestUsers() {
        String insertQuery = """
                INSERT INTO user_account (id, name, age, email) VALUES
                (?, ?, ?, ?),
                (?, ?, ?, ?)""";

        try(Connection connection = getDbConnection();
            PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setInt(1, testUser1.getId());
            statement.setString(2, testUser1.getName());
            statement.setInt(3, testUser1.getAge());
            statement.setString(4, testUser1.getEmail());

            statement.setInt(5, testUser2.getId());
            statement.setString(6, testUser2.getName());
            statement.setInt(7, testUser2.getAge());
            statement.setString(8, testUser2.getEmail());

            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createUser_correctlyCreated() {
        Timestamp beforeCreate = new Timestamp(System.currentTimeMillis());
        User user = new User(0, "nameExample", 1, "emailExample");

        UserDao userDao = new UserDao(sessionFactory);

        assertTrue(userDao.create(user).hasValue());

        try(Connection connection = getDbConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_account")) {

            assertAll(
                    () -> assertTrue(resultSet.next()),
                    () -> assertNotEquals(0, resultSet.getInt("id")),
                    () -> assertEquals(user.getName(), resultSet.getString("name")),
                    () -> assertEquals(user.getAge(), resultSet.getInt("age")),
                    () -> assertEquals(user.getEmail(), resultSet.getString("email")),
                    () -> assertTrue(resultSet.getTimestamp("created_at").after(beforeCreate)),
                    () -> assertFalse(resultSet.next())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createUser_throws() {
        doThrow(new TestException()).when(transactionMock).commit();

        UserDao userDao = new UserDao(sessionFactoryMock);

        assertThrows(TestException.class, () -> userDao.create(new User()));

        verify(transactionMock, times(1)).rollback();
        verify(sessionMock, times(1)).close();
    }

    @Test
    void findAll_correctlyFinishes() {
        insertTestUsers();

        Expected<List<User>, UserDao.Error> expectedUsers = Expected.ofValue(List.of(testUser1, testUser2));

        UserDao userDao = new UserDao(sessionFactory);

        Expected<List<User>, UserDao.Error> actualUsers = userDao.findAll();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void findAll_throws() {
        SessionFactory sessionFactoryMock = mock();
        doThrow(new TestException()).when(sessionFactoryMock).openSession();

        UserDao userDao = new UserDao(sessionFactoryMock);
        assertThrows(TestException.class, userDao::findAll);
    }

    @Test
    void getById_correctlyFinishes() {
        insertTestUsers();

        Expected<User, UserDao.Error> expected = Expected.ofValue(testUser2);

        UserDao userDao = new UserDao(sessionFactory);

        assertEquals(expected, userDao.getById(testUser2.getId()));
    }

    @Test
    void getById_idNotFound() {
        Expected<User, UserDao.Error> expected = Expected.ofError(UserDao.Error.USER_NOT_FOUND);

        UserDao userDao = new UserDao(sessionFactory);

        assertEquals(expected, userDao.getById(1));
    }

    @Test
    void update_correctlyFinishes() {
        insertTestUsers();

        User updatedUser = new User(testUser1.getId(), "updatedName", 44, "updatedEmail");
        Timestamp beforeUpdate = new Timestamp(System.currentTimeMillis());

        UserDao userDao = new UserDao(sessionFactory);

        assertTrue(userDao.update(updatedUser).hasValue());

        try(Connection connection = getDbConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_account WHERE id = %d"
                    .formatted(updatedUser.getId()))) {

            assertAll(
                    () -> assertTrue(resultSet.next()),
                    () -> assertEquals(updatedUser.getName(), resultSet.getString("name")),
                    () -> assertEquals(updatedUser.getAge(), resultSet.getInt("age")),
                    () -> assertEquals(updatedUser.getEmail(), resultSet.getString("email")),
                    () -> assertTrue(resultSet.getTimestamp("created_at").after(beforeUpdate)),
                    () -> assertFalse(resultSet.next())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void update_idNotFound() {
        Expected<User, UserDao.Error> expected = Expected.ofError(UserDao.Error.USER_NOT_FOUND);
        User updatedUser = new User(1, "updatedName", 44, "updatedEmail");

        UserDao userDao = new UserDao(sessionFactory);

        assertEquals(expected, userDao.update(updatedUser));
    }

    @Test
    void update_throws() {
        User updatedUser = new User(-1, "updatedName", 44, "updatedEmail");

        doReturn(new User()).when(sessionMock).find(any(), any());
        doThrow(new TestException()).when(transactionMock).commit();

        UserDao userDao = new UserDao(sessionFactoryMock);

        assertThrows(TestException.class, () -> userDao.update(updatedUser));

        verify(transactionMock, times(1)).rollback();
        verify(sessionMock, times(1)).close();
    }

    @Test
    void deleteById_correctlyFinishes() {
        insertTestUsers();

        UserDao userDao = new UserDao(sessionFactory);

        assertTrue(userDao.deleteById(testUser1.getId()).hasValue());

        try(Connection connection = getDbConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_account WHERE id = %d"
                    .formatted(testUser1.getId()))) {

            assertFalse(resultSet.next());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteById_idNotFound() {
        Expected<Void, UserDao.Error> expected = Expected.ofError(UserDao.Error.USER_NOT_FOUND);

        UserDao userDao = new UserDao(sessionFactory);

        assertEquals(expected, userDao.deleteById(1));
    }

    @Test
    void deleteById_throws() {
        doReturn(new User()).when(sessionMock).find(any(), any());
        doThrow(new TestException()).when(transactionMock).commit();

        UserDao userDao = new UserDao(sessionFactoryMock);

        assertThrows(TestException.class, () -> userDao.deleteById(-1));

        verify(transactionMock, times(1)).rollback();
        verify(sessionMock, times(1)).close();
    }
}
