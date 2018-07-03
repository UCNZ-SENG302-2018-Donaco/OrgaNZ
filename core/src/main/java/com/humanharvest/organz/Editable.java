package com.humanharvest.organz;

import com.humanharvest.organz.utilities.type_converters.StringConverter;
import com.humanharvest.organz.utilities.type_converters.TypeConverter;
import com.humanharvest.organz.utilities.validators.AcceptAnyValidator;
import com.humanharvest.organz.utilities.validators.StringValidator;
import com.humanharvest.organz.utilities.validators.Validator;

public @interface Editable {

    Class<? extends TypeConverter> converter() default StringConverter.class;
    Class<? extends Validator> validator() default AcceptAnyValidator.class;

}
