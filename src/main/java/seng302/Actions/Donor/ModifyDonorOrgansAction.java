package seng302.Actions.Donor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

/**
 * A reversible donor organ modification Action
 */
public class ModifyDonorOrgansAction extends Action {

    private Map<Organ, Boolean> changes = new HashMap<>();
    private Donor donor;

    /**
     * Create a new Action
     * @param donor The donor to be modified
     */
    public ModifyDonorOrgansAction(Donor donor) {
        this.donor = donor;
    }

    /**
     * Add a organ change to the donor. Should check the value is not already set before adding the change
     * @param organ The organ to be updated
     * @param newValue The new value
     * @throws OrganAlreadyRegisteredException Thrown if the organ is already set to that value
     */
    public void addChange(Organ organ, Boolean newValue) throws OrganAlreadyRegisteredException {
        if (donor.getOrganStatus().get(organ) == newValue) {
            throw new OrganAlreadyRegisteredException("That organ is already set to that value");
        }
        changes.put(organ, newValue);
    }

    @Override
    protected void execute() {
        runChanges(false);
    }

    @Override
    protected void unExecute() {
        runChanges(true);
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

        return String.format("Changed organ registration for client %d: %s %s:\n\n%s",
                donor.getUid(), donor.getFirstName(), donor.getLastName(), changesText);
    }

    @Override
    public String getUnexecuteText() {
        String changesText = changes.entrySet().stream()
                .map(entry -> formatChange(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));

        return String.format("Reversed these changes to organ registration for client %d: %s %s:\n\n%s",
                donor.getUid(), donor.getFirstName(), donor.getLastName(), changesText);
    }

    /**
     * Loops through the list of changes and applies them to the donor
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
                donor.setOrganStatus(organ, newState);
            } catch (OrganAlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }
}