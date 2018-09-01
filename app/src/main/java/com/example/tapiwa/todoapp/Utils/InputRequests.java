package com.example.tapiwa.todoapp.Utils;

public class InputRequests {

    private InputRequestType inputRequest;

    public InputRequests() {

    }

    public InputRequestType getInputRequest() {
        return inputRequest;
    }

    public void setInputRequest(InputRequestType inputRequest) {
        this.inputRequest = inputRequest;
    }

    public void closeInputRequest() {
        this.inputRequest = InputRequestType.NONE;
    }

    public static enum InputRequestType {
        RENAME_PROJECT,
        CREATE_NEW_PROJECT,
        NONE,
        ADD_GROUP_MEMBER,
        CREATE_NEW_TASK,
        RENAME_TASK
    }
}
