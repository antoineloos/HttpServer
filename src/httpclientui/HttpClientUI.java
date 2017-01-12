/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpclientui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sun.security.x509.IPAddressName;

/**
 *
 * @author Epulapp
 */
public class HttpClientUI extends Application {

    public final String style = " -fx-border-color: white; " + "-fx-border-width: 3;" + "-fx-background-color: #6AA6E2;" + "-fx-spacing: 5px;" + "-fx-text-fill: white;";
    JFXTextArea FiletextBox;
    JFXTextArea IPtextBox;
    public String courantIP = "";
    public String courantFileName = "";
    private PrintWriter out;

    @Override
    public void start(Stage primaryStage) {

        FlowPane pane = new FlowPane();
        pane.setOrientation(Orientation.VERTICAL);
        pane.setAlignment(Pos.CENTER);

        IPtextBox = new JFXTextArea();
        IPtextBox = new JFXTextArea();
        IPtextBox.setPrefHeight(50);
        IPtextBox.setPrefWidth(150);

        FiletextBox = new JFXTextArea();
        FiletextBox = new JFXTextArea();
        FiletextBox.setPrefHeight(50);
        FiletextBox.setPrefWidth(150);

        JFXButton btnConnect = new JFXButton();
        btnConnect.setText("Set IP");
        btnConnect.setStyle(style);
        btnConnect.setPrefHeight(50);
        btnConnect.setPrefWidth(150);
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                courantIP = IPtextBox.getText();
                
            }
        });

        JFXButton btnGet = new JFXButton();
        btnGet.setText("Get this file");
        btnGet.setStyle(style);
        btnGet.setPrefHeight(50);
        btnGet.setPrefWidth(150);
        btnGet.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                courantFileName = FiletextBox.getText();
                ConnectAndWait();
                GetFile(out);
            }
        });

        StackPane root = new StackPane();
        pane.getChildren().add(IPtextBox);
        pane.getChildren().add(btnConnect);
        pane.getChildren().add(FiletextBox);
        pane.getChildren().add(btnGet);
        root.getChildren().add(pane);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Client HTTP");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void ConnectAndWait() {
        Socket s = null;
        try {
            s = new Socket(courantIP, 80);
            if (s == null) {
                System.exit(-1);
            }
            out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            new Thread(new Listener(s,out)).start();

        } catch (UnknownHostException uhe) {

            System.out.println("serveur inconue ");
            s = null;

        } catch (IOException ioe) {

            System.out.println("Impossible de se connecter au serveur ");
            s = null;

        }
    }

    public void GetFile(PrintWriter courantSocket) {

        out.println("GET /" + courantFileName + " HTTP/1.1\n" +"Host:"+courantIP+"\r\n");

        out.flush();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
