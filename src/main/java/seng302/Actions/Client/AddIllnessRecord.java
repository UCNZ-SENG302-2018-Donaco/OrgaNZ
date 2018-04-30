package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.IllnessRecord;

public class AddIllnessRecord extends Action {

    private Client client;
    private IllnessRecord record;

    public AddIllnessRecord(Client client, IllnessRecord record) {
        this.client = client;
        this.record = record;

    }

    @Override
    public void execute() {
        client.addIllnessRecord(record);

    }

    @Override
    public void unExecute() {
        client.deleteIllnessRecord(record);
    }

    @Override
    public String getExecuteText() {
        return String.format("Added record for illness '%s' to the history of client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed the addition of record for illness '%s' from the history of client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }

}
