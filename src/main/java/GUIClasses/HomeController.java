package GUIClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.FirebaseException;
import JSONClasses.JSONObject;
import StructureClasses.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Button;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class HomeController implements Initializable{

    public void initialize(URL location, ResourceBundle resources) {

        inisialserBaseDeDonne();

    }

    private void inisialserBaseDeDonne() {

        DbOperations.setRoot("https://testfirebase-12722.firebaseio.com/");

        if(Main.testInet("google.com")){
            final Task<Void> inisialiseTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    DbOperations.initialiserLaBaseDeDonnee();

                    return null;
                }
            };

            inisialiseTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                public void handle(WorkerStateEvent event) {

                    GUIutils.taskIndicatorForm.getDialogStage().close();
                }
            });

            GUIutils.taskIndicatorForm.activateProgressBar(inisialiseTask);
            GUIutils.taskIndicatorForm.getDialogStage().show();

            GUIutils.executor.execute(inisialiseTask);
        }
        else {
            Alert dialogE = new Alert(Alert.AlertType.ERROR);
            dialogE.setTitle("Error");
            dialogE.setHeaderText(null);
            dialogE.setContentText(" Ce logiciel a besoin d'une connexion internet verifiez la votre");
            dialogE.showAndWait();
        }

    }

    @FXML
    BorderPane borderPane;

    public void aPropos(ActionEvent actionEvent) throws IOException {
        Stage stage =new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/about.fxml"));
        stage.setTitle("Be Here");
        stage.getIcons().add(new Image("Images/owl.png"));
        stage.setMaxHeight(500);
        stage.setMaxWidth(650);
        stage.setMinHeight(500);
        stage.setMinWidth(650);
        Scene scene = new Scene(root , 600,500);

        scene.getStylesheets().add(Main.class.getResource("/bootstrap3.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void promos(ActionEvent actionEvent) throws IOException {
        borderPane.setCenter((Node) FXMLLoader.load(getClass().getResource("/promos.fxml")));
    }

    public void statistique(ActionEvent actionEvent) throws IOException {

        int[] sexeEcole = getSexeEcole();
        int[] sexeEnseignant = getSexeEnseignant();

        creatStatistiqueStage(LoadStatistiqueToPieChart(sexeEcole[0], sexeEcole[1], "Etudiants"),
                LoadStatistiqueToPieChart(sexeEnseignant[0], sexeEnseignant[1], "Enseignant"));
    }

    public void aide(ActionEvent actionEvent)  {

        try {
            Desktop.getDesktop().browse(new URI("www.groupe22assiduite.tk"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void absences(ActionEvent actionEvent) throws IOException {
        borderPane.setCenter(FXMLLoader.load(getClass().getResource("/absences.fxml")));

    }

    public void site(ActionEvent actionEvent) {

        try {
            Desktop.getDesktop().browse(new URI("www.esi-sba.dz"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int[] getSexeEcole(){

        int[] returnSexe = {0, 0} ;

        String[] path ={DbOperations.CYCLES.substring(1)};

        HashMap<String, Cycle> cycleHashMap = DbOperations.getChildren(Structure.class, Cycle.class, path);

        for (Map.Entry<String, Cycle> cycleEntry : cycleHashMap.entrySet()) {

            String[] path0 = PromosController.pathGenerator(path, cycleEntry.getKey());

            HashMap<String, Filliere> filliereHashMap = DbOperations.getChildren(Cycle.class, Filliere.class, path0);

            for (Map.Entry<String, Filliere> filliereEntry : filliereHashMap.entrySet()) {

                String[] path1 = PromosController.pathGenerator(path0, filliereEntry.getKey());

                HashMap<String, Promo> promoHashMap = DbOperations.getChildren(Filliere.class, Promo.class, path1);

                for (Map.Entry<String, Promo> promoEntry : promoHashMap.entrySet()) {

                    String[] path2 = PromosController.pathGenerator(path1, promoEntry.getKey());

                    int [] nbSexe = PromosController.getSexeEtudiantPromo(path2);
                    returnSexe[0] += nbSexe[0];
                    returnSexe[1] += nbSexe[1];

                }

            }

        }

        return returnSexe;

    }

    private int[] getSexeEnseignant(){

        int[] returnSexe = {0,0};
        String[] path ={DbOperations.ENSEIGNANT_MODULE.substring(1)};

        HashMap<String, Enseignant> enseignantHashMap = DbOperations.getChildren(Structure.class, Enseignant.class, path);

        for (Map.Entry<String, Enseignant> enseignantEntry : enseignantHashMap.entrySet()) {

            if (enseignantEntry.getValue().getSexe() == 'H') returnSexe[0]++ ;
                else returnSexe[1]++;

        }
        return returnSexe;
    }

    private PieChart LoadStatistiqueToPieChart(int nombreDesHomme, int nombreDesFamme, String nom){

        PieChart pieChart =new PieChart();

        ObservableList<PieChart.Data> datas = FXCollections.observableArrayList(
                new PieChart.Data("Homme",nombreDesHomme),
                new PieChart.Data("Femme",nombreDesFamme));
        datas.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName()," ", data.pieValueProperty(), " Etudiant"
                        )
                )
        );
        pieChart.setData(datas);
        pieChart.setTitle("Statistique Du Sexe des "+nom);
        return pieChart;
    }
    public void creatStatistiqueStage(PieChart pieChart0,PieChart pieChart1) throws IOException {

        Stage statistiquesstage = new Stage();
        statistiquesstage.setTitle("Stistique");
        statistiquesstage.getIcons().add(new Image("Images/images.jpg"));

        SplitPane splitPane = new SplitPane();
        BorderPane borderPane = new BorderPane();
        BorderPane borderPane1 =new BorderPane();
        splitPane.getItems().add(0,borderPane);
        splitPane.getItems().add(1,borderPane1);



        Scene scene = new Scene(splitPane, 1000, 700);



        borderPane1.setCenter(pieChart1);
        javafx.scene.control.Button ExportLineChart =new javafx.scene.control.Button("Imprimer");
        ExportLineChart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pieChart1.setAnimated(false);
                DirectoryChooser directoryChooser= new DirectoryChooser();
                File file = directoryChooser.showDialog(null);
                saveAsPng(pieChart1, file.getAbsolutePath()+"//"+pieChart1.getTitle()+".png");


            }
        });
        HBox hBox =new HBox();
        hBox.getChildren().add(ExportLineChart);
        hBox.setAlignment(Pos.CENTER);
        ExportLineChart.setAlignment(Pos.CENTER);
        borderPane1.setBottom(hBox);


        borderPane.setCenter(pieChart0);
        javafx.scene.control.Button exportPieChart =new javafx.scene.control.Button("Imprimer");
        exportPieChart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pieChart0.setAnimated(false);
                DirectoryChooser directoryChooser= new DirectoryChooser();
                File file = directoryChooser.showDialog(null);
                saveAsPng(pieChart0, file.getAbsolutePath()+"//"+pieChart0.getTitle()+".png");

            }
        });
        HBox hBox1 =new HBox();
        hBox1.getChildren().add(exportPieChart);
        hBox1.setAlignment(Pos.CENTER);
        exportPieChart.setAlignment(Pos.CENTER);
        borderPane.setBottom(hBox1);




        scene.getStylesheets().add(Main.class.getResource("/bootstrap3.css").toExternalForm());


        statistiquesstage.setScene(scene);
        statistiquesstage.show();

    }

    public void saveAsPng(PieChart pieChart, String path) {
        WritableImage image = pieChart.snapshot(new SnapshotParameters(), null);
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}