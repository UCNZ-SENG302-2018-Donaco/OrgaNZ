package com.humanharvest.organz.resolvers.clinician;

import java.util.List;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;

public interface ClinicianResolver {

    Clinician modifyClinician(Clinician clinician, ModifyClinicianObject modifyClinicianObject);

    List<HistoryItem> getHistory(Clinician clinician);

}
