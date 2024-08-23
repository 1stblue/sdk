package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bluepipe.client.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Application extends Entity {

    @JsonProperty(value = "id")
    @JsonAlias(value = {"job_guid"})
    private String id;

    @JsonProperty(value = "title")
    @JsonAlias(value = {"job_title"})
    private String title;

    private Application() {
    }

    public Application(@NotNull String id, @NotNull String title) {
        this.id = id;
        setTitle(title);
    }

    @Override
    public String getID() {
        return id;
    }

    /**
     * 设置应用标题
     *
     * @param title Title of the application
     */
    public void setTitle(String title) {
        if (null != title) {
            this.title = title.trim();
        }
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
     * Query current status of the application
     */
    public Object status() {
        throw new RuntimeException("Not implemented yet");
    }

}
