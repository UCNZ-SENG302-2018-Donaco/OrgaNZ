package com.humanharvest.organz.controller.clinician;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.views.ModifyBaseObject;

public class ViewBaseController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(ViewBaseController.class.getName());

    protected static <T> void addChangeIfDifferent(
            ModifyBaseObject modifyObject,
            T viewedObject,
            String fieldString,
            Object newValue) {
        try {
            Field field = modifyObject.getClass().getDeclaredField(fieldString);
            Field clinicianField = viewedObject.getClass().getDeclaredField(fieldString);
            field.setAccessible(true);
            clinicianField.setAccessible(true);
            if (!Objects.equals(clinicianField.get(viewedObject), newValue)) {
                field.set(modifyObject, newValue);
                modifyObject.registerChange(fieldString);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
