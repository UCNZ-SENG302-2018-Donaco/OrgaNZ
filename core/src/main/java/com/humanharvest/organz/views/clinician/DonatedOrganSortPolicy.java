package com.humanharvest.organz.views.clinician;

import com.humanharvest.organz.utilities.enums.DonatedOrganSortOptionsEnum;

public class DonatedOrganSortPolicy {

    private final DonatedOrganSortOptionsEnum sortOption;
    private final boolean isReversed;

    public DonatedOrganSortPolicy(DonatedOrganSortOptionsEnum sortOption, boolean isReversed) {
        this.sortOption = sortOption;
        this.isReversed = isReversed;
    }

    public DonatedOrganSortOptionsEnum getSortOption() {
        return sortOption;
    }

    public boolean isReversed() {
        return isReversed;
    }

}
