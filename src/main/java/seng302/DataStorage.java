package seng302;

import java.util.ArrayList;

public class DataStorage {

    private ArrayList<Donor> donors;

    public DataStorage () {
        donors = new ArrayList<>();
    }

    public void addDonor(Donor donor) {
        donors.add(donor);
    }

    public ArrayList<Donor> getDonors() {
        return donors;
    }

    public void removeDonor(Donor donor) {
        donors.remove(donor);
    }

}
