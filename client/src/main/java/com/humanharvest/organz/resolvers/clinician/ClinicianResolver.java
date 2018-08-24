package com.humanharvest.organz.resolvers.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;

import java.util.List;

public interface ClinicianResolver {

    Clinician modifyClinician(Clinician clinician, ModifyClinicianObject modifyClinicianObject);

    List<HistoryItem> getHistory(Clinician clinician);

}
