package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import protocol.Action;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class ClientGUI extends Application {
    public static String host = "192.168.1.113";
    public static int port = 30000;
    private Stage primaryStage;

    public static void main(String args[]) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        final List<String> params = getParameters().getRaw();
        if(params.size() != 2){
            System.out.println("Two arguments have to be applied, restart the system");
            System.exit(1);
        }
        host = params.get(0);
        port = Integer.parseInt(params.get(1));

        Socket s;
        try {
            Scanner keyboard = new Scanner(System.in);
            System.out.print("Enter your name:" + "\n");
            String name = keyboard.nextLine();

            s = new Socket(host, port);

            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("../gui/startGui.fxml"));
            Parent start = loader1.load();
            ClientGUIController cgc = loader1.getController();


            ClientMonitor mon = new ClientMonitor(name, s, cgc, this);
            ClientReader cr = new ClientReader(mon, s);
            ClientWriter cw = new ClientWriter(mon, s);
            cr.start();
            cw.start();
            mon.putAction(new Action(name, name, 0, -1));


            cgc.setGUI(this);
            cgc.setMonitor(mon);

            Scene scene = new Scene(start, 600, 400);


            primaryStage.setTitle("Skajp");
            primaryStage.setScene(scene);
            primaryStage.show();

//            while (true) {
//                System.out.print("Write a Command: " + "\n");
//                String line = keyboard.nextLine();
//                String[] actionArgs = line.split("-");
//                ArrayList<String> e = new ArrayList<String>();
//                e.add(actionArgs[3]);
//                mon.putAction(new Action(actionArgs[0], actionArgs[1], Integer.parseInt(actionArgs[2]), e));
//            }
            /**
             * Här kommer GUIt ligga :) <3 :D :p
             */

        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

    public void activateCall() throws IOException {
        FXMLLoader loader3 = new FXMLLoader(getClass().getResource("../gui/activeCall.fxml"));
        Parent activeCall = loader3.load();
        ActiveCallGUIController acg = loader3.getController();

        Scene scene = new Scene(activeCall, 600, 400);

        primaryStage.setTitle("Skajp");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public boolean incomingCall(String sender) throws IOException {
        System.out.println("Got to incoming call");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Incoming call");
        alert.setHeaderText(sender + " is calling you");
        alert.setContentText("Do you want to accept?");

        ButtonType buttonTypeOne = new ButtonType("Accept");
        ButtonType buttonTypeTwo = new ButtonType("Reject");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            System.out.println("Accepted call");
            activateCall();
            return true;
        } else if (result.get() == buttonTypeTwo) {
            System.out.println("Rejected call");
            return false;
        } else {
            System.out.println("How did you get here?");
            return false;
        }
    }

    public void startScreen() {

    }
}

