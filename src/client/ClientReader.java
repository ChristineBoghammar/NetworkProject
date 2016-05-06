package client;

import protocol.Action;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class ClientReader extends Thread {
    private Socket s;
    private ClientMonitor mon;
    private boolean connected;

    public ClientReader(ClientMonitor mon, Socket s) {
        this.s = s;
        this.mon = mon;
        this.connected = false;
    }

    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            Action action = null;
            /**
             * Det som står nedan skall gälla egentligen. Testar bara med egna inputs
             */
            try {
                while ((action = ((Action) in.readObject())) != null) {
                    mon.putAction(action);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("IOException in ClientReader for client: " + mon.getName() + "\n");

        }
    }

}
