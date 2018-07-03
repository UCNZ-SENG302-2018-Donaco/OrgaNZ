package com.humanharvest.organz.actions;//package com.humanharvest.organz.actions;
//
//import java.util.ArrayList;
//
//import Client;
//import ClientManager;
//
//public class SaveAction implements Action {
//
//    private ClientManager manager;
//
//    private ArrayList<Client> oldState;
//    private ArrayList<Client> newState;
//
//    public SaveAction(ArrayList<Client> oldState, ArrayList<Client> newState, ClientManager manager) {
//        this.manager = manager;
//        this.oldState = oldState;
//        this.newState = newState;
//    }
//
//    @Override
//    public void execute() {
//        manager.setClients(newState);
//    }
//
//    @Override
//    public void unExecute() {
//        manager.setClients(oldState);
//    }
//
//    @Override
//    public String getExecuteText() {
//        return String.format("Saved %s users to file", newState.size());
//    }
//
//    @Override
//    public String getUnexecuteText() {
//        return null;
//    }
//}
