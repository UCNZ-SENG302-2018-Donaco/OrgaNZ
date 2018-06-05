package com.humanharvest.organz.actions.client;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;

/**
 * A reversible client organ modification Action
 */
public class ModifyClientOrgansAction extends ClientAction {

    private ClientManager manager;
    private Map<Organ, Boolean> changes = new HashMap<>();
    private Client client;

    /**
     * Create a new Action
     * @param client The client to be modified
     */
    public ModifyClientOrgansAction(Client client, ClientManager manager) {
        this.client = client;
        this.manager = manager;
    }

    /**
     * Add a organ change to the client. Should check the value is not already set before adding the change
     * @param organ The organ to be updated
     * @param newValue The new value
     * @throws OrganAlreadyRegisteredException Thrown if the organ is already set to that value
     */
    public void addChange(Organ organ, Boolean newValue) throws OrganAlreadyRegisteredException {
        if (client.getOrganDonationStatus().get(organ) == newValue) {
            throw new OrganAlreadyRegisteredException("That organ is already set to that value");
        }
        changes.put(organ, newValue);
    }

    @Override
    protected void execute() {
        super.execute();
        runChanges(false);
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        runChanges(true);
        manager.applyChangesTo(client);
    }

    private String formatChange(Organ organ, boolean newValue) {
        if (newValue) {
            return String.format("Registered %s for donation.", organ.toString());
        } else {
            return String.format("Deregistered %s for donation.", organ.toString());
        }
    }

    @Override
    public String getExecuteText() {
        String changesText = changes.entrySet().stream()
                .map(entry -> formatChange(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));

        return String.format("Changed organ donation registration for client %d: %s:\n\n%s",
                client.getUid(), client.getFullName(), changesText);
    }

    @Override
    public String getUnexecuteText() {
        String changesText = changes.entrySet().stream()
                .map(entry -> formatChange(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));

        return String.format("Reversed these changes to organ donation registration for client %d: %s:\n\n%s",
                client.getUid(), client.getFullName(), changesText);
    }

    /**
     * Loops through the list of changes and applies them to the client
     * @param isUndo If true, negate all booleans
     */
    private void runChanges(boolean isUndo) {
        for (Map.Entry<Organ, Boolean> entry : changes.entrySet()) {
            try {
                Organ organ = entry.getKey();
                boolean newState = entry.getValue();
                if (isUndo) {
                    newState = !newState;
                }
                client.setOrganDonationStatus(organ, newState);
            } catch (OrganAlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Client getAffectedClient() {
        return client;
    }
}