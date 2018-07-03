package com.humanharvest.organz.actions;//package com.humanharvest.organz.actions;
//
//import java.util.ArrayList;
//
//import Client;
//import ClientManager;
//
///**
// * A reversible ClientManager client list modification Action
// */
//public class LoadAction implements Action {
//
//    private ClientManager manager;
//
//    private ArrayList<Client> oldState;
//    private ArrayList<Client> newState;
//
//    /**
//     * Create a new Action
//     * @param oldState The initial state of the Client array
//     * @param newState The new state of the Client array
//     * @param manager The ClientManager to apply changes to
//     */
//    public LoadAction(ArrayList<Client> oldState, ArrayList<Client> newState, ClientManager manager) {
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
//        return String.format("Loaded %s users from file", newState.size());
//    }
//
//    @Override
//    public String getUnexecuteText() {
//        return String.format("Reverted to the old user list with %s users", oldState.size());
//    }
//}
