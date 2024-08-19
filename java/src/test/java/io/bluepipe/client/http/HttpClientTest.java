package io.bluepipe.client.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpClientTest {

    private static final String testAddress = "http://localhost/api/v1";

    private static final String testApiKey = "etl";

    private static final String testSecret = "yZz6XTYSaiyx5q2u";

    @Test
    void shouldHttpClientWorksFine() throws IOException {
        HttpClient client = new HttpClient("i.am.not.exists", testApiKey, testSecret);
        HttpClient finalClient = client;
        assertThrows(UnknownHostException.class, () -> finalClient.get("/hello?a=b#c2"));

        client = new HttpClient(testAddress, testApiKey, testSecret);
    }
}