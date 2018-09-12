package com.humanharvest.organz.resolvers.config;

import java.util.Set;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.utilities.enums.Organ;

public interface ConfigResolver {

    void setTransplantProgramsForHospital(Hospital hospital, Set<Organ> transplantPrograms);
}
