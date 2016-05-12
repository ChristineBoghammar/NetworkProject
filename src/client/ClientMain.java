package client;

import protocol.Action;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class ClientMain {
    public static String host = "192.168.1.113";
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
            mon.putAction(new Action(name, name, 0, -1));

            while (true) {
                System.out.print("Write a Command: " + "\n");
                String line = keyboard.nextLine();
                String[] actionArgs = line.split("-");
                ArrayList<String> e = new ArrayList<String>();
                e.add(actionArgs[3]);
                mon.putAction(new Action(actionArgs[0], actionArgs[1], Integer.parseInt(actionArgs[2]), e));
            }
            /**
             * Här kommer GUIt ligga :) <3 :D :p
             */

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}

