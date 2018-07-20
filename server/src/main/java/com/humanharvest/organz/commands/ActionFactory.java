package com.humanharvest.organz.commands;

import java.lang.reflect.Constructor;

import com.humanharvest.organz.actions.ActionInvoker;
import picocli.CommandLine.IFactory;

public class ActionFactory implements IFactory {

    private final ActionInvoker invoker;

    public ActionFactory(ActionInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public <K> K create(Class<K> aClass)
            throws Exception {
        Constructor<K> constructor = aClass.getConstructor(ActionInvoker.class);
        return constructor.newInstance(invoker);
    }
}
