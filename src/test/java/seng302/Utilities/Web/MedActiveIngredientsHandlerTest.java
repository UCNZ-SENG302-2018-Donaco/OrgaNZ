package seng302.Utilities.Web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

public class MedActiveIngredientsHandlerTest {

    private HttpTransport mockTransport;
    private MedActiveIngredientsHandler handler;

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

    @Test
    public void getActiveIngredients1() {
        final String EXPECTED_RESPONSE_BODY = "[\"Hydralazine hydrochloride; hydrochlorothiazide; reserpine\",\"Hydroch"
                + "lorothiazide; reserpine\",\"Hydroflumethiazide; reserpine\",\"Reserpine\"]";

        mockTransport = makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
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
