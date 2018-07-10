package com.humanharvest.organz.utilities.pico_type_converters;

import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.type_converters.RegionConverter;
import picocli.CommandLine.ITypeConverter;

public class PicoRegionConverter implements ITypeConverter<Region> {

    @Override
    public Region convert(String s) throws Exception {
        return new RegionConverter().convert(s);
    }
}
