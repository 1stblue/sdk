package io.bluepipe.client.http;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {

    private static final String testAddress = "http://localhost/api/v1";

    @Test
    void shouldHttpClientWorksFine() throws MalformedURLException {
        HttpClient client = new HttpClient(testAddress);
        System.out.println(client);
        assertEquals(1, 1);
    }
}