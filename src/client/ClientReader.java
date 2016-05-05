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
        if(!connected){
            String name = mon.getName();
            mon.putAction(new Action(name, name, 0, -1));
        }
//        try {
//            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
//            Action action = null;
            /**
             * Det som står nedan skall gälla egentligen. Testar bara med egna inputs
             */
//            while ((action = ((Action) in.readObject())) != null) {
//                mon.putAction(action);
//            }
            Scanner keyboard = new Scanner(System.in);
            while(true){
                System.out.print("Write a Command: " + "\n");
                String line = keyboard.nextLine();
                String[] actionArgs = line.split("-");
                ArrayList<String> e = new ArrayList<String>();
                e.add(actionArgs[3]);
                mon.putAction(new Action(actionArgs[0], actionArgs[1], Integer.parseInt(actionArgs[2]), e));

            }


//        } catch (IOException e) {
//            System.err.print("IOException in ClientReader for client: " + mon.getName() + "\n");
//
//        }
    }

}
