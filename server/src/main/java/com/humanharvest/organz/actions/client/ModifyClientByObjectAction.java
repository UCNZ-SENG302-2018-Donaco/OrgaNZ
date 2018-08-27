package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.utilities.type_converters.StringFormatter;
import com.humanharvest.organz.views.client.ModifyClientObject;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class ModifyClientByObjectAction extends ClientAction {

    private ModifyClientObject oldClientDetails;
    private ModifyClientObject newClientDetails;

    /**
     * Create a new Action
     *
     * @param client           The client to be modified
     * @param manager          The client manager to use when applying the changes.
     * @param oldClientDetails The object containing all the old details of the client record.
     * @param newClientDetails The object containing all the new details of the client record.
     */
    public ModifyClientByObjectAction(Client client, ClientManager manager, ModifyClientObject oldClientDetails,
                                      ModifyClientObject newClientDetails) {
        super(client, manager);
        this.oldClientDetails = oldClientDetails;
        this.newClientDetails = newClientDetails;
    }

    @Override
    protected void execute() {
        super.execute();
        BeanUtils.copyProperties(newClientDetails, client, newClientDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        BeanUtils.copyProperties(oldClientDetails, client, oldClientDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        String changesText = newClientDetails.getModifiedFields().stream()
                .map(Field::getName)
                .map(StringFormatter::unCamelCase)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for client %d: %s. \n"
                        + "These changes were made: \n\n%s",
                client.getUid(), client.getFullName(), changesText);
    }

    @Override
    public String getUnexecuteText() {
        String changesText = oldClientDetails.getModifiedFields().stream()
                .map(Field::getName)
                .map(StringFormatter::unCamelCase)
                .collect(Collectors.joining("\n"));

        return String.format("Reversed update for client %d: %s. \n"
                        + "These changes were reversed: \n\n%s",
                client.getUid(), client.getFullName(), changesText);
    }
}
