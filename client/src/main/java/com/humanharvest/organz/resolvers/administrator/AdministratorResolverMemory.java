package com.humanharvest.organz.resolvers.administrator;

import java.util.Collections;
import java.util.List;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.administrator.CreateAdministratorView;
import com.humanharvest.organz.views.administrator.ModifyAdministratorObject;
import org.springframework.beans.BeanUtils;

public class AdministratorResolverMemory implements AdministratorResolver {

    public Administrator createAdministrator(CreateAdministratorView createAdministratorView) {
        Administrator administrator = new Administrator(createAdministratorView.getUsername(),
                createAdministratorView.getPassword());
        State.getAdministratorManager().addAdministrator(administrator);
        return administrator;
    }

    public Administrator modifyAdministrator(Administrator administrator,
            ModifyAdministratorObject modifyAdministratorObject) {
        BeanUtils.copyProperties(modifyAdministratorObject, administrator,
                modifyAdministratorObject.getUnmodifiedFields());
        return administrator;
    }

    @Override
    public List<HistoryItem> getHistory() {
        return Collections.emptyList();
    }
}
