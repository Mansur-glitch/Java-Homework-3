package org.example.state;

import org.example.CliContext;
import org.example.data.User;
import org.example.data.UserDao;
import org.example.util.Expected;
import org.example.util.InputVerifier;
import org.example.util.SimpleMenu;

public class UpdatingState extends CreationState {
    public UpdatingState(CliContext context) {
        super(context);
    }

    @Override
    public ProcessResult process(String input) {
        return enterId(input);
    }

    @Override
    public String getWelcomeMessage() {
        return "Updating user.\nEnter user id or 0 to return:";
    }

    public ProcessResult enterId(String input) {
        Expected<Integer, InputVerifier.Error> expectedId = InputVerifier.parseId(input);
        if (!expectedId.hasValue()) {
            return new ProcessResult(expectedId.error().toString());
        }
        int id = expectedId.value();

        if (expectedId.value() == 0) {
            MainMenuState mainMenuState = new MainMenuState(context, new SimpleMenu<>());
            return new ProcessResult(mainMenuState, mainMenuState.getWelcomeMessage());
        }

        Expected<User, UserDao.Error> expectedUser = context.getUserDao().getById(id);
        if (!expectedUser.hasValue()) {
            return keepState(expectedUser.error().toString());
        }

        blank.setId(id);
        return new ProcessResult(this::enterName, "Enter name:");
    }

    @Override
    public Expected<String, String> databaseAction(User user) {
        Expected<User, UserDao.Error> expected = context.getUserDao().update(user);
        if (!expected.hasValue()) {
            return Expected.ofError(expected.error().toString());
        }
        return Expected.ofValue("Updated %s".formatted(expected.value()));
    }
}
