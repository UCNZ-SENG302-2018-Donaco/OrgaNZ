package com.humanharvest.organz.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionResponseView {

    private final String resultText;
    private final boolean canUndo;
    private final boolean canRedo;

    @JsonCreator
    public ActionResponseView(
            @JsonProperty("resultText") String resultText,
            @JsonProperty("canUndo") boolean canUndo,
            @JsonProperty("canRedo") boolean canRedo) {
        this.resultText = resultText;
        this.canUndo = canUndo;
        this.canRedo = canRedo;
    }

    public String getResultText() {
        return resultText;
    }

    public boolean isCanUndo() {
        return canUndo;
    }

    public boolean isCanRedo() {
        return canRedo;
    }

}
