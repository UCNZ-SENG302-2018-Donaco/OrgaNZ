package com.humanharvest.organz.resolvers.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.springframework.beans.BeanUtils;

public class ClincianResolverMemory implements ClinicianResolver {

    public Clinician modifyClinician(Clinician clinician, ModifyClinicianObject modifyClinicianObject) {
        BeanUtils.copyProperties(modifyClinicianObject, clinician, modifyClinicianObject.getUnmodifiedFields());
        return clinician;
    }
}
