package org.example.state;

import org.example.CliContext;
import org.example.data.User;
import org.example.data.UserDao;
import org.example.util.CliMenu;
import org.example.util.ErrorBase;
import org.example.util.Expected;

import java.util.List;

import static org.example.util.MainMenuEntry.*;

public class MainMenuState extends ContextfulCliState {
    private final CliMenu<Character, ProcessResult> menu;

    public MainMenuState(CliContext context, CliMenu<Character, ProcessResult> menu) {
        super(context);
        this.menu = menu;

        menu.addEntry(CREATE.getKey(), CREATE.getText(), () -> {
            CreationState nextState = new CreationState(context);
            return new ProcessResult(nextState, nextState.getWelcomeMessage());
        });
        menu.addEntry(PRINT_ALL.getKey(), PRINT_ALL.getText(), this::printAllUsers);
        menu.addEntry(GET_BY_ID.getKey(), GET_BY_ID.getText(), () -> {
            SearchingState nextState = new SearchingState(context);
            return new ProcessResult(nextState, nextState.getWelcomeMessage());
        });
        menu.addEntry(UPDATE.getKey(), UPDATE.getText(), () -> {
            UpdatingState nextState = new UpdatingState(context);
            return new ProcessResult(nextState, nextState.getWelcomeMessage());
        });
        menu.addEntry(DELETE.getKey(), DELETE.getText(), () -> {
            DeletionState nextState = new DeletionState(context);
            return new ProcessResult(nextState, nextState.getWelcomeMessage());
        });
        menu.addEntry(EXIT.getKey(), EXIT.getText(), () -> {
            context.setExitFlag(true);
            return new ProcessResult("\nExiting...\n");
        });
    }

    @Override
    public ProcessResult process(String input) {
        Expected<ProcessResult, ErrorBase> expected = menu.process(input);
        if (expected.hasValue()) {
            return expected.value();
        }
        return keepState(expected.error().toString());
    }

    @Override
    public String getWelcomeMessage() {
        return menu.toString();
    }

    public ProcessResult printAllUsers() {
        Expected<List<User>, UserDao.Error> expected = context.getUserDao().findAll();
        if (!expected.hasValue()) {
            return keepState(expected.error().toString());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Found users:");
        for (User u : expected.value()) {
            builder.append('\n').append(u.toString());
        }
        return keepState(builder.toString());
    }
}
