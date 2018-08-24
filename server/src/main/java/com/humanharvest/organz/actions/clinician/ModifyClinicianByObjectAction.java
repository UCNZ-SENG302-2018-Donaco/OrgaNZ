package com.humanharvest.organz.actions.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class ModifyClinicianByObjectAction extends ClinicianAction {

    private ModifyClinicianObject oldClinicianDetails;
    private ModifyClinicianObject newClinicianDetails;

    /**
     * Create a new Action
     *
     * @param clinician           the clinician to be modified
     * @param manager             the clinician manager
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
        String changesText = newClinicianDetails.getModifiedFields().stream()
                .map(Field::getName)
                .map(this::unCamelCase)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for clinician %d: %s. \n"
                        + "These changes were made: \n\n%s",
                clinician.getStaffId(), clinician.getFullName(), changesText);
    }

    @Override
    public String getUnexecuteText() {
        String changesText = oldClinicianDetails.getModifiedFields().stream()
                .map(Field::getName)
                .map(this::unCamelCase)
                .collect(Collectors.joining("\n"));

        return String.format("Reversed update for clinician %d: %s. \n"
                        + "These changes were reversed: \n\n%s",
                clinician.getStaffId(), clinician.getFullName(), changesText);
    }

    private String unCamelCase(String inCamelCase) {
        String unCamelCased = inCamelCase.replaceAll("([a-z])([A-Z]+)", "$1 $2");
        return unCamelCased.substring(0, 1).toUpperCase() + unCamelCased.substring(1);
    }
}
