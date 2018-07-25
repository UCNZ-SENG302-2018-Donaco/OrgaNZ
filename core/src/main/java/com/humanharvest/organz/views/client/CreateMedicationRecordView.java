package com.humanharvest.organz.views.client;

import java.time.LocalDate;

public class CreateMedicationRecordView {

    private String name;
    private LocalDate started;

    public CreateMedicationRecordView() {
    }

    public CreateMedicationRecordView(String name, LocalDate started) {
        this.name = name;
        this.started = started;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStarted() {
        return started;
    }

    public void setStarted(LocalDate started) {
        this.started = started;
    }
}
