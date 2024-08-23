package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.bluepipe.client.Context;
import io.bluepipe.client.Instance;
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

    @Override
    protected String urlPath(String... action) {
        return "/job" + super.urlPath(action);
    }

    @Override
    public Application save() throws Exception {
        checkHttpClient();
        Object result = httpClient.post(urlPath(), this);
        if (null == result) {
            return null;
        }

        return (Application) covert(result, Application.class);
    }

    public void config(CopyTask config) {
    }

    public void removeTask(CopyTask config) {
    }

    /**
     * Query Struct Running Logs
     *
     * @return List
     */
    public List<Object> logs() {
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
        return null;
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
