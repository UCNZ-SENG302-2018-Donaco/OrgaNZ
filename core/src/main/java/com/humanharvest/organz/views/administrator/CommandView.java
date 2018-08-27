package com.humanharvest.organz.views.administrator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommandView {

    private final String command;

    @JsonCreator
    public CommandView(
            @JsonProperty("command") String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
