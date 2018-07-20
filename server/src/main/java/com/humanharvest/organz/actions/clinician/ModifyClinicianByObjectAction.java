package com.humanharvest.organz.actions.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.springframework.beans.BeanUtils;

public class ModifyClinicianByObjectAction extends ClinicianAction {

    private ModifyClinicianObject oldClinicianDetails;
    private ModifyClinicianObject newClinicianDetails;

    /**
     * Create a new Action
     * @param clinician the clinician to be modified
     * @param manager the clinician manager
     * @param oldClinicianDetails previous details of the clinician
     * @param newClinicianDetails updated details of the clinician
     */
    public ModifyClinicianByObjectAction(Clinician clinician, ClinicianManager manager, ModifyClinicianObject
            oldClinicianDetails, ModifyClinicianObject newClinicianDetails) {
        super(clinician, manager);
        this.oldClinicianDetails = oldClinicianDetails;
        this.newClinicianDetails = newClinicianDetails;
    }

    @Override
    protected void execute() {
        BeanUtils.copyProperties(newClinicianDetails, clinician, newClinicianDetails.getUnmodifiedFields());
        manager.applyChangesTo(clinician);
    }

    @Override
    protected void unExecute() {
        BeanUtils.copyProperties(oldClinicianDetails, clinician, oldClinicianDetails.getUnmodifiedFields());
        manager.applyChangesTo(clinician);
    }

    @Override
    public String getExecuteText() {
        return "Todo";
    }

    @Override
    public String getUnexecuteText() {
        return "Todo";
    }

}
