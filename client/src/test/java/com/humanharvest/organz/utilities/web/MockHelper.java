package com.humanharvest.organz.utilities.web;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;

final class MockHelper {

    private MockHelper() {
    }

    /**
     * Creates a MockHttpTransport that will return the same response every time. The response will have:
     * Status: 200 OK
     * ContentType: application/json
     * Body: {jsonResponseBody}
     *
     * @param jsonResponseBody The JSON string to put in the body of every response from this HttpTransport.
     * @return A new MockHttpTransport that will always return the described response.
     */
    static MockHttpTransport makeMockHttpTransport(String jsonResponseBody) {
        return new MockHttpTransport() {
            @Override
            public LowLevelHttpRequest buildRequest(String method, String url) {
                return new MockLowLevelHttpRequest() {
                    @Override
                    public LowLevelHttpResponse execute() {
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
}
