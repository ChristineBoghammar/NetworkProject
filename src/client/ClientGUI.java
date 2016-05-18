package client;

import javafx.application.Application;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
import protocol.Action;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class ClientGUI extends Application {
    public static String host = "192.168.1.203";
    public static int port = 30000;
    private Stage primaryStage;
    private FXMLLoader startLoader;
    private FXMLLoader activeLoader;
    private Parent start;
    private Parent active;

    public static void main(String args[]) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        final List<String> params = getParameters().getRaw();
        if (params.size() != 2) {
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

            startLoader = new FXMLLoader(getClass().getResource("../gui/startGui.fxml"));
            start = startLoader.load();
            ClientGUIController cgc = startLoader.getController();

            activeLoader = new FXMLLoader(getClass().getResource("../gui/activeCall.fxml"));
            active = activeLoader.load();
            ActiveCallGUIController agc = activeLoader.getController();


            ClientMonitor mon = new ClientMonitor(name, s, cgc, agc, this);
            ClientReader cr = new ClientReader(mon, s);
            ClientWriter cw = new ClientWriter(mon, s);
            cr.start();
            cw.start();
            mon.putAction(new Action(name, name, 0, -1));


            cgc.setGUI(this);
            cgc.setMonitor(mon);

            agc.setGUI(this);
            agc.setMonitor(mon);

            Scene scene = new Scene(start, 600, 400);
            setScene(scene);

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
        if(active.getScene() == null){
            Scene scene = new Scene(active, 600, 400);
            setScene(scene);
        } else {
            setScene(active.getScene());
        }
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
        if (result.get() == buttonTypeOne) {
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

    public void startScreen() throws IOException {
        if(start.getScene() == null){
            Scene scene = new Scene(start, 600, 400);
            setScene(scene);
        } else {
            setScene(start.getScene());
        }
    }

    public void setScene(Scene scene){
        primaryStage.setTitle("Skajp");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.exit(1);
            }
        });


    }
}

