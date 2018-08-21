package com.humanharvest.organz.controller.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * An editable table cell that holds a date picker. Changing the date picker triggers an edit commit on the cell.
 * @param <T> The type of data record each row in the table represents.
 */
public class DatePickerCell<T> extends TableCell<T, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DatePicker datePicker;

    /**
     * Creates a new date picker cell for the given column. Binds editable/disabled properties to those of the table.
     * @param column The {@link LocalDate} column to create a date picker cell for.
     */
    public DatePickerCell(TableColumn<T, LocalDate> column) {
        datePicker = new DatePicker();
        datePicker.editableProperty().bind(column.editableProperty());
        datePicker.disableProperty().bind(column.editableProperty().not());
        datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue && newValue) {
                final TableView<T> tableView = getTableView();
                tableView.getSelectionModel().select(getTableRow().getIndex());
                tableView.edit(tableView.getSelectionModel().getSelectedIndex(), column);
            } else if (oldValue && !newValue) {
                if (isEditing()) {
                    cancelEdit();
                }
            }
        });
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditing()) {
                commitEdit(newValue);
            }
        });
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    /**
     * Triggered whenever the {@link LocalDate} value of the cell is updated; it sets that new value in the date picker.
     * @param item The new date.
     * @param empty Whether the cell is now empty or not.
     */
    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            datePicker.setValue(item);
            if (getTableColumn().isEditable()) {
                setGraphic(datePicker);
            } else {
                setGraphic(new Label(datePicker.getValue().format(formatter)));
            }
        }
    }
}
