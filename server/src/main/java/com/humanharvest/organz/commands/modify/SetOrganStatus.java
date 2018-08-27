package com.humanharvest.organz.commands.modify;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.ModifyClientOrgansAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to set the status of their organs in terms of what they would like to donate.
 */

@Command(name = "setorganstatus", description = "Set the organ donation choices of an existing client.", sortOptions =
        false)
public class SetOrganStatus implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(SetOrganStatus.class.getName());

    private final ClientManager manager;
    private final ActionInvoker invoker;
    private final PrintStream outputStream;

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

    public SetOrganStatus(PrintStream outputStream, ActionInvoker invoker) {
        this.invoker = invoker;
        this.outputStream = outputStream;
        manager = State.getClientManager();
    }

    public SetOrganStatus(ClientManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
        outputStream = System.out;
    }


    @Override
    public void run() {
        Optional<Client> client = manager.getClientByID(uid);
        if (!client.isPresent()) {
            outputStream.println("No client exists with that user ID");
            return;
        }

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(client.get(), manager);

        Map<Organ, Boolean> states = new HashMap<>();
        states.put(Organ.LIVER, liver);
        states.put(Organ.KIDNEY, kidney);
        states.put(Organ.PANCREAS, pancreas);
        states.put(Organ.HEART, heart);
        states.put(Organ.LUNG, lung);
        states.put(Organ.INTESTINE, intestine);
        states.put(Organ.CORNEA, cornea);
        states.put(Organ.MIDDLE_EAR, middleear);
        states.put(Organ.SKIN, skin);
        states.put(Organ.BONE, bone);
        states.put(Organ.BONE_MARROW, bonemarrow);
        states.put(Organ.CONNECTIVE_TISSUE, connectivetissue);

        for (Map.Entry<Organ, Boolean> entry : states.entrySet()) {
            Organ organ = entry.getKey();
            Boolean newState = entry.getValue();
            Boolean currState = client.get().getOrganDonationStatus().get(organ);
            if (newState == null) {
                continue;
            } else if (newState && currState) {
                outputStream.println(organ + " is already registered for donation");
            } else if (!newState && !currState) {
                outputStream.println(organ + " is already not registered for donation");
            } else {
                try {
                    action.addChange(organ, newState);
                    // This should not be thrown as we do checks before to ensure it is correct
                } catch (OrganAlreadyRegisteredException e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }

        outputStream.println(invoker.execute(action));
    }
}
