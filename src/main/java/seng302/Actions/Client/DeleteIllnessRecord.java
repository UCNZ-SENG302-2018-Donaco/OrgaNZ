package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.IllnessRecord;

public class DeleteIllnessRecord extends Action {

    private Client client;
    private IllnessRecord record;

    public DeleteIllnessRecord(Client client, IllnessRecord record) {
        this.client = client;
        this.record = record;


    }

    @Override
    public void execute() {
        client.deleteIllnessRecord(record);

    }

    @Override
    public void unExecute() {
        client.addIllnessRecord(record);
    }

    @Override
    public String getExecuteText() {
        return String.format("Removed record for illness '%s' from the history of client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added record for illness '%s' to the history of client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }
}
