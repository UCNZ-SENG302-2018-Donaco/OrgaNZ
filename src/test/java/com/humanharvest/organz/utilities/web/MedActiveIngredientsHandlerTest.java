package com.humanharvest.organz.utilities.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.humanharvest.organz.BaseTest;
import org.junit.Test;

public class MedActiveIngredientsHandlerTest extends BaseTest {
    private HttpTransport mockTransport;
    private MedActiveIngredientsHandler handler;

    @Test
    public void getActiveIngredients1() {
        MockCacheManager.Create();

        final String EXPECTED_RESPONSE_BODY = "[\"Hydralazine hydrochloride; hydrochlorothiazide; reserpine\",\"Hydroch"
                + "lorothiazide; reserpine\",\"Hydroflumethiazide; reserpine\",\"Reserpine\"]";

        mockTransport = MockHelper.makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
        handler = new MedActiveIngredientsHandler(mockTransport);

        List<String> expected = Arrays
                .asList("Hydralazine hydrochloride; hydrochlorothiazide; reserpine", "Hydrochlorothiazide; reserpine",
                        "Hydroflumethiazide; reserpine", "Reserpine");

        List<String> actual = Collections.emptyList();
        try {
            actual = handler.getActiveIngredients("reserpine");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertEquals(expected, actual);
    }
}
