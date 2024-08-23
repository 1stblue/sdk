package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bluepipe.client.core.HttpClient;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class NamedEntity extends Entity {

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

    protected NamedEntity() {
        super();
    }

    protected NamedEntity(String name, HttpClient client) {
        if (null != name) {
            this.id = name.replaceAll("\\s+", "");
        }
        setHttpClient(client);
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

    /**
     * Delete entity
     */
    @Deprecated
    public void delete() throws Exception {
        checkHttpClient();
        httpClient.delete(urlPath());
    }

}
