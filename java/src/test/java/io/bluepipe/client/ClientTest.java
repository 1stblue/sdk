package io.bluepipe.client;

import io.bluepipe.client.core.HttpClient;
import io.bluepipe.client.core.TransportException;
import io.bluepipe.client.model.Application;
import io.bluepipe.client.model.Connection;
import io.bluepipe.client.model.CopyTask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        final HttpClient client = new HttpClient(testAddress, testApiKey, testSecret);

        Connection config = new Connection("mysql.dev", client);
        config.setAddress("jdbc:mysql://demo-mysql:3306/mysql", 12);
        config.setTitle(" 测试啦啦 ");
        config.setUserInfo("root", "123456");
        config.setOption("key1", "value1");
        config.save();

        config = new Connection("ydb.dev", client);
        config.setAddress("postgresql://demo-postgres:5432/postgres", 20);
        config.setTitle("测试PG");
        config.setUserInfo("postgres", "123456");

        config.save();
    }

    @Test
    void shouldApplicationAPIWorksFine() throws Exception {

        final HttpClient client = new HttpClient(testAddress, testApiKey, testSecret);

        CopyTask config = CopyTask.Builder.create()
                .source("mysql.dev", "tpch", "*")
                .target("ydb.dev", "tpch_test", "v1_{table}")
                .fields("*", "{field}_new")
                .options(Context.Default())
                .build();

        Application app = new Application("demo-app", client);
        app.setTitle("测试 SDK");
        app.attach(config);

        app.start(true, true);
        app.pause(true);
    }

}