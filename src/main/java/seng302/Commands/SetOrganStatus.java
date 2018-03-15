package seng302.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Actions.ActionInvoker;
import seng302.Actions.ModifyDonorOrgansAction;
import seng302.HistoryItem;
import seng302.App;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.*;

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
    private ActionInvoker invoker;

    public SetOrganStatus() {
        manager = App.getManager();
        invoker = App.getInvoker();
    }

    public SetOrganStatus(DonorManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
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

        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(donor);

        if (liver != null) {
            action.addChange(LIVER, donor.getOrganStatus().get(LIVER), liver);
        }
        if (kidney != null) {
            action.addChange(KIDNEY, donor.getOrganStatus().get(KIDNEY), kidney);
        }
        if (pancreas != null) {
            action.addChange(PANCREAS, donor.getOrganStatus().get(PANCREAS), pancreas);
        }
        if (heart != null) {
            action.addChange(HEART, donor.getOrganStatus().get(HEART), heart);
        }
        if (lung != null) {
            action.addChange(LUNG, donor.getOrganStatus().get(LUNG), lung);
        }
        if (intestine != null) {
            action.addChange(INTESTINE, donor.getOrganStatus().get(INTESTINE), intestine);
        }
        if (cornea != null) {
            action.addChange(CORNEA, donor.getOrganStatus().get(CORNEA), cornea);
        }
        if (middleear != null) {
            action.addChange(MIDDLE_EAR, donor.getOrganStatus().get(MIDDLE_EAR), middleear);
        }
        if (skin != null) {
            action.addChange(SKIN, donor.getOrganStatus().get(SKIN), skin);
        }
        if (bone != null) {
            action.addChange(BONE, donor.getOrganStatus().get(BONE), bone);
        }
        if (bonemarrow != null) {
            action.addChange(BONE_MARROW, donor.getOrganStatus().get(BONE_MARROW), bonemarrow);
        }
        if (connectivetissue != null) {
            action.addChange(CONNECTIVE_TISSUE, donor.getOrganStatus().get(CONNECTIVE_TISSUE), connectivetissue);
        }

        invoker.execute(action);

        HistoryItem setOrganStatus = new HistoryItem("SET ORGAN STATUS", "The organ status was updated for " + uid);
        JSONConverter.updateHistory(setOrganStatus, "action_history.json");
    }
}
