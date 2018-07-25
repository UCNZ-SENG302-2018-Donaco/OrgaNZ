package com.humanharvest.organz.resolvers.clinician;

import java.util.List;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.springframework.beans.BeanUtils;

public class ClincianResolverMemory implements ClinicianResolver {

    public Clinician modifyClinician(Clinician clinician, ModifyClinicianObject modifyClinicianObject) {
        BeanUtils.copyProperties(modifyClinicianObject, clinician, modifyClinicianObject.getUnmodifiedFields());
        return clinician;
    }

    @Override
    public List<HistoryItem> getHistory(Clinician clinician) {
        return clinician.getChangesHistory();
    }
}
