package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bluepipe.client.core.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Entity {

    private static final ObjectMapper jackson = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * 额外设定信息
     */
    @JsonProperty(value = "setting")
    @JsonAlias(value = {"properties", "options"})
    protected Map<String, Object> properties = new HashMap<>();

    /**
     * 实体 ID
     */
    @JsonProperty(value = "id")
    @JsonAlias(value = {"tns_name", "job_guid"})
    protected String id;

    /**
     * 实体描述
     */
    @JsonProperty(value = "title")
    @JsonAlias(value = {"tns_title"})
    protected String title;

    @JsonIgnore
    protected transient HttpClient httpClient;

    private Entity() {
    }

    protected Entity(HttpClient httpClient) {
        this("", httpClient);
    }

    protected Entity(String id, HttpClient client) {
        if (null != id) {
            this.id = id.replaceAll("\\s+", "");
        }
        setHttpClient(client);
    }

    protected static Entity covert(Object entity, Class<? extends Entity> clazz) {
        return jackson.convertValue(entity, clazz);
    }

    public String getID() {
        return id;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setTitle(String title) {
        if (null != title) {
            this.title = title.trim();
        }
    }

    protected String urlPath(String... action) {
        List<String> output = new ArrayList<>();
        if (null == id || id.isEmpty()) {
            return "";
        }

        output.add(HttpClient.cleanURLPath(id));
        for (String each : action) {
            output.add(HttpClient.cleanURLPath(each));
        }

        return "/" + String.join("/", output);
    }

    protected void checkHttpClient() {
        if (null == httpClient) {
            throw new RuntimeException("http client not set");
        }
    }

    public void setOptions(Map<String, Object> properties) {
        if (properties != null) {
            if (this.properties == null) {
                this.properties = new HashMap<>();
            }
            this.properties.putAll(properties);
        }
    }

    public void setOption(String key, Object value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
    }

    public abstract Entity save() throws Exception;

    /**
     * Delete entity
     */
    @Deprecated
    public void delete() throws Exception {
        checkHttpClient();
        httpClient.delete(urlPath());
    }
}
