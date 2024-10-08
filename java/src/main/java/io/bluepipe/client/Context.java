package io.bluepipe.client;

import java.util.HashMap;

public class Context extends HashMap<String, Object> {

    private static final String keyAutoCreateTable = "auto.create.table";
    private static final String keyAutoModifyTable = "auto.alter.table";

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
    public Object put(String key, Object value) {
        if (null != key) {
            super.put(key.replaceAll("\\s+", "").toLowerCase(), value);
        }

        return value;
    }

    /**
     * 写入端表不存在时自动创建
     *
     * @param enabled true or false; default true
     */
    public void autoCreateTable(boolean enabled) {
        put(keyAutoCreateTable, enabled);
    }

    /**
     * 写入端表结构不匹配时自动调整
     *
     * @param enabled true or false; default false
     */
    public void autoModifyTable(boolean enabled) {
        put(keyAutoModifyTable, enabled);
    }

    /**
     * 复制策略
     *
     * @param snapshot    复制全量
     * @param incremental 复制增量
     */
    private void scope(boolean snapshot, boolean incremental) {
        if (incremental) {
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
        put("ignore.delete.statement", ignore);
    }

}
