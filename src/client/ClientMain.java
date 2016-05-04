package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class ClientMain {
    public static String host = "localhost";
    public static int port = 30000;

    public static void main(String args[]) {
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);

        }

        Socket s;
        try {
            Scanner keyboard = new Scanner(System.in);
            System.out.print("Enter your name:" + "\n");
            String name = keyboard.nextLine();

            s = new Socket(host, port);
            ClientMonitor mon = new ClientMonitor(name, s);
            ClientReader cr = new ClientReader(mon, s);
            ClientWriter cw = new ClientWriter(mon, s);
            cr.start();
            cw.start();
            /**
             * Här kommer GUIt ligga :) <3 :D :p
             */

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}

