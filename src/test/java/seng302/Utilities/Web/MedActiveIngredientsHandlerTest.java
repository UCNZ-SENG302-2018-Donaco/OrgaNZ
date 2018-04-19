package seng302.Utilities.Web;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import org.junit.Test;

public class MedActiveIngredientsHandlerTest {

    private HttpTransport mockTransport;
    private MedActiveIngredientsHandler handler;

    @Test
    public void getActiveIngredients1() {
        final String EXPECTED_RESPONSE_BODY = "[\"Hydralazine hydrochloride; hydrochlorothiazide; reserpine\",\"Hydroch"
                + "lorothiazide; reserpine\",\"Hydroflumethiazide; reserpine\",\"Reserpine\"]";

        mockTransport = MockHelper.makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
        handler = new MedActiveIngredientsHandler(mockTransport);

        List<String> expected = Arrays
                .asList("Hydralazine hydrochloride; hydrochlorothiazide; reserpine", "Hydrochlorothiazide; reserpine",
                        "Hydroflumethiazide; reserpine", "Reserpine");

        List<String> actual = handler.getActiveIngredients("reserpine");

        assertEquals(expected, actual);
    }
}
