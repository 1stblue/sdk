package io.bluepipe.client;

import io.bluepipe.client.model.CopyTask;

public class TaskBuilder {

    /**
     * output CopyTask
     */
    private final CopyTask output;

    public static TaskBuilder create() {
        return new TaskBuilder();
    }

    private TaskBuilder() {
        output = new CopyTask();
    }

    public TaskBuilder source() {
        return this;
    }

    public TaskBuilder target() {
        return this;
    }

    public TaskBuilder autoCreateTable(boolean yes) {
        return this;
    }

    public TaskBuilder autoModifyTable(boolean yes) {
        return this;
    }

    public CopyTask build() {
        return output;
    }
}
