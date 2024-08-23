package io.bluepipe.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bluepipe.client.core.HttpClient;
import io.bluepipe.client.core.ServiceException;
import io.bluepipe.client.core.TransportException;
import io.bluepipe.client.model.Application;
import io.bluepipe.client.model.Connection;
import io.bluepipe.client.model.CopyTask;
import io.bluepipe.client.model.Entity;

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
    Entity save(Entity entity) throws Exception;

    /**
     * Delete an entity
     *
     * @param entity Entity to be deleted
     */
    void delete(Entity entity) throws Exception;

    /**
     * Load Connection Config from remote
     *
     * @param name ID of the connection
     * @return Connection
     * @throws ServiceException   when
     * @throws TransportException when
     */
    Connection getConnection(String name) throws Exception;

    /**
     * Load Application from remote
     *
     * @param name ID of the application
     * @return Application
     */
    Application getApplication(String name) throws Exception;

    /**
     * Submit copy entity to run immediately
     *
     * @return List of Instance
     */
    @Deprecated
    List<Instance> submit(CopyTask entity, Context context) throws Exception;

//    Job loadJob(String jobId) throws Exception;

    class V1 implements Client {

        private static final ObjectMapper jackson = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        private final HttpClient httpClient;

        private V1(String address, String apiKey, String secret) throws MalformedURLException {
            httpClient = new HttpClient(address, apiKey, secret);
        }

        private String requestPath(Entity entity) {
            List<String> paths = new ArrayList<>();
            if (entity instanceof Connection) {
                paths.add("connection");
            } else if (entity instanceof CopyTask) {
                paths.add("next/task");
            } else if (entity instanceof Application) {
                paths.add("job");
            }

            String suffix = entity.getID();
            if (suffix != null) {
                paths.add(HttpClient.cleanURLPath(suffix));
            }

            return "/" + String.join("/", paths);
        }

        @Override
        public Entity save(Entity entity) throws Exception {
            httpClient.post(requestPath(entity), entity);
            return null;
        }

        @Override
        public void delete(Entity entity) throws Exception {
            httpClient.delete(requestPath(entity));
        }

        @Override
        public List<Instance> submit(CopyTask entity, Context context) throws Exception {
            Object result = httpClient.post(requestPath(entity), entity);
            System.out.println(result);
            return null;
        }

        @Override
        public Connection getConnection(String name) throws Exception {
            Object result = httpClient.get(String.format("/connection/%s", HttpClient.cleanURLPath(name)));
            if (result == null) {
                return null;
            }

            return jackson.convertValue(result, Connection.class);
        }

        @Override
        public Application getApplication(String name) throws Exception {
            Object result = httpClient.get(String.format("/job/%s/config", HttpClient.cleanURLPath(name)));
            if (result == null) {
                return null;
            }

            return jackson.convertValue(result, Application.class);
        }

    }

}
