package org.example.state;

import org.example.CliContext;
import org.example.data.User;
import org.example.util.Expected;
import org.example.util.InputVerifier;

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
        Expected<Integer, String> expectedId = InputVerifier.parseId(input);
        if (!expectedId.hasValue()) {
            return new ProcessResult(expectedId.error());
        }
        int id = expectedId.value();

        if (expectedId.value() == 0) {
            MainMenuState mainMenuState = new MainMenuState(context);
            return new ProcessResult(mainMenuState, mainMenuState.getWelcomeMessage());
        }

        Expected<User, String> expectedUser = context.getUserDao().getById(id);
        if (!expectedUser.hasValue()) {
            return keepState(expectedUser.error());
        }

        blank.setId(id);
        return new ProcessResult(this::enterName, "Enter name:");
    }

    @Override
    public Expected<String, String> databaseAction(User user) {
        Expected<User, String> expected = context.getUserDao().update(user);
        if (!expected.hasValue()) {
            return Expected.ofError(expected.error());
        }
        return Expected.ofValue("Updated %s".formatted(expected.value()));
    }
}
