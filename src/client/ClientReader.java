package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;


public class ClientReader extends Thread {
    private Socket s;
    private client.ClientMonitor mon;

    public ClientReader(ClientMonitor mon, Socket s) {
        this.s = s;
        this.mon = mon;
    }

    public void run() {
        try {
            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String line;
            while((line = br.readLine()) != null){
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Socket Closed");
        } finally{
            return;
        }
    }

}
