package com.humanharvest.organz.utilities.web;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.List;

public class MedAutoCompleteResponse extends GenericJson {

    @Key
    private List<String> suggestions;

    public List<String> getSuggestions() {
        return suggestions;
    }
}
