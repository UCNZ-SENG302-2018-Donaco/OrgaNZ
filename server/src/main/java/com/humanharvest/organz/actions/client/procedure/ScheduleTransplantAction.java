package com.humanharvest.organz.actions.client.procedure;

import java.time.LocalDate;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.utilities.exceptions.DateOutOfBoundsException;

/**
 * A reversible action that will schedule a {@link TransplantRecord} and add it to the
 * {@link com.humanharvest.organz.ProcedureRecord}s of the recipient.
 */
public class ScheduleTransplantAction extends AddProcedureRecordAction {

    private DonatedOrgan donatedOrgan;

    /**
     * Creates a new action to schedule a transplant procedure.
     *
     * @param organ The {@link DonatedOrgan} that is going to be transplanted.
     * @param request The {@link TransplantRequest} to be fulfilled.
     * @param manager The {@link ClientManager} to apply the changes with.
     */
    public ScheduleTransplantAction(DonatedOrgan organ, TransplantRequest request, Hospital hospital,
            LocalDate scheduledDate, ClientManager manager) throws DateOutOfBoundsException {
        // Create a new TransplantRecord using the parameters and pass to AddProcedureRecord constructor
        super(request.getClient(), new TransplantRecord(organ, request, hospital, scheduledDate), manager);

        // Check that date is not out of bounds
        if (scheduledDate.isBefore(LocalDate.now())) {
            throw new DateOutOfBoundsException("A transplant cannot be scheduled in the past.");
        }
        donatedOrgan = organ;
    }

    @Override
    public void execute() {
        super.execute();
        donatedOrgan.setAvailable(false);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        donatedOrgan.setAvailable(true);
    }
}
