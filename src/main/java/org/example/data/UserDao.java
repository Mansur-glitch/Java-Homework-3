package org.example.data;

import org.example.util.Expected;
import org.example.util.LogUtility;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class UserDao {
    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Expected<Integer, String> create(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.persist(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            LogUtility.logger().severe("Failed to create new user with %d id".formatted(user.getId()));
            session.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
        LogUtility.logger().info("User with %d id was created".formatted(user.getId()));
        return Expected.ofValue(user.getId());
    }

    public Expected<List<User>, String> findAll() {
        Session session = sessionFactory.openSession();
        List<User> users;
        users = session.createSelectionQuery("From User", User.class)
                .getResultList();
        session.close();
        LogUtility.logger().info("%d entries were retrieved".formatted(users.size()));
        return Expected.ofValue(users);
    }

    public Expected<User, String> getById(int id) {
        Session session = sessionFactory.openSession();
        User user = session.find(User.class, id);
        session.close();
        if (user == null) {
            LogUtility.logger().info("User with %d id wasn't found".formatted(id));
            return Expected.ofError("User with specified id wasn't found!");
        }
        LogUtility.logger().info("User with %d id was retrieved".formatted(id));
        return Expected.ofValue(user);
    }

    public Expected<User, String> update(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            User dbUser = session.find(User.class, user.getId());
            if (dbUser == null) {
                return Expected.ofError("User with specified id wasn't found!");
            }
            if (user.getName() != null) {
                dbUser.setName(user.getName());
            }
            if (user.getAge() != -1) {
                dbUser.setAge(user.getAge());
            }
            if (user.getEmail() != null) {
                dbUser.setEmail(user.getEmail());
            }
            session.getTransaction().commit();
            LogUtility.logger().info("User with %d id was updated".formatted(user.getId()));
            return Expected.ofValue(dbUser);
        } catch (Exception e) {
            LogUtility.logger().severe("Failed to update user with %d id".formatted(user.getId()));
            session.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    public Expected<Void, String> deleteById(int id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            User user = session.find(User.class, id);
            if (user == null) {
                session.getTransaction().rollback();
                return Expected.ofError("User with specified id wasn't found!");
            }
            session.remove(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            LogUtility.logger().severe("Failed to delete user with %d id".formatted(id));
            session.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
        LogUtility.logger().info("User with %d id was deleted".formatted(id));
        return Expected.ofValue(null);
    }
}
