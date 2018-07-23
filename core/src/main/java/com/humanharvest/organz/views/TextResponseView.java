package com.humanharvest.organz.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TextResponseView {
    private final String response;

    @JsonCreator
    public TextResponseView(
            @JsonProperty("response") String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
