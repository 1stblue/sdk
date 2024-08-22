package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.bluepipe.client.core.HttpClient;
import org.jetbrains.annotations.NotNull;

public class CopyTask extends Entity {

    // rightNow
    // crontab
    // cdc
    @JsonProperty(value = "id")
    private String ruleId;

    @JsonProperty(value = "source")
    private String sourceTns;

    @JsonProperty(value = "target")
    private String targetTns;

    @Override
    public String getID() {
        return this.ruleId;
    }

    public void setID(@NotNull String id) {
        this.ruleId = HttpClient.cleanURLPath(id);
    }

    public CopyTask() {

    }

}
