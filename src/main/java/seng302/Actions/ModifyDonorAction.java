package seng302.Actions;

import seng302.Donor;

import java.beans.Statement;
import java.util.HashMap;
import java.util.Map;

public class ModifyDonorAction implements Action {
    private Map<String, Object> executors = new HashMap<>();
    private Map<String, Object> unExecutors = new HashMap<>();
    private Donor donor;


    public ModifyDonorAction(Donor donor) {
        this.donor = donor;
    }

    public void addChange(String field, Object oldValue, Object newValue) {
        executors.put(field, newValue);
        unExecutors.put(field, oldValue);
    }

    @Override
    public void execute() {
        runChanges(executors);
    }

    @Override
    public void unExecute() {
        runChanges(unExecutors);
    }

    private void runChanges(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                Object[] var = {entry.getValue()};
                new Statement(donor, entry.getKey(), var).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
