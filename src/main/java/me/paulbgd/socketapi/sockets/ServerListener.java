package me.paulbgd.socketapi.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author PaulBGD
 */
public class ServerListener extends Thread {

    private final ServerSocket server;

    protected ServerListener(int port) throws IOException {
        this.server = new ServerSocket(port);

        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = server.accept();
                SocketAPI.addConnection(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
