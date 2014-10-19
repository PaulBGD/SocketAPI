package me.paulbgd.socketapi.sockets;

import java.net.Socket;

/**
 * @author PaulBGD
 */
public class SocketListener extends Thread {

    private final SocketResponse response;
    private final String ip;
    private final int port;

    public SocketListener(SocketResponse response, String ip, int port) {
        this.response = response;
        this.ip = ip;
        this.port = port;

        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = new Socket(this.ip, this.port);
                this.response.onConnect(socket);
                return;
            } catch (Exception e) {
                if(e.getMessage().equals("Connection refused: connect")) {
                    continue;
                }
                System.out.println("Failed to connect " + e.getMessage());
            }
        }
    }

    public interface SocketResponse {
        public void onConnect(Socket socket);
    }

}
