package org.example;

import org.example.util.LogUtility;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App {
    public static void main(String[] args) {
        LogUtility.logger().info("Application started");
        try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory()) {
            CliContext cliContext = new CliContext(sessionFactory);
            cliContext.run();
        } catch (Exception e) {
            LogUtility.logger().severe(e.getMessage());
            System.out.println("\n\nUnexpected error! See log.txt\n");
        }
        LogUtility.logger().info("Application exiting\n");
    }
}