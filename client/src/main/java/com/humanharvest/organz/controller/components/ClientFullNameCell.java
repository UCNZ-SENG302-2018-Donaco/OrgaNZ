package com.humanharvest.organz.controller.components;

import javafx.scene.control.ListCell;

import com.humanharvest.organz.Client;

public class ClientFullNameCell extends ListCell<Client> {

    public ClientFullNameCell() {
    }

    @Override
    public void updateItem(Client item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.getFullName());
        }
    }
}
