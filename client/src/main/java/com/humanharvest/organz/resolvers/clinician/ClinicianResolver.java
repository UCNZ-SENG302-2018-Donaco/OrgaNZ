package com.humanharvest.organz.resolvers.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;

public interface ClinicianResolver {

    Clinician modifyClinician(Clinician clinician, ModifyClinicianObject modifyClinicianObject);

}
