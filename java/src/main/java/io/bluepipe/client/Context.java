package io.bluepipe.client;

import java.util.HashMap;

public class Context extends HashMap<String, String> {

    private static final String keyAutoCreateTable = "auto.create.table";
    private static final String keyAutoModifyTable = "auto.alter.table";
    private static final String keyTaskSandbox = "job_guid";

    public Context() {
    }

    public static Context Default() {
        Context ctx = new Context();
        ctx.autoCreateTable(true);
        ctx.autoModifyTable(false);
        ctx.ignoreDeleteStatement(false);
        return ctx;
    }

    @Override
    public String put(String key, String value) {
        if (null != key) {
            super.put(key.replaceAll("\\s+", "").toLowerCase(), value);
        }

        return value;
    }

    /**
     * <ul>在同一个沙箱内:
     * <li>做批流融合</li>
     * <li>一对 pair 只起一条 log based replication 链路</li>
     * </ul>
     */
    public void sandbox(String sandbox) {
        put(keyTaskSandbox, sandbox);
    }

    public void autoCreateTable(boolean enabled) {
        put(keyAutoCreateTable, Boolean.toString(enabled));
    }

    public void autoModifyTable(boolean enabled) {
        put(keyAutoModifyTable, Boolean.toString(enabled));
    }

    /**
     * 只增不删
     * (尚未实现的 feature)
     *
     * @param enabled ignore or not
     */
    @Deprecated
    public void ignoreDeleteStatement(boolean enabled) {

    }

    public void enableAutoRun() {

    }

}
