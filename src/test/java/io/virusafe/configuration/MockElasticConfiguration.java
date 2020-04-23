package io.virusafe.configuration;

import org.json.JSONObject;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class MockElasticConfiguration {
    private static final String ELASTIC_VERSION = "2.4";
    private static final String ELASTIC_CLUSTER_NAME = "mock";
    private static final String[] INDEXES = new String[]{"location", "proximity", "questionnaire"};
    private static final String SEPARATOR = "/";

    public void setup(final MockServerClient client) {

        for (String index : INDEXES) {
            // Check if index exists
            client.when(HttpRequest.request().withPath(SEPARATOR + index).withMethod(HttpMethod.HEAD.name()))
                    .respond(HttpResponse.response().withHeader("Content-Type", "application/json")
                            .withHeader("Content-Length", "195").withHeader("Chunked", "false"));
            // Check if index exists
            client.when(HttpRequest.request().withPath(SEPARATOR + index + "/_mapping/" + index).withMethod("PUT"))
                    .respond(toHttpResponse(new JSONObject(map("acknowledged", true, "shards_acknowledged", true))));
        }

        // Default
        client.when(HttpRequest.request()).respond(toHttpResponse(
                new JSONObject(map("name", "mock", "cluster_name", ELASTIC_CLUSTER_NAME, "version",
                        map("number", ELASTIC_VERSION)))));

    }

    public void assertIndexCreated(final MockServerClient client, final String index) throws AssertionError {
        client.verify(HttpRequest.request().withMethod("PUT").withPath("/" + index), VerificationTimes.once());
    }

    private HttpResponse toHttpResponse(final JSONObject data) {
        return HttpResponse.response(data.toString()).withHeader("Content-Type", "application/json");
    }

    //TODO change with Java 11 Map.of
    private <K, V> Map<K, V> map(Object... args) {
        Map<K, V> res = new HashMap<>();
        K key = null;
        for (Object arg : args) {
            if (key == null) {
                key = (K) arg;
            } else {
                res.put(key, (V) arg);
                key = null;
            }
        }
        return res;
    }
}
