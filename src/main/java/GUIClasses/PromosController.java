package GUIClasses;

import JSONClasses.JSONObject;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import FirebaseClasses.DbOperations;
import StructureClasses.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class PromosController implements Initializable{

    public static final Integer ANNEE_SPINNER_INITIAL = 2012, NIVEAU_SPINNER_INITIAL = 1;
    @FXML
    private ChoiceBox<Cycle> cyclesChoiceBox;
    @FXML
    private ChoiceBox<Filliere> fillieresChoiceBox;
    @FXML
    private ChoiceBox<Specialite> specialitesChoiceBox;
    @FXML
    private TableView<Promo> promosTableView;
    @FXML
    private TextField designationTextField;
    @FXML
    private Spinner<Integer> anneeSpinner, niveauSpinner;

    public void initialize(URL location, ResourceBundle resources) {
        //activer la selection multiple
        promosTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //


        //initialisation des Spinner

        SpinnerValueFactory<Integer> anneeSpinnerValueFactory = new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                this.setValue(getValue() - 1);
            }

            @Override
            public void increment(int steps) {
                this.setValue(getValue() + 1);
            }
        };

        anneeSpinnerValueFactory.setValue(ANNEE_SPINNER_INITIAL);
        anneeSpinner.setValueFactory(anneeSpinnerValueFactory);

        SpinnerValueFactory<Integer> niveauSpinnerValueFactory = new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                this.setValue(getValue() - 1);
            }

            @Override
            public void increment(int steps) {
                this.setValue(getValue() + 1);
            }
        };
        niveauSpinnerValueFactory.setValue(NIVEAU_SPINNER_INITIAL);
        niveauSpinner.setValueFactory(niveauSpinnerValueFactory);


        //remplissage des choiceBox de l'interface

        loadCyclesToChoiceBox();

        cyclesChoiceBox.valueProperty().addListener(new ChangeListener<Cycle>() {

            public void changed(ObservableValue<? extends Cycle> observable, Cycle oldValue, Cycle newValue) {

                loadFillieresToChoiceBox();

            }
        });

        fillieresChoiceBox.valueProperty().addListener(new ChangeListener<Filliere>() {
            public void changed(ObservableValue<? extends Filliere> observable, Filliere oldValue, Filliere newValue) {

                if(fillieresChoiceBox.getSelectionModel().getSelectedItem()!=null)
                    loadSpecialiteToChoiceBox();

            }
        });
        specialitesChoiceBox.valueProperty().addListener(new ChangeListener<Specialite>() {
            public void changed(ObservableValue<? extends Specialite> observable, Specialite oldValue, Specialite newValue) {

                loadPromosToTableView();
            }
        });

        //
    }

    private void loadCyclesToChoiceBox() {


        String[] path ={DbOperations.CYCLES.substring(1)};

        GUIutils.loadChildrenToChoiceBox(cyclesChoiceBox, path, Structure.class, Cycle.class);    }

    private void loadFillieresToChoiceBox() {

        specialitesChoiceBox.getItems().clear();
        promosTableView.getColumns().clear();

        String cycleId = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, cycleId);

        String[] path ={DbOperations.CYCLES.substring(1), cycleId};

        GUIutils.loadChildrenToChoiceBox(fillieresChoiceBox, path, Cycle.class, Filliere.class);
    }
    private void loadSpecialiteToChoiceBox() {

        promosTableView.getColumns().clear();

        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, idFilliere);
        String[] path = {DbOperations.FILIERE_SPECIALITES.substring(1), idFilliere};
        GUIutils.loadChildrenToChoiceBox(specialitesChoiceBox, path, Filliere.class, Specialite.class);
    }
    private void loadPromosToTableView() {


        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idSpecialite = specialitesChoiceBox.getSelectionModel().getSelectedItem().getId();

        String[] attributs = {"designation", "niveau", "annee"};
        String[] columnsTitle = {"Designation", "Niveau", "Année"};


        if (idSpecialite.equals(Specialite.SANS_SPECIALITE))    {

            String[] path0 ={DbOperations.CYCLES.substring(1), idCycle, idFilliere};
            //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere);
            GUIutils.loadChildrenToTableView(promosTableView, path0, Filliere.class, Promo.class,
                    attributs, columnsTitle);
        }
        else  {

            String[] path0 ={DbOperations.SPECIALITE_PROMOS.substring(1), idSpecialite};
            //String path = DbOperations.firebasePath(DbOperations.SPECIALITE_PROMOS, idSpecialite);
            GUIutils.loadChildrenToTableView(promosTableView, path0, Specialite.class, Promo.class,
                    attributs, columnsTitle);
        }


    }
    public void ajouterPromo(ActionEvent actionEvent) {

        Promo promo = new Promo(designationTextField.getText(), anneeSpinner.getValue().toString());
        promo.setId(GUIutils.generateId(promo));
        promo.setIdCycle(cyclesChoiceBox.getSelectionModel().getSelectedItem().getId());
        promo.setIdFilliere(fillieresChoiceBox.getSelectionModel().getSelectedItem().getId());
        promo.setIdSpecialite(specialitesChoiceBox.getSelectionModel().getSelectedItem().getId());
        promo.setNiveau(niveauSpinner.getValue());

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(promo);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadPromosToTableView();
                designationTextField.clear();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);
    }
    public void supprimerPromos(ActionEvent actionEvent){

        GUIutils.supprmerObj(promosTableView);
        DbGestionController.actualiserLaBaseDeDonnee();
        loadPromosToTableView();
    }


    public static int[] getSexeEtudiantPromo(String[] pathIn){

        int nombreDesHomme = 0; int nombreDesFemme = 0;

        HashMap<String, Section> sectionHashMap = DbOperations.getChildren(Promo.class, Section.class, pathIn);

        for (Map.Entry<String, Section> sectionEntry : sectionHashMap.entrySet()){

            String[] path = pathGenerator(pathIn, sectionEntry.getValue().getId());

            HashMap<String, Groupe> groupeHashMap = DbOperations.getChildren(Section.class, Groupe.class, path);

            for (Map.Entry<String, Groupe> groupeEntry : groupeHashMap.entrySet()){

                String[] path0 =pathGenerator(path, groupeEntry.getValue().getId());

                HashMap<String, Etudiant> etudiantHashMap;

                etudiantHashMap = DbOperations.getChildren(Groupe.class, Etudiant.class, path0);

                for (Map.Entry<String, Etudiant> etudiantEntry : etudiantHashMap.entrySet()){

                    if (etudiantEntry.getValue().getSexe() == 'H') nombreDesHomme++ ;
                    else nombreDesFemme++;

                }
            }
        }

        return  new int[]{nombreDesHomme, nombreDesFemme};
    }

    public static String[] pathGenerator(String[] path, String add) {

        String[] returnpath= new String[path.length+1];

        for (int i = 0; i <path.length ; i++) {

            returnpath[i]=path[i];
        }
        returnpath[path.length]= add;

        return returnpath;
    }

    public static HashMap<String,Absence> getAbcensePromo(String[] pathIn){

        HashMap<String, Absence> returnHashMap = new HashMap<>();

        HashMap<String, Section> sectionHashMap = DbOperations.getChildren(Promo.class, Section.class, pathIn);
        for (Map.Entry<String, Section> sectionEntry : sectionHashMap.entrySet()){

            String[] path0 = pathGenerator(pathIn, sectionEntry.getValue().getId());

            HashMap<String, Groupe> groupeHashMap = DbOperations.getChildren(Section.class, Groupe.class, path0);

            for (Map.Entry<String, Groupe> groupeEntry : groupeHashMap.entrySet()){

                String[] path1 = pathGenerator(path0, groupeEntry.getValue().getId());

                HashMap<String, Etudiant> etudiantHashMap = DbOperations.getChildren(Groupe.class, Etudiant.class, path1);

                for (Map.Entry<String, Etudiant> etudiantEntry : etudiantHashMap.entrySet()){

                    String[] path2 = pathGenerator(path1, etudiantEntry.getValue().getId());

                    JSONObject base = DbOperations.baseDonnees;

                    for (int i = 0; i < path2.length ; i++) {

                        if(base.length()!=0)
                            base = base.getJSONObject(path2[i]);

                    }


                    for ( String moduleId : base.keySet()){
                        if(!new Etudiant().getMap().containsKey(moduleId)){

                            String[] path3 = pathGenerator(path2, moduleId);

                            HashMap<String, Absence> absenceHashMap = DbOperations.getChildren(Module.class, Absence.class, path3);

                            returnHashMap.putAll(absenceHashMap);
                        }
                    }
                }
            }
        }
        return returnHashMap;
    }


    public void statistique(ActionEvent actionEvent) throws IOException {

        Promo promo=promosTableView.getSelectionModel().getSelectedItem();

        if (promo != null){

            String[] path = {DbOperations.CYCLES.substring(1), promo.getIdCycle(), promo.getIdFilliere(), promo.getId()};

            int[] sexeTab= getSexeEtudiantPromo(path);

            creatStatistiqueStage( LoadStatistiqueToPieChart(sexeTab[0],sexeTab[1],promo)
                    ,LoadStatistiqueToLineChart( getAbcensePromo(path), promo));


        }else ;


    }


    private LineChart LoadStatistiqueToLineChart(HashMap<String , Absence> absenceHashMap, Promo promo){
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("MONTH");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre d' Absences");
        final LineChart<String,Number> lineChart =
                new LineChart<String,Number>(xAxis,yAxis);
        lineChart.setTitle("Staistique des absences de la "+promo.getDesignation());
        XYChart.Series series = new XYChart.Series();
        series.setName("nombre d' absences ");
        int[] nomnreAbsence=GUIutils.getNombreAbsencesByMonth(absenceHashMap);
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(1),nomnreAbsence[0]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(2),nomnreAbsence[1]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(3),nomnreAbsence[2]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(4),nomnreAbsence[3]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(5),nomnreAbsence[4]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(6),nomnreAbsence[5]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(7),nomnreAbsence[6]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(8),nomnreAbsence[7]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(9),nomnreAbsence[8]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(10),nomnreAbsence[9]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(11),nomnreAbsence[10]));
        series.getData().add(new XYChart.Data( GUIutils.getMonthFromInt(12),nomnreAbsence[11]));

        lineChart.getData().add(series);
        return lineChart;



    }

    private PieChart LoadStatistiqueToPieChart(int nombreDesHomme, int nombreDesFamme, Promo promo){

        PieChart pieChart =new PieChart();

        ObservableList<PieChart.Data> datas = FXCollections.observableArrayList(
                new PieChart.Data("Homme",nombreDesHomme),
                new PieChart.Data("Famme",nombreDesFamme));
        datas.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName()," ", data.pieValueProperty(), " Etudiant"
                        )
                )
        );
        pieChart.setData(datas);
        pieChart.setTitle("Statistique De Sexe de "+promo.getDesignation());
        return pieChart;
    }
    public void creatStatistiqueStage(PieChart pieChart,LineChart lineChart) throws IOException {
        Stage statistiquesstage = new Stage();
        statistiquesstage.setTitle("Stistique");
        statistiquesstage.getIcons().add(new Image("Images/images.jpg"));

        SplitPane splitPane = new SplitPane();
        BorderPane borderPane = new BorderPane();
        BorderPane borderPane1 =new BorderPane();
        splitPane.getItems().add(0,borderPane);
        splitPane.getItems().add(1,borderPane1);



        Scene scene = new Scene(splitPane, 800, 700);



        borderPane1.setCenter(lineChart);
        Button ExportLineChart =new Button("Imprimer");
        ExportLineChart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lineChart.setAnimated(false);
                DirectoryChooser directoryChooser= new DirectoryChooser();
                File file = directoryChooser.showDialog(null);
                saveAsPng(lineChart, file.getAbsolutePath()+"//"+lineChart.getTitle()+".png");


            }
        });
        HBox hBox =new HBox();
        hBox.getChildren().add(ExportLineChart);
        hBox.setAlignment(Pos.CENTER);
        ExportLineChart.setAlignment(Pos.CENTER);
        borderPane1.setBottom(hBox);


        borderPane.setCenter(pieChart);
        Button exportPieChart =new Button("Imprimer");
        exportPieChart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pieChart.setAnimated(false);
                DirectoryChooser directoryChooser= new DirectoryChooser();
                File file = directoryChooser.showDialog(null);
                saveAsPng(pieChart, file.getAbsolutePath()+"//"+pieChart.getTitle()+".png");

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
    public void saveAsPng(LineChart lineChart, String path) {
        WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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