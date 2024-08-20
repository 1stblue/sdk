package io.bluepipe.client.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpClientTest {

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

        assertThrows(RuntimeException.class, () -> client.post("/i/am/404", null));
    }
}