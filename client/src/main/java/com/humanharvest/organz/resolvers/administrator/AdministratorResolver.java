package com.humanharvest.organz.resolvers.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.views.administrator.CreateAdministratorView;
import com.humanharvest.organz.views.administrator.ModifyAdministratorObject;

public interface AdministratorResolver {

    Administrator createAdministrator(CreateAdministratorView administratorView);

    Administrator modifyAdministrator(Administrator administrator, ModifyAdministratorObject modifyAdministratorObject);

}
