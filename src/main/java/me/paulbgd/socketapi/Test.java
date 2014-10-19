package me.paulbgd.socketapi;

import java.io.IOException;
import java.util.Map;
import me.paulbgd.socketapi.sockets.SocketAPI;
import me.paulbgd.socketapi.sockets.requests.Requester;
import me.paulbgd.socketapi.sockets.requests.Sender;
import me.paulbgd.socketapi.sockets.utils.MapBuilder;

/**
 * @author PaulBGD
 */
public class Test {

    static int connected = 0;

    public static void main(String[] args) throws IOException {
        // start two
        SocketAPI.startListening(2000);
        SocketAPI.startListening(3000);

        SocketAPI.addConnection("localhost", 2000, new Runnable() {
            @Override
            public void run() {
                if (++connected == 2) {
                    addRequests();
                }
            }
        });
        SocketAPI.addConnection("localhost", 3000, new Runnable() {
            @Override
            public void run() {
                if (++connected == 2) {
                    addRequests();
                }
            }
        });

        while (true) {

        }
    }

    static void addRequests() {
        SocketAPI.addSender("GetName", new Sender(new MapBuilder<String, Class<?>>() {{
            $("Name", String.class);
        }}, new MapBuilder<String, Class<?>>() {{
            $("Id", int.class);
        }}) {
            @Override
            public Map<String, Object> onDataRequest(Map<String, Object> parameters) {
                int id = (int) parameters.get("Id");
                String name = null;
                switch (id) {
                    case 1:
                        name = "PaulBGD";
                }
                final String finalName = name;
                return new MapBuilder<String, Object>() {{
                    $("Name", finalName);
                }};
            }
        });

        SocketAPI.request("GetName", new Requester(new MapBuilder<String, Object>() {{
            $("Id", 1);
        }}, new MapBuilder<String, Class<?>>() {{
            $("Name", String.class);
        }}) {
            @Override
            public void onReceive(Map<String, Object> data) {
                System.out.println("Name: " + data.get("Name"));
            }

            @Override
            public void onError(String error) {
                System.out.println("[Error:] " + error);
            }
        });
    }

}
