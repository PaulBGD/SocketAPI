package me.paulbgd.socketapi.sockets;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import me.paulbgd.socketapi.sockets.requests.Requester;
import me.paulbgd.socketapi.sockets.requests.Sender;
import me.paulbgd.socketapi.sockets.utils.MapBuilder;

/**
 * @author PaulBGD
 */
public class SocketAPI {

    private static final List<SocketHandler> handles = new CopyOnWriteArrayList<>();
    private static final HashMap<String, Sender> senders = new HashMap<>();
    private static final HashMap<String, Requester> receivers = new HashMap<>();

    public static void startListening(int port) throws IOException {
        new ServerListener(port);
    }

    public static void addConnection(String ip, int port, final Runnable after) {
        new SocketListener(new SocketListener.SocketResponse() {
            @Override
            public void onConnect(Socket socket) {
                handles.add(new SocketHandler(socket));
                if (after != null) {
                    after.run();
                }
            }
        }, ip, port);
    }

    protected static void addConnection(Socket socket) {
        handles.add(new SocketHandler(socket));
    }

    public static void addSender(String channel, Sender sender) {
        senders.put(channel, sender);
    }

    public static void request(String channel, Requester requester) {
        receivers.put(channel, requester);
        for (SocketHandler handle : handles) {
            handle.send(channel, true, requester.getRequestedData());
        }
    }

    protected static void onDataReceive(SocketHandler handler, Map<String, Object> map) {
        String channel = (String) map.remove("$_channel");
        boolean requesting = map.remove("$_type") == 1;
        if (requesting) {
            // it's a request, so check our senders
            Sender sender = senders.get(channel);
            if (sender == null) {
                handler.send(channel, true, new MapBuilder<String, Object>() {{
                    $("$_error", "No such channel!");
                }});
                return;
            }
            final String match = doParametersMatch(sender.getParameters(), map);
            if (match == null) {
                // they match! It's verified, so let's get the new data!
                Map<String, Object> returned = sender.onDataRequest(map);
                handler.send(channel, false, returned); // send back the new data!
            } else {
                handler.send(channel, false, new MapBuilder<String, Object>() {{
                    $("$_error", match);
                }});
            }
        } else {
            // we're returning data!
            Requester request = receivers.get(channel);
            if (request == null) {
                // EH? What's going on.. so how did this data get here?
                return;
            }
            if (map.containsKey("$_error")) {
                // we're returning an error :(
                request.onError((String) map.get("$_error"));
            } else {
                String match = doParametersMatch(request.getParameters(), map);
                if (match == null) {
                    // the data is good!
                    request.onReceive(map);
                } else {
                    request.onError("Invalid data");
                }
            }
        }
    }

    private static String doParametersMatch(Map<String, Class<?>> parameters, Map<String, Object> data) {
        for (Map.Entry<String, Class<?>> entry : parameters.entrySet()) {
            Class<?> changed = getPrimitive(entry.getValue());
            if (!data.containsKey(entry.getKey())) {
                return "Missing parameters: " + entry.getKey();
            } else if (!changed.isAssignableFrom(getPrimitive(data.get(entry.getKey()).getClass()))) {
                return "Invalid data type: " + data.get(entry.getKey()).getClass().getName() + " instead of " + changed.getName();
            }
        }
        return null;
    }

    private static Class<?> getPrimitive(Class<?> clazz) {
        switch (clazz.getSimpleName()) {
            case "Integer":
                return int.class;
            case "Float":
                return float.class;
            case "Double":
                return double.class;
            case "Long":
                return long.class;
            case "Boolean":
                return boolean.class;
            default:
                return clazz;
        }
    }

}
