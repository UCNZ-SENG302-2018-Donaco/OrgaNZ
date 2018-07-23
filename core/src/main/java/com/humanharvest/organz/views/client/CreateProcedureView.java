package com.humanharvest.organz.views.client;

import java.time.LocalDate;


import com.humanharvest.organz.utilities.enums.Organ;


public class CreateProcedureView {


    private String summary;
    private String description;
    private LocalDate date;
    private Organ organ;

    public CreateProcedureView(String summary, String description, LocalDate date, Organ organ) {
        this.summary = summary;
        this.description = description;
        this.date = date;
        this.organ = organ;
    }

    public String getSummary() { return summary; }

    public void setSummary(String summary) { this.summary = summary; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }

    public Organ getOrgan() { return organ; }

    public void setOrgan(Organ organ) { this.organ = organ; }

}
