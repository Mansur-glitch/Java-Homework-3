package org.example.state;

import org.example.CliContext;
import org.example.util.Expected;
import org.example.util.InputVerifier;

public class DeletionState extends ContextfulCliState {
    public DeletionState(CliContext context) {
        super(context);
    }

    @Override
    public ProcessResult process(String input) {
        Expected<Integer, String> expectedId = InputVerifier.parseId(input);
        if (!expectedId.hasValue()) {
            return keepState(expectedId.error());
        }

        if (expectedId.value() == 0) {
            MainMenuState mainMenuState = new MainMenuState(context);
            return new ProcessResult(mainMenuState, mainMenuState.getWelcomeMessage());
        }

        Expected<Void, String> expectedUser = context.getUserDao().deleteById(expectedId.value());
        if (!expectedUser.hasValue()) {
            return keepState(expectedUser.error());
        }

        return keepState("User with specified id successfully deleted!");
    }

    @Override
    public String getWelcomeMessage() {
        return "Deleting user.\nEnter user id or 0 to return:";
    }
}
