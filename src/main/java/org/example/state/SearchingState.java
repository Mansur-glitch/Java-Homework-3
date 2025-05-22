package org.example.state;

import org.example.CliContext;
import org.example.data.User;
import org.example.util.Expected;
import org.example.util.InputVerifier;

public class SearchingState extends ContextfulCliState {
    public SearchingState(CliContext context) {
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

        Expected<User, String> expectedUser = context.getUserDao().getById(expectedId.value());
        if (!expectedUser.hasValue()) {
            return keepState(expectedUser.error());
        }

        return keepState(expectedUser.value().toString());
    }

    @Override
    public String getWelcomeMessage() {
        return "Getting user.\nEnter user id or 0 to return:";
    }
}
