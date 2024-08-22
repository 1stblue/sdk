package io.bluepipe.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bluepipe.client.core.HttpClient;
import io.bluepipe.client.core.ServiceException;
import io.bluepipe.client.core.TransportException;
import io.bluepipe.client.model.Connection;
import io.bluepipe.client.model.CopyTask;
import io.bluepipe.client.model.Entity;

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

    /**
     * Load Connection Config from remote
     *
     * @param tnsName id of the connection config
     * @return Connection
     * @throws ServiceException   when
     * @throws TransportException when
     */
    Connection getConnection(String tnsName) throws Exception;

    /**
     * Load CopyTask Configuration from remote
     *
     * @param taskId id of the task config
     * @return CopyTask
     */
    CopyTask getCopyTask(String taskId) throws Exception;

    /**
     * Submit copy entity to run immediately
     *
     * @return List of Instance
     */
    List<Instance> submit(CopyTask entity, Context context) throws Exception;

    Instance getInstance(String id) throws Exception;

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
        public List<Instance> submit(CopyTask entity, Context context) throws Exception {
            httpClient.post(requestPath(entity), entity);
            return null;
        }

        @Override
        public Instance getInstance(String id) throws Exception {
            // TODO: load
            return new Instance(id, httpClient);
        }

        @Override
        public Connection getConnection(String tnsName) throws TransportException, ServiceException, IOException {
            Object result = httpClient.get(String.format("/connection/%s", HttpClient.cleanURLPath(tnsName)));
            if (result == null) {
                return null;
            }

            return jackson.convertValue(result, Connection.class);
        }

        @Override
        public CopyTask getCopyTask(String taskId) throws Exception {
            Object result = httpClient.get(String.format("/job/%s/rule", HttpClient.cleanURLPath(taskId)));
            return null;
        }

    }

}