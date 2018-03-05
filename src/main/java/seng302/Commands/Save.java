package seng302.Commands;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.App;
import seng302.Donor;
import seng302.DonorManager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

@Command(name = "save", description = "Save donors to file", sortOptions = false)
public class Save implements Runnable {

    private DonorManager manager;

    public Save() {
        manager = App.getManager();
    }

    public Save(DonorManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        ArrayList<Donor> donors = manager.getDonors();


        if (donors.size() == 0) {
            System.out.println("No donors exist, nothing to save");
            return;
        }
        try {
            Writer writer = new FileWriter("savefile.json");
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .enableComplexMapKeySerialization()
                    .create();

            gson.toJson(donors, writer);
            System.out.println(gson.toJson(donors));
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save to file");
        }
    }
}