package org.example.state;

import org.example.CliContext;
import org.example.data.User;
import org.example.util.CliMenu;
import org.example.util.Expected;

import java.util.List;

public class MainMenuState extends ContextfulCliState {
    private final CliMenu<ProcessResult> menu;

    public MainMenuState(CliContext context) {
        super(context);
        menu = new CliMenu<>();

        menu.addEntry('1', "Create new user", () -> {
            CreationState nextState = new CreationState(context);
            return new ProcessResult(nextState, nextState.getWelcomeMessage());
        });
        menu.addEntry('2', "Print all users", this::printAllUsers);
        menu.addEntry('3', "Get user by id", () -> {
            SearchingState nextState = new SearchingState(context);
            return new ProcessResult(nextState, nextState.getWelcomeMessage());
        });
        menu.addEntry('4', "Update user", () -> {
            UpdatingState nextState = new UpdatingState(context);
            return new ProcessResult(nextState, nextState.getWelcomeMessage());
        });
        menu.addEntry('5', "Delete user", () -> {
            DeletionState nextState = new DeletionState(context);
            return new ProcessResult(nextState, nextState.getWelcomeMessage());
        });
        menu.addEntry('0', "Exit", () -> {
            context.setExitFlag(true);
            return new ProcessResult("\nExiting...\n");
        });
    }

    @Override
    public ProcessResult process(String input) {
        Expected<ProcessResult, String> expected = menu.process(input);
        if (expected.hasValue()) {
            return expected.value();
        }
        return keepState(expected.error());
    }

    @Override
    public String getWelcomeMessage() {
        return menu.buildMenuString();
    }

    public ProcessResult printAllUsers() {
        Expected<List<User>, String> expected = context.getUserDao().findAll();
        if (!expected.hasValue()) {
            return keepState(expected.error());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Found users:");
        for (User u : expected.value()) {
            builder.append('\n').append(u.toString());
        }
        return keepState(builder.toString());
    }
}
