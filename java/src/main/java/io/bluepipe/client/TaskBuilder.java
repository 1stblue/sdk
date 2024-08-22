package io.bluepipe.client;

import io.bluepipe.client.core.HttpClient;
import io.bluepipe.client.model.CopyTask;

public class TaskBuilder {

    private static final String keyAutoCreateTable = "auto.create.table";
    private static final String keyAutoModifyTable = "auto.alter.table";
    private static final String keyTaskSandbox = "job_guid";

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

    public TaskBuilder source() {
        return this;
    }

    public TaskBuilder target() {
        return this;
    }

    /**
     * <ul>在同一个沙箱内:
     * <li>做批流融合</li>
     * <li>一对 pair 只起一条 log based replication 链路</li>
     * </ul>
     */
    public TaskBuilder sandbox(String sandbox) {
        output.setProperty(keyTaskSandbox, sandbox);
        return this;
    }

    public TaskBuilder autoCreateTable(boolean yes) {
        output.setProperty(keyAutoCreateTable, Boolean.toString(yes));
        return this;
    }

    public TaskBuilder autoModifyTable(boolean yes) {
        output.setProperty(keyAutoModifyTable, Boolean.toString(yes));
        return this;
    }

    public CopyTask build() {
        return output;
    }
}
