package io.bluepipe.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bluepipe.client.core.HttpClient;
import io.bluepipe.client.core.ServiceException;
import io.bluepipe.client.core.TransportException;
import io.bluepipe.client.model.Connection;
import io.bluepipe.client.model.Entity;
import io.bluepipe.client.model.Job;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public interface Client {

    static Client create(String address, String apiKey, String secret) throws MalformedURLException {
        return new V1(address, apiKey, secret);
    }

    /**
     * Save an entity
     *
     * @param entity Entity to be saved
     */
    void save(Entity entity) throws Exception;

    /**
     * Delete an entity
     *
     * @param entity Entity to be deleted
     */
    void delete(Entity entity) throws Exception;

    Instance getInstance(String id) throws Exception;

    Connection loadConnection(String tnsName) throws Exception;

//    Job loadJob(String jobId) throws Exception;

    //  Instance loadInstance(String instanceId) throws Exception;

    class V1 implements Client {

        private static final ObjectMapper jackson = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        private final HttpClient httpClient;

        private V1(String address, String apiKey, String secret) throws MalformedURLException {
            httpClient = new HttpClient(address, apiKey, secret);
        }

        private String requestPath(Entity entity) {
            List<String> paths = new ArrayList<>();
            if (entity instanceof Connection) {
                paths.add("connection");
            } else if (entity instanceof Job) {
                paths.add("job");
            }

            String suffix = entity.getID();
            if (suffix != null) {
                paths.add(HttpClient.cleanURLPath(suffix));
            }

            return "/" + String.join("/", paths);
        }

        @Override
        public void save(Entity entity) throws Exception {
            httpClient.post(requestPath(entity), entity);
        }

        @Override
        public void delete(Entity entity) throws Exception {
            httpClient.delete(requestPath(entity));
        }

        @Override
        public Instance getInstance(String id) throws Exception {
            return new Instance(id, httpClient);
        }

        @Override
        public Connection loadConnection(String tnsName) throws TransportException, ServiceException, IOException {
            Object result = httpClient.get(String.format("/connection/%s", HttpClient.cleanURLPath(tnsName)));
            if (result == null) {
                return null;
            }

            return jackson.convertValue(result, Connection.class);
        }

    }

}
