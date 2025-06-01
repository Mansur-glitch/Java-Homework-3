package org.example.state;

import org.example.CliContext;
import org.example.data.User;
import org.example.data.UserDao;
import org.example.util.Expected;
import org.example.util.InputVerifier;
import org.example.util.SimpleMenu;

public class SearchingState extends ContextfulCliState {
    public SearchingState(CliContext context) {
        super(context);
    }

    @Override
    public ProcessResult process(String input) {
        Expected<Integer, InputVerifier.Error> expectedId = InputVerifier.parseId(input);
        if (!expectedId.hasValue()) {
            return keepState(expectedId.error().toString());
        }

        if (expectedId.value() == 0) {
            MainMenuState mainMenuState = new MainMenuState(context, new SimpleMenu<>());
            return new ProcessResult(mainMenuState, mainMenuState.getWelcomeMessage());
        }

        Expected<User, UserDao.Error> expectedUser = context.getUserDao().getById(expectedId.value());
        if (!expectedUser.hasValue()) {
            return keepState(expectedUser.error().toString());
        }

        return keepState(expectedUser.value().toString());
    }

    @Override
    public String getWelcomeMessage() {
        return "Getting user.\nEnter user id or 0 to return:";
    }
}
