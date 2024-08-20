package io.bluepipe.client.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpClient {

    public static final String defaultServerName = "api.1stblue.cloud";

    private static final ObjectMapper jackson = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private static final String userAgent = "1stblue.java/" + packageVersion();

    private static final HttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(5, TimeUnit.SECONDS)
            .setConnectionKeepAlive(TimeValue.ofSeconds(10))
            .build();

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private final HttpHost server;

    private final String prefix;

    private final String apiKey;

    private final HmacUtils secret;

    public HttpClient(String address, String apiKey, String secret) throws MalformedURLException {
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
        this.apiKey = apiKey.trim();
        this.secret = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secret.trim());
    }

    private static String packageVersion() {
        String version = HttpClient.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "dev";
        }

        return version;
    }

    private static String createNonce() {
        return Long.toHexString(secureRandom.nextLong());
    }

    private CloseableHttpClient getClient() {
        return HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .setUserAgent(userAgent)
                .build();
    }

    private String requestPath(String path) {
        return prefix + path;
    }

    private Object doRequest(HttpUriRequestBase request, Object rawData) throws IOException {
        int pos = request.getPath().indexOf("/");
        if (pos > 0) {
            request.setHeader("Host", request.getPath().substring(0, pos));
            request.setPath(request.getPath().substring(pos));
        } else {
            request.setHeader("Host", defaultServerName);
        }

        request.setHeader("Date", DateUtils.formatStandardDate(Instant.now()));
        request.setHeader("X-Api-Key", apiKey);
        request.setHeader("X-Api-Nonce", createNonce());

        List<String> output = new ArrayList<>();
        for (Header header : request.getHeaders()) {
            String name = header.getName().toLowerCase();
            if (name.equals("date") || name.startsWith("x-api-")) {
                output.add(String.format("%s:%s", name, header.getValue().trim()));
            }
        }

        output.sort(String::compareTo);
        output.add(0, String.format("%s %s", request.getMethod(), request.getPath()));

        if (rawData != null) {
            String content = jackson.writeValueAsString(rawData);
            request.setEntity(new StringEntity(content, StandardCharsets.UTF_8));
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Content-Length", content.length());

            output.add("");
            output.add(content);
        }

        request.setHeader("Authorization", String.format("APIKEY %s",
                secret.hmacHex(String.join("\n", output))));

        return getClient().execute(server, request, new ResponseHandler());
    }

    public Object get(String path) throws IOException {
        return doRequest(new HttpGet(requestPath(path)), null);
    }

    public Object post(String path, Object data) throws IOException {
        return doRequest(new HttpPost(requestPath(path)), data);
    }

    public Object delete(String path) throws IOException {
        return doRequest(new HttpDelete(requestPath(path)), null);
    }

    public Object put(String path, Object data) throws IOException {
        return doRequest(new HttpPut(requestPath(path)), data);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ApiResponse {

        @JsonProperty("success")
        private Boolean success;

        @JsonProperty(value = "code")
        private int code;

        @JsonProperty("message")
        private String message;

        @JsonProperty("data")
        @JsonRawValue(value = true)
        private Object data;

    }

    static class ResponseHandler implements HttpClientResponseHandler<Object> {

        @Override
        public Object handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
            if (response.getCode() / 100 != 2) {
                throw new HttpException("HttpException: %d", response.getCode());
            }

            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            ApiResponse result = jackson.readValue(content, ApiResponse.class);
            if (result.success) {
                return result.data;
            }

            throw new RuntimeException(String.format("ResponseError: %s[%d]", result.message, result.code));
        }
    }

}
