package me.paulbgd.socketapi.sockets.requests;

import java.util.Map;

/**
 * @author PaulBGD
 */
public abstract class Sender {

    private final Map<String, Class<?>> requestedData, parameters;

    public Sender(Map<String, Class<?>> data, Map<String, Class<?>> parameters) {
        this.requestedData = data;
        this.parameters = parameters;
    }

    public abstract Map<String, Object> onDataRequest(Map<String, Object> parameters);

    public Map<String, Class<?>> getRequestedData() {
        return requestedData;
    }

    public Map<String, Class<?>> getParameters() {
        return parameters;
    }
}
