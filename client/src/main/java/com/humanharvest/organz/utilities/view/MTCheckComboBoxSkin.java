package com.humanharvest.organz.utilities.view;

import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.cell.CheckBoxListCell;
import org.controlsfx.control.CheckComboBox;

import java.util.Collections;

public class MTCheckComboBoxSkin<T> extends BehaviorSkinBase<CheckComboBox<T>, BehaviorBase<CheckComboBox<T>>> {

    private static final PseudoClass PRESSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("pressed");
    private final ComboBox<T> comboBox;
    private final ListCell<T> buttonCell;
    // data
    private final CheckComboBox<T> control;
    private final ObservableList<T> items;
    private final ReadOnlyUnbackedObservableList<Integer> selectedIndices;
    private final ReadOnlyUnbackedObservableList<T> selectedItems;
    private boolean isShowing = false;
    private BooleanProperty touchPressed = new BooleanPropertyBase(false) {
        protected void invalidated() {
//            getPopupContent().pseudoClassStateChanged(MTComboBoxListViewSkin.PRESSED_PSEUDO_CLASS, get());
        }

        public Object getBean() {
            return this;
        }

        public String getName() {
            return "pressed";
        }
    };


    /**************************************************************************
     *
     * Constructors
     *
     **************************************************************************/

    @SuppressWarnings("unchecked")
    public MTCheckComboBoxSkin(final CheckComboBox<T> control) {
        super(control, new BehaviorBase<>(control, Collections.emptyList()));

        this.control = control;
        this.items = control.getItems();
        comboBox.setAy

                selectedIndices = (ReadOnlyUnbackedObservableList<Integer>) control.getCheckModel().getCheckedIndices();
        selectedItems = (ReadOnlyUnbackedObservableList<T>) control.getCheckModel().getCheckedItems();

        comboBox = new ComboBox<T>(items) {
            @Override
            protected javafx.scene.control.Skin<?> createDefaultSkin() {
                return new ComboBoxListViewSkin<T>(this) {
                    // overridden to prevent the popup from disappearing
                    @Override
                    protected boolean isHideOnClickEnabled() {
                        return false;
                    }
                };
            }
        };
        comboBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // installs a custom CheckBoxListCell cell factory
        comboBox.setCellFactory(listView -> {
            CheckBoxListCell<T> result = new CheckBoxListCell<>(control::getItemBooleanProperty);
            result.converterProperty().bind(control.converterProperty());
            return result;
        });

        // we render the selection into a custom button cell, so that it can
        // be pretty printed (e.g. 'Item 1, Item 2, Item 10').
        buttonCell = new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                // we ignore whatever item is selected, instead choosing
                // to display the selected item text using commas to separate
                // each item
                setText(buildString());
            }
        };
        comboBox.setButtonCell(buttonCell);
        comboBox.setValue((T) buildString());

        // The zero is a dummy value - it just has to be legally within the bounds of the
        // item count for the CheckComboBox items list.
        selectedIndices.addListener((ListChangeListener<Integer>) c -> buttonCell.updateIndex(0));

        getChildren().add(comboBox);
    }


    /**************************************************************************
     *
     * Overriding public API
     *
     **************************************************************************/

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return comboBox.minWidth(height);
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return comboBox.minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return comboBox.prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return comboBox.prefHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }


    /**************************************************************************
     *
     * Implementation
     *
     **************************************************************************/

    protected String buildString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0, max = selectedItems.size(); i < max; i++) {
            T item = selectedItems.get(i);
            if (control.getConverter() == null) {
                sb.append(item);
            } else {
                sb.append(control.getConverter().toString(item));
            }
            if (i < max - 1) {
                sb.append(", "); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }
}
}
