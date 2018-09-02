package com.humanharvest.organz.resolvers.config;

import java.util.Set;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.utilities.enums.Organ;

public class ConfigResolverMemory implements ConfigResolver {

    @Override
    public void setTransplantProgramsForHospital(Hospital hospital, Set<Organ> transplantPrograms) {
        hospital.setTransplantPrograms(transplantPrograms);
    }
}
