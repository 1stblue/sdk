package io.bluepipe.client.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpClient {

    public static final String defaultServerName = "api.1stblue.com";

    private static final ObjectMapper jackson = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private static final String userAgent = "1stblue.java/" + packageVersion();

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private final HttpHost server;

    private final String prefix;

    private final HttpClientConnectionManager connManager;

    private final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(5, TimeUnit.SECONDS)
            .setConnectionKeepAlive(TimeValue.ofSeconds(10))
            .build();

    public HttpClient(String address) throws MalformedURLException {

        this.connManager = new PoolingHttpClientConnectionManager();

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

    private String signature(List<String> message) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, "")
                .hmacHex(String.join("\n", message));
    }

    private Object doRequest(HttpUriRequestBase request) throws IOException {
        int pos = request.getPath().indexOf("/");
        if (pos > 0) {
            request.setHeader("Host", request.getPath().substring(0, pos));
            request.setPath(request.getPath().substring(pos));
        } else {
            request.setHeader("Host", defaultServerName);
        }

        request.setHeader("X-Api-Key", "");
        request.setHeader("X-Api-Nonce", "");

        List<String> output = new ArrayList<>();
        for (Header header : request.getHeaders()) {
            String name = header.getName().toLowerCase();
            if (name.equals("date") || name.startsWith("x-")) {
                output.add(String.format("%s:%s", name, header.getValue().trim()));
            }
        }

        output.sort(String::compareTo);
        output.add(0, String.format("%s %s", request.getMethod(), request.getPath()));

        // TODO: payload
        request.setHeader("Authorization", String.format("APIKEY %s", signature(output)));

        return getClient().execute(server, request, new ResponseHandler());
    }

    public void post(String path, Object body) {
        //doRequest(new HttpPost(server, requestPath(path)));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ResponseHandler implements HttpClientResponseHandler<Object> {

        @JsonProperty("success")
        private Boolean success;

        @JsonProperty("message")
        private String message;

        @JsonProperty("data")
        @JsonRawValue(value = true)
        private Object data;

        @Override
        public Object handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
            if (response.getCode() / 100 != 2) {
                throw new HttpException("HttpResponse: %d", response.getCode());
            }

            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            jackson.readValue(content, this.getClass());

            return data;
        }
    }

}
