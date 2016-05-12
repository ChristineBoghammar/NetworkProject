package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by christineboghammar on 26/04/16.
 */
public class ServerMain {

    public static void main(String args[]) {
        ServerMonitor mon = new ServerMonitor();
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName("192.168.1.113");
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try (ServerSocket server = new ServerSocket(30000, 50, addr)) {
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
