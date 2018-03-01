package seng302;


import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class SaveToJSON {
    @SuppressWarnings("unchecked")

    private static DonorManager donorManager;
    public static void main(String[] args) throws IOException {

        JSONObject Donor = new JSONObject();
        Donor.put("Name: ", donorManager.getDonors());

        JSONArray uidArray = new JSONArray();
        uidArray.add(donorManager.getUid());
        Donor.put("UID: ", uidArray);

        // try-with-resources statement based on post comment below :)
        try (FileWriter file = new FileWriter("/Users/<username>/Documents/file1.txt")) {
            file.write(Donor.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + Donor);
        }
    }
}
