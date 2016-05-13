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
    private static int port;
    private static String host;

    public static void main(String args[]) {
        ServerMonitor mon = new ServerMonitor();
        InetAddress addr = null;
        if(args.length != 2) {
            System.out.println("Must be two arguments");
            System.exit(1);
        }
        try {
            port = Integer.parseInt(args[0]);
            host = args[1];
            addr = InetAddress.getByName(host);
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try (ServerSocket server = new ServerSocket(port, 50, addr)) {
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
