package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.bluepipe.client.core.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance extends Entity {

    public Instance() {
        this("", null);
    }

    public Instance(String id, HttpClient httpClient) {
        super(id, httpClient);
    }

    public void kill(String will) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("message", will);
        httpClient.post("/instance"+urlPath("stop"), params);
    }

    public Object status() throws IOException {
        Object result = httpClient.get("/instance"+urlPath());
        System.out.println(result);
        return null;
    }

}
