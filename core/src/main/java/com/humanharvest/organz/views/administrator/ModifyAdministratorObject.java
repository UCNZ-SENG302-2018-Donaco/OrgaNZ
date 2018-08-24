package com.humanharvest.organz.views.administrator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.humanharvest.organz.views.ModifyBaseObject;

import java.lang.reflect.Member;
import java.util.stream.Collectors;

@JsonSerialize(using = ModifyBaseObject.Serialiser.class)
public class ModifyAdministratorObject extends ModifyBaseObject {

    private String username;
    private String password;

    public String toString() {
        String changesText = getModifiedFields().stream()
                .map(ModifyAdministratorObject::fieldString)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for administrator.\n"
                        + "These changes were made: \n\n%s",
                changesText);
    }

    private static String fieldString(Member field) {
        return String.format("Updated %s", field.getName());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        registerChange("username");
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        registerChange("password");
        this.password = password;
    }
}
