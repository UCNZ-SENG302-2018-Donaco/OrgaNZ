package seng302.Utilities.Web;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import org.junit.Test;

/**
 * Tests for the medication autocompletion handler. These tests do not connect to the web API, they instead use a
 * mock HttpTransport that will always return a given response.
 */
public class MedAutoCompleteHandlerTest {

    private HttpTransport mockTransport;
    private MedAutoCompleteHandler handler;

    /**
     * Creates a MockHttpTransport that will return the same response every time. The response will have:
     * Status: 200 OK
     * ContentType: application/json
     * Body: {jsonResponseBody}
     * @param jsonResponseBody The JSON string to put in the body of every response from this HttpTransport.
     * @return A new MockHttpTransport that will always return the described response.
     */
    private MockHttpTransport makeMockHttpTransport(String jsonResponseBody) {
        return new MockHttpTransport() {
            @Override
            public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
                return new MockLowLevelHttpRequest() {
                    @Override
                    public LowLevelHttpResponse execute() throws IOException {
                        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                        response.setStatusCode(200);
                        response.setContentType(Json.MEDIA_TYPE);
                        response.setContent(jsonResponseBody);
                        return response;
                    }
                };
            }
        };
    }

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

        mockTransport = makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
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

        mockTransport = makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
        handler = new MedAutoCompleteHandler(mockTransport);

        List<String> expected = Collections.emptyList();

        List<String> actual = handler.getSuggestions("panda");

        assertEquals(expected, actual);
    }
}
