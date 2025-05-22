package org.example.state;

@FunctionalInterface
public interface CliState {
    ProcessResult process(String input);

    record ProcessResult(CliState nextState, String textResponse) {
        public ProcessResult(String textResponse) {
            this(null, textResponse);
        }
    }
}
