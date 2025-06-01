package org.example;

import org.example.data.UserDao;
import org.example.state.CliState;
import org.example.state.MainMenuState;
import org.example.util.SimpleMenu;
import org.hibernate.SessionFactory;

import java.util.Scanner;

public class CliContext {
    private final UserDao userDao;
    private final Scanner scanner;
    private CliState state;
    private boolean exitFlag;

    public CliContext(SessionFactory sessionFactory) {
        MainMenuState mainMenuState = new MainMenuState(this, new SimpleMenu<>());
        System.out.println(mainMenuState.getWelcomeMessage());

        this.state = mainMenuState;
        this.userDao = new UserDao(sessionFactory);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (!exitFlag) {
            String input = scanner.nextLine();
            CliState.ProcessResult result = state.process(input);
            if (result.nextState() != null) {
                state = result.nextState();
            }
            System.out.println(result.textResponse());
        }

    }

    public boolean isExitFlag() {
        return exitFlag;
    }

    public void setExitFlag(boolean exitFlag) {
        this.exitFlag = exitFlag;
    }

    public UserDao getUserDao() {
        return userDao;
    }
}
