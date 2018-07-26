package com.humanharvest.organz.utilities.web;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;

public class MedAutoCompleteURL extends GenericUrl {

    @Key
    private String query;

    public MedAutoCompleteURL(String encodedUrl) {
        super(encodedUrl);
    }

    public void setQueryString(String query) {
        this.query = query;
    }
}
