package seng302.Utilities.Web;

import java.util.HashMap;
import java.util.List;

import seng302.Donor;

import com.google.api.client.util.Key;

public class DrugInteractionsResponse {
    @Key(value = "co_existing_conditions")
    private HashMap<String, Integer> coexistingConditions;

    @Key(value = "reports")
    private HashMap<String, Integer> reports;

    @Key(value = "age_interaction")
    private HashMap<String, String[]> ageInteraction;

    @Key(value = "duration_interaction")
    private HashMap<String, String[]> durationInteraction;

    @Key(value = "gender_interaction")
    private HashMap<String, String[]> genderInteraction;

    public List<String> calculateDonorInteractions(Donor donor) {
        throw new UnsupportedOperationException();
    }
}
