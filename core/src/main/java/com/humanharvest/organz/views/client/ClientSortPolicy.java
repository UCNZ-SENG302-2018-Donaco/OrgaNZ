package com.humanharvest.organz.views.client;

import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;

public class ClientSortPolicy {

    private final ClientSortOptionsEnum sortOption;
    private final boolean isReversed;

    public ClientSortPolicy(ClientSortOptionsEnum sortOption, boolean isReversed) {
        this.sortOption = sortOption;
        this.isReversed = isReversed;
    }

    public ClientSortOptionsEnum getSortOption() {
        return sortOption;
    }

    public boolean isReversed() {
        return isReversed;
    }

}
