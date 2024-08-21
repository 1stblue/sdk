package io.bluepipe.client;

import io.bluepipe.client.core.HttpClient;
import io.bluepipe.client.core.TransportException;
import io.bluepipe.client.model.Connection;
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
    void shouldConnectionCRUDWorksFine() throws Exception {

        Client client = Client.create(testAddress, testApiKey, testSecret);

        Connection config = new Connection("jdbc:mysql://root@demo-mysql:3306/mysql", Connection.MYSQL);

        // 强制设置 ID
        config.setId("mysql.dev");
        config.setTitle("测试啦啦 ");
        config.setUserInfo("root", "123456");
        config.setProperty("key1", "value1");

        client.delete(config);
        client.save(config);

        config = client.loadConnection("mysql.dev");
        assertNotNull(config);

        // TODO: or throw exception?
        config = client.loadConnection("i.am.not.exists");
        assertNull(config);
    }

    @Test
    void shouldInstanceStatusWorksFine() throws Exception {
        Client client = Client.create(testAddress, testApiKey, testSecret);
        Instance instance = client.getInstance("b7696e0b-30fa-46e0-87cd-03275db28a65");
        assertNotNull(instance);

        instance.status();
    }

    @Test
    void shouldAAWorksFine() throws Exception {
        Client client = Client.create(testAddress, testApiKey, testSecret);
    }

}