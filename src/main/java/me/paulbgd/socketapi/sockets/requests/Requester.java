package me.paulbgd.socketapi.sockets.requests;

import java.util.Map;

/**
 * @author PaulBGD
 */
public abstract class Requester {

    private final Map<String, Object> requestedData;
    private final Map<String, Class<?>> parameters;

    public Requester(Map<String, Object> data, Map<String, Class<?>> parameters) {
        this.requestedData = data;
        this.parameters = parameters;
    }

    public abstract void onReceive(Map<String, Object> data);

    public abstract void onError(String error);

    public Map<String, Object> getRequestedData() {
        return requestedData;
    }

    public Map<String, Class<?>> getParameters() {
        return parameters;
    }
}
