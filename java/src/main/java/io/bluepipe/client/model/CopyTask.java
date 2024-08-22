package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bluepipe.client.Context;
import io.bluepipe.client.core.HttpClient;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CopyTask extends Entity {

    @JsonProperty(value = "id")
    private String taskId;

    @JsonProperty(value = "source")
    private Bucket source;

    @JsonProperty(value = "target")
    private Bucket target;

    @JsonProperty(value = "fields")
    private Map<String, String> fields = new HashMap<>();

    // for json deserialize
    private CopyTask() {
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

        public static Bucket of(String tnsName, String database, String schemaName, String tableName) {
            Bucket bucket = new Bucket();
            bucket.tnsName = tnsName.trim();
            if (null != database) {
                bucket.database = database.trim();
            }
            bucket.schemaName = schemaName.trim();
            bucket.tableName = tableName.trim();
            return bucket;
        }

        public String toString() {
            return String.format("%s.%s.%s@%s", database, schemaName, tableName, tnsName);
        }
    }

    /**
     * Task Builder
     */
    public static class Builder {

        /**
         * output CopyTask
         */
        private final CopyTask output;

        private Builder(String taskId) {
            output = new CopyTask();
            output.setID(HttpClient.cleanURLPath(taskId));
        }

        public static Builder create() {
            return new Builder("");
        }

        public static Builder create(String id) {
            return new Builder(id);
        }


        /**
         * Set Task Source
         *
         * @param tnsName    Connection ID
         * @param schemaName Name of schema to be copied. If set to "*", means all schemas of the database.
         * @param tableName  Name of the table to be copied. If set to "*", means all tables under the schema[s].
         */
        private Builder source(@NotNull String tnsName, @NotNull String schemaName, String tableName, Map<String, String> partition) {
            output.source = Bucket.of(tnsName, null, schemaName, tableName);
            return this;
        }

        public Builder source(@NotNull String tnsName, @NotNull String schemaName, String tableName) {
            return source(tnsName, schemaName, tableName, null);
        }

        public Builder source(@NotNull String tnsName, @NotNull String schemaName) {
            return source(tnsName, schemaName, "*");
        }

        private Builder target(@NotNull String tnsName, String schemaName, String tableName, Map<String, String> partition) {
            output.target = Bucket.of(tnsName, null, schemaName, tableName);
            return this;
        }

        public Builder target(@NotNull String tnsName, String schemaName, String tableName) {
            return target(tnsName, schemaName, tableName, null);
        }

        public Builder target(@NotNull String tnsName) {
            return target(tnsName, "{schema}", "{table}", null);
        }

        public Builder fields(@NotNull String origin) {
            return fields(origin, "{field}");
        }

        public Builder fields(@NotNull String origin, String rename) {
            if (null == rename) {
                rename = "{field}";
            }
            output.fields.put(rename, origin.trim());

            return this;
        }

        @Deprecated
        private Builder context(Context context) {
            if (null != context) {
                for (String key : context.keySet()) {
                    output.setProperty(key, context.get(key));
                }
            }

            return this;
        }

        public CopyTask build() {
            return output;
        }
    }

}
