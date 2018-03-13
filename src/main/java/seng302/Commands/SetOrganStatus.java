package seng302.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Action;
import seng302.App;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.*;

import java.time.LocalDate;

import static seng302.Utilities.Organ.*;

/**
 * Command line to set the status of their organs in terms of what they would like to donate.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 05/03/2018
 */

@Command(name = "setorganstatus", description = "Set the organ donation choices of an existing user.", sortOptions = false)
public class SetOrganStatus implements Runnable {

    private DonorManager manager;

    public SetOrganStatus() {
        manager = App.getManager();
    }

    public SetOrganStatus(DonorManager manager) {
        this.manager = manager;
    }

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    @Option(names = "--liver", description = "Liver status")
    private Boolean liver;

    @Option(names = "--kidney", description = "Kidney status")
    private Boolean kidney;

    @Option(names = "--pancreas", description = "Pancreas status")
    private Boolean pancreas;

    @Option(names = "--heart", description = "Heart status")
    private Boolean heart;

    @Option(names = "--lung", description = "Lung status")
    private Boolean lung;

    @Option(names = "--intestine", description = "Intestine status")
    private Boolean intestine;

    @Option(names = "--cornea", description = "Cornea status")
    private Boolean cornea;

    @Option(names = {"--middleear", "--middle-ear"}, description = "Middle ear status")
    private Boolean middleear;

    @Option(names = "--skin", description = "Skin status")
    private Boolean skin;

    @Option(names = "--bone", description = "Bone status")
    private Boolean bone;

    @Option(names = {"--bonemarrow", "--bone-marrow"}, description = "Bone marrow status")
    private Boolean bonemarrow;

    @Option(names = {"--connectivetissue", "--connective-tissue"}, description = "Connective tissue status")
    private Boolean connectivetissue;


    @Override
    public void run() {
        Donor donor = manager.getDonorByID(uid);
        if (donor == null) {
            System.out.println("No donor exists with that user ID");
            return;
        }
        if (liver != null) {
            try {
                donor.setOrganStatus(LIVER, liver);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Liver is already registered for donation");
            }
        }
        if (kidney != null) {
            try {
                donor.setOrganStatus(KIDNEY, kidney);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Kidney is already registered for donation");
            }
        }
        if (pancreas != null) {
            try {
                donor.setOrganStatus(PANCREAS, pancreas);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Pancreas is already registered for donation");
            }
        }
        if (heart != null) {
            try {
                donor.setOrganStatus(HEART, heart);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Heart is already registered for donation");
            }
        }
        if (lung != null) {
            try {
                donor.setOrganStatus(LUNG, lung);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Lung is already registered for donation");
            }
        }
        if (intestine != null) {
            try {
                donor.setOrganStatus(INTESTINE, intestine);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Intestine is already registered for donation");
            }
        }
        if (cornea != null) {
            try {
                donor.setOrganStatus(CORNEA, cornea);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Cornea is already registered for donation");
            }
        }
        if (middleear != null) {
            try {
                donor.setOrganStatus(MIDDLE_EAR, middleear);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Middle ear is already registered for donation");
            }
        }
        if (skin != null) {
            try {
                donor.setOrganStatus(SKIN, skin);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Skin is already registered for donation");
            }
        }
        if (bone != null) {
            try {
                donor.setOrganStatus(BONE, bone);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Bone is already registered for donation");
            }
        }
        if (bonemarrow != null) {
            try {
                donor.setOrganStatus(BONE_MARROW, bonemarrow);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Bone marrow is already registered for donation");
            }
        }
        if (connectivetissue != null) {
            try {
                donor.setOrganStatus(CONNECTIVE_TISSUE, connectivetissue);
            } catch (OrganAlreadyRegisteredException e) {
                System.out.println("Connective tissue is already registered for donation");
            }
        }

        manager.updateDonor(donor);
        Action setOrganStatus = new Action("SET ORGAN STATUS", "The organ status was updated for " + uid);
        JSONConverter.updateActionHistory(setOrganStatus, "action_history.json");
    }
}
