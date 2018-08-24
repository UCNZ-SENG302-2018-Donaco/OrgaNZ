package com.humanharvest.organz.commands;

import com.humanharvest.organz.actions.ActionInvoker;
import picocli.CommandLine.IFactory;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Inject a PrintStream and ActionInvoker (if required) into PicoCLI Commands
 */
public class CommandFactory implements IFactory {

    private final ActionInvoker invoker;
    private final PrintStream outputStream;

    /**
     * Create a new injector
     *
     * @param outputStream The output stream to print all result text to
     * @param invoker      The ActionInvoker to execute any changes to if the command makes changes
     */
    public CommandFactory(PrintStream outputStream, ActionInvoker invoker) {
        this.invoker = invoker;
        this.outputStream = outputStream;
    }

    /**
     * Try to inject the PrintStream and ActionInvoker into the class.
     *
     * @param aClass The class to inject into
     * @param <K>    Any class
     * @return Returns a created and injected instance of the class
     * @throws IllegalAccessException    if this Constructor object is enforcing Java language access control and the
     *                                   underlying constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor throws an exception.
     * @throws InstantiationException    if the class that declares the underlying constructor represents an abstract
     *                                   class.
     * @throws NoSuchMethodException     if the given class does not have a constructor with either (PrintStream,
     *                                   ActionInvoker) or (PrintStream) or ().
     *                                   Any command being injected must implement one of these constructors.
     */
    @Override
    public <K> K create(Class<K> aClass)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        try {
            Constructor<K> constructor = aClass.getConstructor(PrintStream.class, ActionInvoker.class);
            return constructor.newInstance(outputStream, invoker);
        } catch (NoSuchMethodException e) {
            try {
                Constructor<K> constructor = aClass.getConstructor(PrintStream.class);
                return constructor.newInstance(outputStream);
            } catch (NoSuchMethodException e2) {
                try {
                    Constructor<K> constructor = aClass.getConstructor();
                    return constructor.newInstance();
                } catch (NoSuchMethodException e3) {
                    Constructor<K> constructor = aClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    return constructor.newInstance();
                }
            }
        }
    }
}
