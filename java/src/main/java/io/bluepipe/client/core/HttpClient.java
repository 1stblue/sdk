package io.bluepipe.client.core;

import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class HttpClient {

    private final HttpHost server;

    private final String prefix;

    public HttpClient(String address) {
        address = address.trim();
        if (!address.contains("://")) {
            address = "https://" + address;
        }

        try {
            URL addr = URI.create(address).toURL();
            this.server = new HttpHost(addr.getProtocol(), addr.getHost(), addr.getPort());
            this.prefix = addr.getPath().replaceAll("/+$", "");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void doRequest(HttpRequest request) {

    }

}
