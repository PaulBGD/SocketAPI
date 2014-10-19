package me.paulbgd.socketapi.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;

/**
 * @author PaulBGD
 */
public class SocketHandler extends Thread {

    private final Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;

        start();
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            while (true) {
                if (socket.isClosed()) {
                    return;
                }
                byte[] bytes = new byte[inputStream.available()];
                if (inputStream.read(bytes) > 0) {
                    try {
                        JSONObject object = (JSONObject) JSONValue.parse(bytes);
                        SocketAPI.onDataReceive(this, object);
                    } catch (Exception e) {
                        System.out.println("[Error] Failed to read data!");
                    }
                }
            }
        } catch (IOException e) {
            // won't happen
        }
    }

    public void send(String channel, boolean requesting, Map<String, Object> map) {
        try {
            map.put("$_channel", channel);
            map.put("$_type", requesting ? 1 : 0);
            OutputStream outputStream = socket.getOutputStream();
            JSONObject json = new JSONObject(map);
            outputStream.write(json.toJSONString(JSONStyle.MAX_COMPRESS).getBytes());
        } catch (Exception e) {
            System.out.println("[Error] Failed to send data! " + e.getMessage());
        }
    }

}
