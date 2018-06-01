package com.humanharvest.organz.utilities.web;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.humanharvest.organz.BaseTest;
import org.junit.Test;

/**
 * Tests for the medication autocompletion handler. These tests do not connect to the web API, they instead use a
 * mock HttpTransport that will always return a given response.
 */
public class MedAutoCompleteHandlerTest extends BaseTest {

    private HttpTransport mockTransport;
    private MedAutoCompleteHandler handler;

    /**
     * Test for query string "res" that expects the results shown below.
     */
    @Test
    public void getSuggestionsTest1() {
        final String EXPECTED_RESPONSE_BODY = "{\"query\":\"res\",\"suggestions\":[\"Reserpine\",\"Resectisol\",\"Resec"
                + "tisol in plastic container\",\"Restoril\",\"Rescriptor\",\"Restasis\",\"Rescula\",\"Reserpine and hy"
                + "drochlorothiazide\",\"Reserpine, hydralazine hydrochloride and hydrochlorothiazide\",\"Reserpine, hy"
                + "drochlorothiazide, and hydralazine hydrochloride\",\"Reserpine and hydrochlorothiazide-50\",\"Reserp"
                + "ine and hydroflumethiazide\",\"Resporal\"]}";

        mockTransport = MockHelper.makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
        handler = new MedAutoCompleteHandler(mockTransport);

        List<String> expected = Arrays
                .asList("Reserpine", "Resectisol", "Resectisol in plastic container", "Restoril", "Rescriptor",
                        "Restasis", "Rescula", "Reserpine and hydrochlorothiazide",
                        "Reserpine, hydralazine hydrochloride and hydrochlorothiazide",
                        "Reserpine, hydrochlorothiazide, and hydralazine hydrochloride",
                        "Reserpine and hydrochlorothiazide-50", "Reserpine and hydroflumethiazide", "Resporal");

        List<String> actual = handler.getSuggestions("res");

        assertEquals(expected, actual);
    }

    /**
     * Test for query string "panda" that expects no results.
     */
    @Test
    public void getSuggestionsTest2() {
        final String EXPECTED_RESPONSE_BODY = "{\"query\":\"panda\",\"suggestions\":[]}";

        mockTransport = MockHelper.makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
        handler = new MedAutoCompleteHandler(mockTransport);

        List<String> expected = Collections.emptyList();

        List<String> actual = handler.getSuggestions("panda");

        assertEquals(expected, actual);
    }
}
