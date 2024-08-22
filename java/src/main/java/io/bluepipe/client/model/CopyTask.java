package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bluepipe.client.core.HttpClient;
import org.jetbrains.annotations.NotNull;

public class CopyTask extends Entity {

    // rightNow
    // crontab
    // cdc
    @JsonProperty(value = "id")
    private String taskId;
    @JsonProperty(value = "source")
    private Bucket source;
    @JsonProperty(value = "target")
    private Bucket target;

    public CopyTask() {

    }

    @Override
    public String getID() {
        return this.taskId;
    }

    public void setID(@NotNull String id) {
        this.taskId = HttpClient.cleanURLPath(id);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bucket {

        @JsonProperty(value = "tns_name")
        private String tnsName;

        @JsonProperty(value = "database")
        @JsonAlias(value = {"tenant", "catalog"})
        private String database;

        @JsonProperty(value = "schema")
        @JsonAlias(value = {"namespace", "prefix"})
        private String schemaName;

        @JsonProperty(value = "table")
        @JsonAlias(value = {"topic"})
        private String tableName;

        public static Bucket parse(String value) {
            return null;
        }

        public String toString() {
            return "";
        }
    }

}
