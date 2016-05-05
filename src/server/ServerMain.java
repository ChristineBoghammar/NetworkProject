package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by christineboghammar on 26/04/16.
 */
public class ServerMain {

    public static void main(String args[]) {
        ServerMonitor mon = new ServerMonitor();

        try (ServerSocket server = new ServerSocket(30000)) {
            while (true) {
                try {
                    Socket connection = server.accept();
                    System.out.println("Socket accepted");
                    new ServerReader(mon, connection).start();
                    new ServerWriter(mon, connection).start();

                } catch (IOException ex) {

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
