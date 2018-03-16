package seng302.Actions;

import seng302.Donor;
import seng302.Utilities.Organ;
import seng302.Utilities.OrganAlreadyRegisteredException;

import java.util.HashMap;
import java.util.Map;

public class ModifyDonorOrgansAction implements Action {
    private Map<Organ, Boolean> executors = new HashMap<>();
    private Map<Organ, Boolean> unExecutors = new HashMap<>();
    private Donor donor;


    public ModifyDonorOrgansAction(Donor donor) {
        this.donor = donor;
    }

    public void addChange(Organ organ, Boolean oldValue, Boolean newValue) {
        executors.put(organ, newValue);
        unExecutors.put(organ, oldValue);
    }

    @Override
    public void execute() {
        for (Map.Entry<Organ, Boolean> entry : executors.entrySet()) {
            try {
                donor.setOrganStatus(entry.getKey(), entry.getValue());
            } catch (OrganAlreadyRegisteredException e) {
                unExecutors.remove(entry.getKey());
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void unExecute() {
        for (Map.Entry<Organ, Boolean> entry : unExecutors.entrySet()) {
            try {
                donor.setOrganStatus(entry.getKey(), entry.getValue());
            } catch (OrganAlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }
}
