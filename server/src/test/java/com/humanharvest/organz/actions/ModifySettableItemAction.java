package com.humanharvest.organz.actions;

public class ModifySettableItemAction extends Action {

    private SettableItem item;
    private String oldStringValue;
    private String newStringValue;

    public ModifySettableItemAction(SettableItem item, String newStringValue) {
        this.item = item;
        this.newStringValue = newStringValue;
        oldStringValue = item.getString();
    }

    @Override
    public void execute() {
        item.setString(newStringValue);
    }

    @Override
    public void unExecute() {
        item.setString(oldStringValue);
    }

    @Override
    public String getExecuteText() {
        return "Set String to " + newStringValue;
    }

    @Override
    public String getUnexecuteText() {
        return "Set String back to " + oldStringValue;
    }

    @Override
    public Object getModifiedObject() {
        return item;
    }
}
