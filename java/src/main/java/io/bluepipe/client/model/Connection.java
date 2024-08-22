package io.bluepipe.client.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bluepipe.client.core.HttpClient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Connection extends Entity {

    // TODO: 参考连接器的 manifest (应该在 server 端做映射)
    public static final String MYSQL = "mysql";
    public static final String POSTGRESQL = "postgresql";
    public static final String ORACLE = "oracle";
    public static final String SQLSERVER = "sqlserver";
    public static final String YDB = "ymatrix";
    public static final String MONGODB = "mongodb";
    public static final String KAFKA = "kafka";

    /**
     * Schema mapping
     */
    private static final Map<String, String> schemeClassMapper = new HashMap<>();

    static {
        schemeClassMapper.put(YDB, "com.firstblue.bluepipe.connector.ymatrix.YMatrixConnector");
        schemeClassMapper.put(MYSQL, "com.firstblue.bluepipe.connector.mysql.MySQLConnector");
        schemeClassMapper.put(ORACLE, "com.firstblue.bluepipe.connector.oracle.OracleConnector");
        schemeClassMapper.put(SQLSERVER, "com.firstblue.bluepipe.connector.sqlserver.SqlServerConnector");
        schemeClassMapper.put(KAFKA, "com.firstblue.bluepipe.connector.kafka.KafkaConnector");
        schemeClassMapper.put(MONGODB, "com.firstblue.bluepipe.connector.mongodb.MongoDBConnector");
        schemeClassMapper.put(POSTGRESQL, "com.firstblue.bluepipe.connector.postgresql.PostgreSQLConnector");
    }

    @JsonProperty(value = "tns_name")
    @JsonAlias(value = {"id"})
    private String id;

    @JsonProperty(value = "tns_title")
    @JsonAlias(value = {"title"})
    private String title;

    @JsonProperty(value = "connector")
    @JsonAlias(value = {"scheme"})
    private String scheme;

    @JsonProperty(value = "classname")
    @JsonAlias(value = {"class", "className"})
    private String className = "";

    @JsonProperty(value = "servers")
    private List<Server> servers;

    @JsonProperty(value = "database")
    @JsonAlias(value = {"tenant", "catalog", "namespace"})
    private String namespace;

    @JsonProperty(value = "auth_method")
    private String authMethod;

    @JsonProperty(value = "auth_config")
    private Map<String, String> authConfig = new HashMap<>();

    public Connection() {
    }

    public Connection(@NotNull String address, @NotNull String scheme) {
        this(address, scheme, 0);
    }

    public Connection(@NotNull String address, @NotNull String scheme, int poolSize) {
        setScheme(scheme);
        setAddress(address, poolSize);
    }

    private static String leftUntil(String value, String... search) {
        for (String s : search) {
            int pos = value.indexOf(s);
            if (pos > 0) {
                return value.substring(0, pos);
            }
        }

        return value;
    }

    private void setScheme(String scheme) {
        this.scheme = scheme;
        this.className = schemeClassMapper.get(scheme);
    }

    public void setId(@NotNull String tnsName) {
        this.id = HttpClient.cleanURLPath(tnsName);
    }

    @Override
    public String getID() {
        return id;
    }

    public void setTitle(@NotNull String title) {
        this.title = title.trim();
    }

    private void setNamespace(@NotNull String namespace) {
        this.namespace = namespace;
    }

    public void setAddress(@NotNull String address, int poolSize) {
        int pos = address.indexOf("://");
        if (pos > 0) {
            if (scheme.isEmpty()) {
                String scheme = address.substring(0, pos);
                setScheme(scheme.substring(1 + scheme.lastIndexOf(":")));
                // jdbc:mysql://
            }
            address = address.substring(pos + 3);
        }

        pos = address.indexOf("@");
        if (pos > 0) {
            // TODO: password
            address = address.substring(pos + 1);
        }

        this.servers = new ArrayList<>();
        for (String each : address.split(",")) {
            each = each.trim();
            if (each.isEmpty()) {
                continue;
            }

            pos = each.indexOf("/");
            if (pos > 0) {
                setNamespace(leftUntil(each.substring(pos + 1), "?", "#"));
                each = each.substring(0, pos);
            }

            Server server = new Server();
            server.poolSize = poolSize;

            String[] temp = each.split(":", 3);
            if (temp.length < 3) {
                server.address = each;
            } else {
                // ORACLE SID
                setNamespace(temp[2]);
                server.address = String.format("%s:%s", temp[0], temp[1]);
            }
            this.servers.add(server);
        }
    }

    public void setUserInfo(String username, String password) {
        this.authMethod = "password";
        this.authConfig = new HashMap<>();
        this.authConfig.put("username", username);
        this.authConfig.put("password", password);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Server {

        @JsonProperty(value = "address")
        private String address;

        @JsonProperty(value = "pool_size")
        private int poolSize;

    }
}
