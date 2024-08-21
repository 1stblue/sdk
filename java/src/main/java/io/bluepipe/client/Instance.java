package io.bluepipe.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bluepipe.client.core.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {

    @JsonIgnore
    private transient HttpClient httpClient;

    @JsonProperty(value = "id")
    private String id;

    public Instance() {

    }

    public Instance(String id, HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private String reqPath(String command) {
        return "";
    }

    public void kill(String will) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("message", will);
        httpClient.post(String.format("/instance/%s/stop", HttpClient.cleanURLPath(id)), params);
    }

    public Object status() {

        return null;
    }

}
