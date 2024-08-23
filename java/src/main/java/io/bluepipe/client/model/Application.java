package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.bluepipe.client.Context;
import io.bluepipe.client.core.HttpClient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Application extends NamedEntity {

    private Application() {
        super();
    }

    public Application(@NotNull String name, @NotNull HttpClient client) {
        super(name, client);
        setOptions(Context.Default());
    }

    private void save() throws Exception {
        Object result = httpClient.post("/job" + urlPath(), this);
        if (result instanceof String) {
            this.id = (String) result;
        }
    }

    /**
     * Attach CopyTask to current application.
     * 在同一个应用内:
     * <li>做批流融合</li>
     * <li>一对 pair 只起一条 log based replication 链路</li>
     *
     * @param config CopyTask configuration
     */
    public void attach(@NotNull CopyTask config) throws Exception {
        checkHttpClient();
        Object result = httpClient.post("/next/task" + urlPath(), config);
        if (null != result) {
            Application app = jackson.convertValue(result, Application.class);
            if (null == id || id.isEmpty()) {
                id = app.id;
            }
        }
        save();
    }

    /**
     * Remote CopyTask from current application
     *
     * @param config CopyTask configuration
     */
    public void detach(CopyTask config) throws Exception {
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Query Audit Logs
     *
     * @return List
     */
    public List<Object> auditLogs() throws Exception {
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * 单表数据校准（基于当前 snapshot 重刷数据）
     *
     * @param table ... Names of target table to be calibrated
     * @return List of instance
     */
    @Deprecated
    public List<Instance> calibrate(String... table) {
        // /job/.../start
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Start to schedule the application
     *
     * @param snapshot    copy or not
     * @param incremental or not
     */
    public void start(boolean snapshot, boolean incremental) throws Exception {
        checkHttpClient();
        if (incremental) {
            setOption("auto_method", "CDC");
        }
        httpClient.post("/job" + urlPath("automatic", "enable"), null);
    }

    /**
     * Pause to schedule the application
     *
     * @param killRunning Kill all running instances
     */
    public void pause(boolean killRunning) throws Exception {
        checkHttpClient();
        httpClient.post("/job" + urlPath("automatic", "disable"), null);
    }

    /**
     * Query current status of the application
     */
    public Object status() {
        throw new RuntimeException("Not implemented yet");
    }

}
