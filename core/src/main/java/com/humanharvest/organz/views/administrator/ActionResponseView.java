package com.humanharvest.organz.views.administrator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionResponseView {

    private final String resultText;
    private final boolean moreActions;

    @JsonCreator
    public ActionResponseView(
            @JsonProperty("resultText") String resultText,
            @JsonProperty("moreActions") boolean moreActions) {
        this.resultText = resultText;
        this.moreActions = moreActions;
    }

    public String getResultText() {
        return resultText;
    }

    public boolean isMoreActions() {
        return moreActions;
    }

}
