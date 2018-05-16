package seng302.Commands.Modify;

import static seng302.Utilities.Enums.Organ.*;

import java.util.HashMap;
import java.util.Map;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.ModifyClientOrgansAction;
import seng302.Client;
import seng302.HistoryItem;
import seng302.HistoryManager;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to set the status of their organs in terms of what they would like to donate.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "setorganstatus", description = "Set the organ donation choices of an existing user.", sortOptions = false)
public class SetOrganStatus implements Runnable {

    private ClientManager manager;
    private ActionInvoker invoker;

    public SetOrganStatus() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
    }

    public SetOrganStatus(ClientManager manager, ActionInvoker invoker) {
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
        Client client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");
            return;
        }

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(client);

        Map<Organ, Boolean> states = new HashMap<>();
        states.put(LIVER, liver);
        states.put(KIDNEY, kidney);
        states.put(PANCREAS, pancreas);
        states.put(HEART, heart);
        states.put(LUNG, lung);
        states.put(INTESTINE, intestine);
        states.put(CORNEA, cornea);
        states.put(MIDDLE_EAR, middleear);
        states.put(SKIN, skin);
        states.put(BONE, bone);
        states.put(BONE_MARROW, bonemarrow);
        states.put(CONNECTIVE_TISSUE, connectivetissue);

        for (Map.Entry<Organ, Boolean> entry : states.entrySet()) {
            Organ organ = entry.getKey();
            Boolean newState = entry.getValue();
            Boolean currState = client.getOrganDonationStatus().get(organ);
            if (newState == null) {
                continue;
            } else if (newState && currState) {
                System.out.println(organ.toString() + " is already registered for donation");
            } else if (!newState && !currState) {
                System.out.println(organ.toString() + " is already not registered for donation");
            } else {
                try {
                    action.addChange(organ, newState);
                } catch (OrganAlreadyRegisteredException e) {
                    e.printStackTrace();
                }
            }
        }

        invoker.execute(action);

        HistoryItem historyItem = new HistoryItem("SET ORGAN STATUS", "The organ status was updated for " + uid);
        HistoryManager.INSTANCE.updateHistory(historyItem);
    }
}
