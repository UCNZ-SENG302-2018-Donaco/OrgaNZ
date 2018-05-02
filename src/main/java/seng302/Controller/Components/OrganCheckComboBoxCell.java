package seng302.Controller.Components;

import java.util.HashSet;
import java.util.Set;

import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import seng302.Utilities.Enums.Organ;

import org.controlsfx.control.CheckComboBox;

/**
 * An editable table cell that holds a {@link CheckComboBox} for organ values. Changing the checked items triggers an
 * edit commit on the cell.
 * @param <T> The type of data record each row in the table represents.
 */
public class OrganCheckComboBoxCell<T> extends TableCell<T, Set<Organ>> {
    private final CheckComboBox<Organ> checkComboBox;

    /**
     * Creates a new organ {@link CheckComboBox} cell for the given column. Also binds the disabled property to that of
     * the table.
     * @param column The {@link Set<Organ>} column to create a date picker cell for.
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
     * Triggered whenever the {@link Set<Organ>} value of the cell is updated; it sets that new value in the
     * {@link CheckComboBox}.
     * @param item The new set of organs.
     * @param empty Whether the cell is now empty or not.
     */
    @Override
    protected void updateItem(Set<Organ> item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            for (Organ organ : item) {
                if (!checkComboBox.getCheckModel().isChecked(organ)) {
                    checkComboBox.getCheckModel().check(organ);
                }
            }
            setGraphic(checkComboBox);
        }
    }
}
