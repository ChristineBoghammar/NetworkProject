package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class ClientMain {

    public static void main(String args[]) {
        if (args.length == 2) {
            String arg0 = args[0];
            int arg1 = Integer.parseInt(args[1]);
            Socket s;
            try {
                s = new Socket(arg0, arg1);
                ClientReader listener = new ClientReader(s);
                listener.start();
                Scanner keyboard = new Scanner(System.in);
                while(true){
                    String line = keyboard.nextLine();
                    s.getOutputStream().write(line.getBytes());
                    s.getOutputStream().write('\n');

                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }
}
