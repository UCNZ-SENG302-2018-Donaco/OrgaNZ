package com.humanharvest.organz.controller.components;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.TableCell;

/**
 * Formats a table cell that holds a {@link LocalDateTime} value to display that value in the date time format.
 */
public class FormattedLocalDateTimeCell<S> extends TableCell<S, LocalDateTime> {

    private final DateTimeFormatter dateTimeFormat;

    /**
     * Formats a table cell that holds a {@link LocalDateTime} value to display that value in the date time format.
     *
     * @param dateTimeFormat The DateTimeFormatter format to apply to the cells
     */
    public FormattedLocalDateTimeCell(DateTimeFormatter dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    @Override
    protected void updateItem(LocalDateTime item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            setText(item.format(dateTimeFormat));
        }
    }
}
