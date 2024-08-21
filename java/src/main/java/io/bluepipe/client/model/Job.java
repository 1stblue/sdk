package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Job extends Entity {

    @JsonProperty(value = "id")
    private String id;

    /**
     * <ul>在同一个沙箱内:
     * <li>做批流融合</li>
     * <li>一对 pair 只起一条 log based replication 链路</li>
     * </ul>
     */
    @JsonProperty(value = "sandbox")
    @JsonAlias(value = {"namespace"})
    private String sandbox;

    @Override
    public String getID() {
        return this.id;
    }

}
