package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.bluepipe.client.Context;
import io.bluepipe.client.core.HttpClient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Application extends Entity {

    private Application() {
        super(null);
    }

    public Application(@NotNull String name, @NotNull HttpClient client) {
        super(name, client);
        setOptions(Context.Default());
    }

    /**
     * Attach CopyTask to current application
     * 在同一个沙箱内:
     * <li>做批流融合</li>
     * <li>一对 pair 只起一条 log based replication 链路</li>
     *
     * @param config CopyTask configuration
     * @since >= 2.0
     */
    public void attach(@NotNull CopyTask config) throws Exception {
        checkHttpClient();
        if (null != id && !id.isEmpty()) {
            Object result = httpClient.post("/job" + urlPath(), this);
            if (result instanceof String) {
                this.id = (String) result;
            }
        }

        httpClient.post("/next/task" + urlPath(), config);
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
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Start to schedule the application
     *
     * @param snapshot    copy or not
     * @param incremental or not
     */
    public void start(boolean snapshot, boolean incremental) {
    }

    /**
     * Stop to schedule the application
     */
    public void pause() {
    }

    /**
     * Query current status of the application
     */
    public Object status() {
        throw new RuntimeException("Not implemented yet");
    }

}
