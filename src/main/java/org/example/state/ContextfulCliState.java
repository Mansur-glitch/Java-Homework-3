package org.example.state;

import org.example.CliContext;

public abstract class ContextfulCliState implements CliState {
    protected final CliContext context;

    public ContextfulCliState(CliContext context) {
        this.context = context;
    }

    public abstract String getWelcomeMessage();

    public ProcessResult keepState(String textResponse) {
        return new ProcessResult(textResponse + "\n\n" + getWelcomeMessage());
    }
}