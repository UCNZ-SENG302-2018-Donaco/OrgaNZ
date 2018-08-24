package com.humanharvest.organz.controller.components;

import com.humanharvest.organz.utilities.enums.Organ;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import org.controlsfx.control.CheckComboBox;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An editable table cell that holds a {@link CheckComboBox} for organ values. Changing the checked items triggers an
 * edit commit on the cell.
 *
 * @param <T> The type of data record each row in the table represents.
 */
public class OrganCheckComboBoxCell<T> extends TableCell<T, Set<Organ>> {
    private final CheckComboBox<Organ> checkComboBox;

    /**
     * Creates a new organ {@link CheckComboBox} cell for the given column. Also binds the disabled property to that of
     * the table.
     *
     * @param column The {@link Set} of {@link Organ} column to create a date picker cell for.
     */
    public OrganCheckComboBoxCell(TableColumn<T, Set<Organ>> column) {
        checkComboBox = new CheckComboBox<>();
        checkComboBox.getItems().setAll(Organ.values());
        checkComboBox.disableProperty().bind(column.editableProperty().not());

        checkComboBox.addEventHandler(ComboBox.ON_SHOWN, event -> {
            final TableView<T> tableView = getTableView();
            tableView.getSelectionModel().select(getTableRow().getIndex());
            tableView.edit(tableView.getSelectionModel().getSelectedIndex(), column);
        });

        checkComboBox.addEventHandler(ComboBox.ON_HIDDEN, event -> {
            if (isEditing()) {
                commitEdit(new HashSet<>(checkComboBox.getCheckModel().getCheckedItems()));
            }
        });
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    /**
     * Triggered whenever the {@link Set} of {@link Organ} value of the cell is updated; it sets that new value in the
     * {@link CheckComboBox}.
     *
     * @param item  The new set of organs.
     * @param empty Whether the cell is now empty or not.
     */
    @Override
    protected void updateItem(Set<Organ> item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            checkComboBox.getCheckModel().clearChecks();
            for (Organ organ : item) {
                checkComboBox.getCheckModel().check(organ);
            }

            if (getTableColumn().isEditable()) {
                setGraphic(checkComboBox);
            } else {
                String text = checkComboBox.getCheckModel().getCheckedItems().stream()
                        .map(Organ::toString)
                        .collect(Collectors.joining(", "));
                setTooltip(new Tooltip(text));
                setGraphic(new Label(text));
            }
        }
    }
}
