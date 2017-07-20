package StructureClasses;


import FirebaseClasses.FirebaseException;
import JSONClasses.JSONObject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;



public class Main extends Application {

    static  {
        BasicConfigurator.configure();
    }

    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{

        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/dbGestion.fxml"));
        primaryStage.setTitle("Be Here");
        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add(Main.class.getResource("/bootstrap3.css").toExternalForm());
        primaryStage.setScene(scene);
        if(Main.testInet("google.com")){
            primaryStage.show();
        }
        else {
            Alert dialogE = new Alert(Alert.AlertType.ERROR);
            dialogE.setTitle("Error");
            dialogE.setHeaderText(null);
            dialogE.setContentText(" Ce logiciel a besoin d'une connexion internet verifiez la votre");
            dialogE.showAndWait();
        }

    }

    public static void main(String[] args) {
         launch(args);
    }

    public static boolean testInet(String site) {
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress(site,80);
        try {
            sock.connect(addr,3000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {sock.close();}
            catch (IOException e) {}
        }
    }
}
