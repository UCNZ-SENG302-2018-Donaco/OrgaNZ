package seng302.Actions;

import seng302.Donor;
import seng302.DonorManager;

import java.time.LocalDate;

public class CreateUserAction implements Action {


    private Donor donor;
    private DonorManager manager;


    public CreateUserAction(String firstName, String middleNames, String lastName, LocalDate dateOfBirth, int uid, DonorManager manager) {

        donor = new Donor(firstName, middleNames, lastName, dateOfBirth, uid);

        this.manager = manager;
    }


    @Override
    public void execute() {
        manager.addDonor(donor);
    }

    @Override
    public void unExecute() {
        manager.removeDonor(donor);
    }
}
