package com.humanharvest.organz.controller.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.TableCell;

/**
 * Formats a table cell that holds a {@link LocalDate} value to display that value in the date format.
 */
public class FormattedLocalDateCell<S> extends TableCell<S, LocalDate> {

    private final DateTimeFormatter dateTimeFormat;

    /**
     * Formats a table cell that holds a {@link LocalDate} value to display that value in the date format.
     *
     * @param dateTimeFormat The DateTimeFormatter format to apply to the cells
     */
    public FormattedLocalDateCell(DateTimeFormatter dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.format(dateTimeFormat));
        }
    }

}
