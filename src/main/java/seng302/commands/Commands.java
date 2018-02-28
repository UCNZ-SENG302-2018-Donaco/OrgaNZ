package seng302.commands;

import seng302.DonorManager;
import seng302.Donor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import static seng302.App.getManager;

public class Commands {

    public static void createuser(ArrayList<String> inputs) {
        System.out.println("createuser");
        System.out.println(inputs.size());
        DonorManager manager = getManager();

        if (inputs.size() != 2) {
            System.out.println("Invalid input expects form \"createuser {name} {dd/mm/yyyy}\"");
            return;
        }

        String name = inputs.get(0);
        String dateStr = inputs.get(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date;

        try {
            date = LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid input expects form \"createuser {name} {dd/mm/yyyy}\"");
            return;
        }

        if (manager.collisionExists(name, date)) {
            System.out.println("A user already exists with that Name and DOB, would you like to proceed? (y/n)");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.next();
            if (!response.equals("y")) {
                return;
            }

        }

        int uid = manager.getUid();

        Donor donor = new Donor(name, date, uid);

        manager.addDonor(donor);
    }

    public static void help(ArrayList<String> inputs) {

        DonorManager manager = getManager();

        System.out.println("help");
        System.out.println(inputs.size());
        System.out.println(manager.getDonors().size());

    }
}
