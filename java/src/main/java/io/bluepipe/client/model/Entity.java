package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bluepipe.client.core.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Entity {

    protected static final ObjectMapper jackson = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * 额外设定信息
     */
    @JsonProperty(value = "setting")
    @JsonAlias(value = {"properties", "options"})
    protected Map<String, Object> properties = new HashMap<>();

    protected Entity() {
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
}
