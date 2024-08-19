package io.bluepipe.client.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    public static final String defaultServerName = "api.1stblue.com";

    private static final ObjectMapper jackson = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private static final String userAgent = "1stblue.java/" + packageVersion();

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private final HttpHost server;

    private final String prefix;

    public HttpClient(String address) throws MalformedURLException {
        address = address.trim();
        if (!address.contains("://")) {
            address = "https://" + address;
        }

        URL addr = URI.create(address).toURL();
        int port = addr.getPort();
        if (port < 1) {
            port = addr.getDefaultPort();
        }

        this.server = new HttpHost(addr.getProtocol(), addr.getHost(), port);
        this.prefix = addr.getPath().replaceAll("/+$", "");
    }

    private static String packageVersion() {
        String version = HttpClient.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "dev";
        }

        return version;
    }

    private static HttpUriRequestBase requestWithBody(HttpUriRequestBase req, Object content) throws JsonProcessingException {
        if (content != null) {
            if (content instanceof String) {
                req.setHeader("Content-Type", "text/plain");
                req.setEntity(new StringEntity((String) content, StandardCharsets.UTF_8));
            } else {
                req.setHeader("Content-Type", "application/json");
                req.setEntity(new StringEntity(jackson.writeValueAsString(content), StandardCharsets.UTF_8));
            }
        }

        return req;
    }

    private String requestPath(String path) {
        return prefix + path;
    }

    private void doRequest(HttpUriRequestBase request) {

        int pos = request.getPath().indexOf("/");
        if (pos > 0) {
            request.setHeader("Host", request.getPath().substring(0, pos));
            request.setPath(request.getPath().substring(pos));
        } else {
            request.setHeader("Host", defaultServerName);
        }

        // TODO: auth
        request.setHeader("User-Agent", "Bluepipe Client");
        request.setHeader("Accept", "application/json");

    }

    public void post(String path, Object body) {
        //doRequest(new HttpPost(server, requestPath(path)));

    }

}
