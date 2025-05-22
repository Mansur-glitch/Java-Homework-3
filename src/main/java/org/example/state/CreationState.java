package org.example.state;

import org.example.CliContext;
import org.example.data.User;
import org.example.util.Expected;
import org.example.util.InputVerifier;

public class CreationState extends ContextfulCliState {
    protected final User blank;

    public CreationState(CliContext context) {
        super(context);
        blank = new User();
    }

    @Override
    public ProcessResult process(String input) {
        return enterName(input);
    }

    @Override
    public String getWelcomeMessage() {
        return "Creating new user.\nEnter name:";
    }

    public ProcessResult enterName(String input) {
        Expected<String, String> expected = InputVerifier.parseUserName(input);
        if (!expected.hasValue()) {
            return new ProcessResult(expected.error());
        }
        blank.setName(expected.value());
        return new ProcessResult(this::enterAge, "Enter age:");
    }

    public ProcessResult enterAge(String input) {
        Expected<Integer, String> expected = InputVerifier.parseUserAge(input);
        if (!expected.hasValue()) {
            return new ProcessResult(expected.error());
        }
        blank.setAge(expected.value());
        return new ProcessResult(this::enterEmail, "Enter email:");
    }

    public ProcessResult enterEmail(String input) {
        Expected<String, String> expectedEmail = InputVerifier.parseUserEmail(input);
        if (!expectedEmail.hasValue()) {
            return new ProcessResult(expectedEmail.error());
        }
        blank.setEmail(expectedEmail.value());

        Expected<String, String> expectedResponse = databaseAction(blank);

        MainMenuState mainMenuState = new MainMenuState(context);
        String textResponse = expectedResponse.hasValue() ?
                expectedResponse.value() :
                expectedResponse.error();
        textResponse = textResponse + "\n\n" + mainMenuState.getWelcomeMessage();
        return new ProcessResult(mainMenuState, textResponse);
    }

    public Expected<String, String> databaseAction(User user) {
        Expected<Integer, String> expected = context.getUserDao().create(user);
        if (!expected.hasValue()) {
            return Expected.ofError(expected.error());
        }
        return Expected.ofValue("User with %d id successfully created!".formatted(expected.value()));
    }
}
