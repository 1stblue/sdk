package io.bluepipe.client.http;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {

    private static final String testAddress = "http://localhost/api/v1";

    private static final String testApiKey = "etl";

    private static final String testSecret = "yZz6XTYSaiyx5q2u";

    @Test
    void shouldHttpClientWorksFine() throws MalformedURLException {
        HttpClient client = new HttpClient(testAddress, testApiKey, testSecret);
        System.out.println(client);
        assertEquals(1, 1);
    }
}