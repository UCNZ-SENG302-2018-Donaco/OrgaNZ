package seng302.Commands;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.App;
import seng302.Donor;
import seng302.DonorManager;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Command line to load the information of all the donors from a JSON file,
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 05/03/2018
 */

@Command(name = "load", description = "Load donors from file", sortOptions = false)
public class Load implements Runnable {

    private DonorManager manager;

    public Load() {
        manager = App.getManager();
    }

    public Load(DonorManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        try (Reader reader = new FileReader("savefile.json")) {
            Gson gson = new Gson();
            ArrayList<Donor> donors;
            Type collectionType = new TypeToken<ArrayList<Donor>>(){}.getType();
            donors = gson.fromJson(reader, collectionType);
            manager.setDonors(donors);
            System.out.println(donors);
            reader.close();
        } catch (IOException e) {
            System.out.println("Could not save to file");
        }
    }
}