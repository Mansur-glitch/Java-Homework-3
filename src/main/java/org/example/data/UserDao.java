package org.example.data;

import org.example.util.ErrorBase;
import org.example.util.Expected;
import org.example.util.LogUtility;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class UserDao {
    public static class Error extends ErrorBase {
        public static final Error USER_NOT_FOUND = new Error("User with specified id wasn't found!");

        protected Error(String message) {
            super(message);
        }
    }
    
    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Expected<Integer, Error> create(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.persist(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            LogUtility.getStaticLogger().severe("Failed to create new user with %d id".formatted(user.getId()));
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        LogUtility.getStaticLogger().info("User with %d id was created".formatted(user.getId()));
        return Expected.ofValue(user.getId());
    }

    public Expected<List<User>, Error> findAll() {
        Session session = sessionFactory.openSession();
        List<User> users;
        users = session.createSelectionQuery("From User", User.class)
                .getResultList();
        session.close();
        LogUtility.getStaticLogger().info("%d entries were retrieved".formatted(users.size()));
        return Expected.ofValue(users);
    }

    public Expected<User, Error> getById(int id) {
        Session session = sessionFactory.openSession();
        User user = session.find(User.class, id);
        session.close();
        if (user == null) {
            LogUtility.getStaticLogger().info("User with %d id wasn't found".formatted(id));
            return Expected.ofError(Error.USER_NOT_FOUND);
        }
        LogUtility.getStaticLogger().info("User with %d id was retrieved".formatted(id));
        return Expected.ofValue(user);
    }

    public Expected<User, Error> update(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            User dbUser = session.find(User.class, user.getId());
            if (dbUser == null) {
                return Expected.ofError(Error.USER_NOT_FOUND);
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
            LogUtility.getStaticLogger().info("User with %d id was updated".formatted(user.getId()));
            return Expected.ofValue(dbUser);
        } catch (Exception e) {
            LogUtility.getStaticLogger().severe("Failed to update user with %d id".formatted(user.getId()));
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public Expected<Void, Error> deleteById(int id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            User user = session.find(User.class, id);
            if (user == null) {
                session.getTransaction().rollback();
                return Expected.ofError(Error.USER_NOT_FOUND);
            }
            session.remove(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            LogUtility.getStaticLogger().severe("Failed to delete user with %d id".formatted(id));
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        LogUtility.getStaticLogger().info("User with %d id was deleted".formatted(id));
        return Expected.ofValue(null);
    }
}
