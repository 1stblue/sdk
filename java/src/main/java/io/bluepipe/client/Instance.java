package io.bluepipe.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bluepipe.client.core.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {

    @JsonIgnore
    private transient HttpClient httpClient;

    @JsonProperty(value = "id")
    private String id;

    public Instance() {
        this("", null);
    }

    public Instance(String id, HttpClient httpClient) {
        this.id = id;
        this.httpClient = httpClient;
    }

    private String actionPath(String command) {
        List<String> paths = new ArrayList<>();
        paths.add("instance");
        paths.add(HttpClient.cleanURLPath(id));
        if (null != command && !command.isEmpty()) {
            paths.add(command);
        }

        return "/" + String.join("/", paths);
    }

    public void kill(String will) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("message", will);
        httpClient.post(actionPath("stop"), params);
    }

    public Object status() throws IOException {
        Object result = httpClient.get(actionPath(null));
        System.out.println(result);
        return null;
    }

}
