package seng302.commands;

import seng302.DonorManager;
import seng302.Donor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seng302.App.getManager;

public class CommandHandler {

    private DonorManager donorManager;

    public CommandHandler (DonorManager donorManager) {
        this.donorManager = donorManager;
    }

    public void parseCommand(String input) {
        ArrayList<String> inputs = new ArrayList<>();

        //Regex matcher that separates on space but allows for double quoted strings to be considered single strings
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
        while (m.find())
            inputs.add(m.group(1).replace("\"", ""));
        if (inputs.size() == 0) {
            return;
        }

        String command = inputs.get(0);
        inputs.remove(0);

        switch (command) {
            case "createuser":
                createuser(inputs);
                break;
            case "help":
                help(inputs);
                break;
            case "printall":
                printAllDonorInfo();
                break;
            case "setattribute":
                setAttribute(inputs);
                break;
            case "printuser":
                printUser(inputs);
                break;
            default:
                System.out.println("Command not found");
        }
    }


    public void createuser(ArrayList<String> inputs) {

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

        if (donorManager.collisionExists(name, date)) {
            System.out.println("A user already exists with that Name and DOB, would you like to proceed? (y/n)");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.next();
            if (!response.equals("y")) {
                return;
            }

        }

        int uid = donorManager.getUid();

        Donor donor = new Donor(name, date, uid);

        donorManager.addDonor(donor);
    }

    public void help(ArrayList<String> inputs) {

        System.out.println("help");
        System.out.println(inputs.size());
        System.out.println(donorManager.getDonors().size());

    }

    public void printAllDonorInfo() {

        ArrayList<Donor> donors = donorManager.getDonors();

        if (donors.size() == 0) {
            System.out.println("No donors exist");
        } else {
            for (Donor donor : donors) {
                System.out.println(donor.getDonorInfoString());
            }
        }
    }

    public void printUser(ArrayList<String> inputs) {
        System.out.println("printuser");

        if (inputs.size() != 1) {
            System.out.println("Invalid input expects form \"printuser {uid}\"");
            return;
        }

        String user = inputs.get(0);

        int uid;

        try {
            uid = Integer.parseInt(user);
        } catch (NumberFormatException e) {
            System.out.println("Invalid user ID, please enter a number");
            return;
        }
        Donor donor = donorManager.getDonorByID(uid);
        if (donor == null) {
            System.out.println("No donor exists with that user ID");
            return;
        }
        System.out.println(donor.getDonorInfoString());
    }

    public void setAttribute(ArrayList<String> inputs) {
        System.out.println("setattr");

        if (inputs.size() != 3) {
            System.out.println("Invalid input expects form \"setattribute {uid} {attribute} {value}\"");
            return;
        }

        String user = inputs.get(0);
        String attribute = inputs.get(1);
        String value = inputs.get(2);

        int uid = Integer.parseInt(user);

        Donor donor = donorManager.getDonorByID(uid);

        if (donor == null) {
            System.out.println("No donor exists with that user ID");
            return;
        }

        try {

            switch (attribute) {
                case "name":
                case "bloodType":
                case "currentAddress":
                case "region":
                    donor.setStringField(attribute, value);
                    break;
                case "height":
                case "weight":
                    donor.setIntField(attribute, Integer.parseInt(value));
                    break;
                case "dateOfBirth":
                case "dateOfDeath":
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate date;
                    date = LocalDate.parse(value, formatter);

                    donor.setDateField(attribute, date);
                    break;
                default:
                    System.out.println("Invalid attribute");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("Error");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format");
        }


        /*
        if (manager.collisionExists(name, date)) {
            System.out.println("A user already exists with that Name and DOB, would you like to proceed? (y/n)");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.next();
            if (!response.equals("y")) {
                return;
            }

        }
        */
    }
}
