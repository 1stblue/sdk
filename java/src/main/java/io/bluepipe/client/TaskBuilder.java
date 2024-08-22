package io.bluepipe.client;

import io.bluepipe.client.core.HttpClient;
import io.bluepipe.client.model.CopyTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TaskBuilder {

    /**
     * output CopyTask
     */
    private final CopyTask output;

    private TaskBuilder(String taskId) {
        output = new CopyTask();
        output.setID(HttpClient.cleanURLPath(taskId));
    }

    public static TaskBuilder create() {
        return new TaskBuilder("");
    }

    public static TaskBuilder create(String id) {
        return new TaskBuilder(id);
    }

    public TaskBuilder source(@NotNull String tnsName, @NotNull String schemaName, String tableName, Map<String, String> partition) {
        CopyTask.Bucket bucket = new CopyTask.Bucket();
        return this;
    }

    public TaskBuilder source(@NotNull String tnsName, @NotNull String schemaName, String tableName) {
        return source(tnsName, schemaName, tableName, null);
    }

    public TaskBuilder source(@NotNull String tnsName, @NotNull String schemaName) {
        return source(tnsName, schemaName, "*");
    }

    public TaskBuilder target(@NotNull String tnsName, String schemaName, String tableName, Map<String, String> partition) {
        return this;
    }

    public TaskBuilder target(@NotNull String tnsName, String schemaName, String tableName) {
        return target(tnsName, schemaName, tableName, null);
    }

    public TaskBuilder target(@NotNull String tnsName) {
        return target(tnsName, "{schema}", "{table}", null);
    }

    public TaskBuilder fields() {
        return this;
    }

    public TaskBuilder context(Context context) {
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
