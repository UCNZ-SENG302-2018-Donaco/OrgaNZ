package seng302;

import java.time.LocalDate;
import java.util.ArrayList;

public class DonorManager {

    private ArrayList<Donor> donors;
    private int uid;

    public DonorManager() {
        donors = new ArrayList<>();
        uid = 1;
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

    public void updateDonor(Donor donor) {
        donors.remove(donor);
        donors.add(donor);
    }

    public int getUid() {
        return uid++;
    }

    public boolean collisionExists(String name, LocalDate dateOfBirth) {
        for (Donor donor : donors) {
            if (donor.getName().equals(name) && donor.getDateOfBirth().isEqual(dateOfBirth)) {
                return true;
            }
        }
        return false;
    }

    public Donor getDonorByID(int id) {
        return donors.stream()
                .filter(d -> d.getUid() == id).findFirst().orElse(null);
    }
}
