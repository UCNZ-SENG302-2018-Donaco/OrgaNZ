package com.humanharvest.organz.resolvers.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class ClincianResolverMemory implements ClinicianResolver {

    @Override
    public Clinician modifyClinician(Clinician clinician, ModifyClinicianObject modifyClinicianObject) {
        BeanUtils.copyProperties(modifyClinicianObject, clinician, modifyClinicianObject.getUnmodifiedFields());
        return clinician;
    }

    @Override
    public List<HistoryItem> getHistory(Clinician clinician) {
        return clinician.getChangesHistory();
    }
}
