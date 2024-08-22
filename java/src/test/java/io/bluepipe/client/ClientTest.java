package io.bluepipe.client;

import io.bluepipe.client.core.HttpClient;
import io.bluepipe.client.core.ServiceException;
import io.bluepipe.client.core.TransportException;
import io.bluepipe.client.model.Connection;
import io.bluepipe.client.model.CopyTask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private static final String testAddress = "http://localhost/api/v1";

    private static final String testApiKey = "etl";

    private static final String testSecret = "yZz6XTYSaiyx5q2u";

    @Test
    void shouldNetworkExceptionWorksFine() throws MalformedURLException {
        final HttpClient client = new HttpClient("i.am.not.exists", testApiKey, testSecret);
        assertThrows(UnknownHostException.class, () -> client.get("/hello?a=b#c2"));
    }

    @Test
    void shouldHttpClientWorksFine() throws IOException {

        final HttpClient client = new HttpClient(testAddress, testApiKey, testSecret);
        assertDoesNotThrow(() -> {
            client.get("/connection/ping");
        });

        assertThrows(TransportException.class, () -> client.post("/i/am/404", null));
    }

    @Test
    void shouldConnectionAPIWorksFine() throws Exception {

        Client client = Client.create(testAddress, testApiKey, testSecret);

        Connection config = new Connection("jdbc:mysql://demo-mysql:3306/mysql", Connection.MYSQL, 12);

        // 强制设置 ID
        config.setId("mysql.dev");
        config.setTitle(" 测试啦啦 ");
        config.setUserInfo("root", "123456");
        config.setProperty("key1", "value1");

        //client.delete(config);
        client.save(config);

        config = client.getConnection("mysql.dev");
        assertNotNull(config);

        config = new Connection("postgresql://demo-postgres:5432/postgres",
                "", 20);
        config.setId("ydb.dev");
        config.setTitle("测试PG");
        config.setUserInfo("postgres", "123456");
        //client.delete(config);
        client.save(config);

        assertThrows(ServiceException.class, () -> client.getConnection("i.am.not.exists"));
    }

    @Test
    void shouldInstanceStatusWorksFine() throws Exception {
        Client client = Client.create(testAddress, testApiKey, testSecret);
        Instance instance = client.getInstance("b7696e0b-30fa-46e0-87cd-03275db28a65");
        assertNotNull(instance);

        instance.status();
        instance.kill("test");
    }

    @Test
    void shouldCopyTaskAPIWorksFine() throws Exception {
        /* 老版本：
         * 1. Save -> Run;
         * 2. Job ID
         */
        CopyTask config = CopyTask.Builder.create("rule.1234")
                .source("mysql.dev", "tpch", "*")
                .target("ydb.dev", "tpch_test", "v1_{table}")
                .fields("*", "{field}")
                .defaultOptions(Context.Default())
                .build();

        Client client = Client.create(testAddress, testApiKey, testSecret);
        //client.save(config);
        client.submit(config, Context.Default());
    }

}