package seng302.Controller.Components;

import java.time.LocalDate;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DatePickerCell<T> extends TableCell<T, LocalDate> {
    private final DatePicker datePicker;

    public DatePickerCell(TableColumn<T, LocalDate> column) {
        datePicker = new DatePicker();
        datePicker.editableProperty().bind(column.editableProperty());
        datePicker.disableProperty().bind(column.editableProperty().not());
        datePicker.setOnShowing(event -> {
            final TableView<T> tableView = getTableView();
            tableView.getSelectionModel().select(getTableRow().getIndex());
            tableView.edit(tableView.getSelectionModel().getSelectedIndex(), column);
        });
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditing()) {
                commitEdit(newValue);
            }
        });
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            datePicker.setValue(item);
            setGraphic(datePicker);
        }
    }
}
