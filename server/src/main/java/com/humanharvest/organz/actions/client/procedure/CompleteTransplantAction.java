package com.humanharvest.organz.actions.client.procedure;

import java.time.LocalDate;
import java.time.LocalTime;

import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.client.ClientAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.exceptions.DateOutOfBoundsException;

/**
 * A reversible action that will resolve a transplant record and correctly link the organ to the recipient, as well
 * as the transplant request.
 */
public class CompleteTransplantAction extends ClientAction {

    private final TransplantRecord record;

    /**
     * Create a new complete action. The date of the record cannot be in the future
     *
     * @param record The record to mark as complete
     * @param manager The ClientManager to use to apply changes
     * @throws DateOutOfBoundsException Thrown if the transplant record has it's date set to the future
     */
    public CompleteTransplantAction(TransplantRecord record, ClientManager manager)
            throws DateOutOfBoundsException {
        super(record.getClient(), manager);
        if (record.isCompleted()) {
            throw new DateOutOfBoundsException("The transplant cannot be resolved twice");
        }
        this.record = record;
    }

    @Override
    protected void execute() {
        super.execute();
        record.setCompleted(true);
        record.getOrgan().setReceiver(record.getClient());

        TransplantRequest request = record.getRequest();
        request.setResolvedReason("The transplant has been completed");
        if (record.getDate().isAfter(LocalDate.now())) {
            record.setDate(LocalDate.now());
        }
        request.setResolvedDateTime(record.getDate().atTime(LocalTime.now()));
        request.setStatus(TransplantRequestStatus.COMPLETED);

        manager.applyChangesTo(request);
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        super.unExecute();

        record.setCompleted(false);
        record.getOrgan().setReceiver(null);

        TransplantRequest request = record.getRequest();
        request.setStatus(TransplantRequestStatus.SCHEDULED);
        request.setResolvedDateTime(null);
        request.setResolvedReason(null);

        manager.applyChangesTo(request);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Completed %s's %s transplant.",
                record.getReceiver().getFullName(), record.getOrgan().getOrganType().toString());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed completion of %s's %s transplant.",
                record.getReceiver().getFullName(), record.getOrgan().getOrganType().toString());
    }
}
