package com.humanharvest.organz.views.client;

import java.time.LocalDate;

public class SingleDateView {

    private LocalDate date;

    public SingleDateView() {}

    public SingleDateView(LocalDate date) {
        this.date = date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

}
