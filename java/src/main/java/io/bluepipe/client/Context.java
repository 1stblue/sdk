package io.bluepipe.client;

import java.util.HashMap;

public class Context extends HashMap<String, String> {

    private static final String keyAutoCreateTable = "auto.create.table";
    private static final String keyAutoModifyTable = "auto.alter.table";
    private static final String keyTaskSandbox = "job_guid";

    public Context(boolean snapshot, boolean incremental) {
        this.scope(snapshot, incremental);
    }

    public static Context Default() {
        Context ctx = new Context(true, true);
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
     * 在同一个沙箱内:
     * <li>做批流融合</li>
     * <li>一对 pair 只起一条 log based replication 链路</li>
     *
     * @param sandbox ID of the sandbox
     * @param title   Title of the sandbox
     * @since >= 2.0
     */
    public void sandbox(String sandbox, String title) {
        put(keyTaskSandbox, sandbox);
    }

    /**
     * 写入端表不存在时自动创建
     *
     * @param enabled true or false; default true
     */
    public void autoCreateTable(boolean enabled) {
        put(keyAutoCreateTable, Boolean.toString(enabled));
    }

    /**
     * 写入端表结构不匹配时自动调整
     *
     * @param enabled true or false; default false
     */
    public void autoModifyTable(boolean enabled) {
        put(keyAutoModifyTable, Boolean.toString(enabled));
    }

    /**
     * 复制策略
     *
     * @param snapshot    复制全量
     * @param incremental 复制增量
     */
    public void scope(boolean snapshot, boolean incremental) {
        if (incremental) {
            // TODO:
            put("auto_method", "CDC");
        }
    }

    /**
     * 自动校准数据（尚未实现）
     *
     * @param crontab   Unix Like Crontab Expression
     * @param diff      Diff and patch
     * @param fieldName Name of field used as cursor
     * @since >= 2.0
     */
    public void autoCalibrate(String crontab, boolean diff, String fieldName) {
        if (null != crontab) {
            crontab = crontab.trim();
        }

        put("auto.calibrate.cron", crontab);
    }

    /**
     * 只增不删（尚未实现）。设置为 true 时：
     * <li>跑批开始时，**不会**对写入端做truncate操作（purge = false）；</li>
     * <li>增量复制过程中，对 DELETE 语句直接丢弃；</li>
     * <li>无论何种情况，由于主键冲突导致的报错，框架都会删除旧的记录，再写入新记录。</li>
     *
     * @param ignore ignore or not
     * @since >= 2.0
     */
    public void ignoreDeleteStatement(boolean ignore) {
        put("ignore.delete.statement", Boolean.toString(ignore));
    }

}
