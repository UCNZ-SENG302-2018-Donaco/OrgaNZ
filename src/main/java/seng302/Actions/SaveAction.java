//package seng302.Actions;
//
//import java.util.ArrayList;
//
//import seng302.Donor;
//import seng302.State.DonorManager;
//
//public class SaveAction implements Action {
//
//    private DonorManager manager;
//
//    private ArrayList<Donor> oldState;
//    private ArrayList<Donor> newState;
//
//    public SaveAction(ArrayList<Donor> oldState, ArrayList<Donor> newState, DonorManager manager) {
//        this.manager = manager;
//        this.oldState = oldState;
//        this.newState = newState;
//    }
//
//    @Override
//    public void execute() {
//        manager.setDonors(newState);
//    }
//
//    @Override
//    public void unExecute() {
//        manager.setDonors(oldState);
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
