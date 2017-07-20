package GUIClasses;


import JSONClasses.JSONException;
import PoiClasses.XSSFileExchanger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import FirebaseClasses.DbOperations;
import StructureClasses.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class AbsencesController implements Initializable {

    @FXML
    private ChoiceBox<Seance> seancesChoiceBox;
    @FXML
    private ChoiceBox<Module> modulesChoiceBox;
    @FXML
    private ChoiceBox<Section> sectionsChoiceBox;
    @FXML
    private ChoiceBox<Groupe> groupesChoiceBox;
    @FXML
    private TableView<Etudiant> absencesTableView;
    @FXML
    private TextArea motifTextArea;
    @FXML
    Button go;
    @FXML
    private RadioButton wayRadioButtonGroupes;
    @FXML
    private RadioButton wayRadioButtonSections;

    /*    les Statistiques  */

    private final NumberAxis yAxis = new NumberAxis();
    private final CategoryAxis xAxis = new CategoryAxis();
    private LineChart<String,Number> lineChartStatistique =null;
    HashMap<Seance,HashMap<String,Absence>> statistique;

    /*       */


    public void initialize(URL location, ResourceBundle resources) {


        //RadioButtonConfiguration

        go.setDisable(true);
        wayRadioButtonGroupes.setOnAction(e -> {
            groupesChoiceBox.setDisable(false);
            wayRadioButtonSections.setSelected(false);
            wayRadioButtonGroupes.setDisable(true);
            wayRadioButtonSections.setDisable(false);
            go.setDisable(false);
        });
        wayRadioButtonSections.setOnAction(e -> {
            groupesChoiceBox.setDisable(true);
            wayRadioButtonGroupes.setSelected(false);
            wayRadioButtonGroupes.setDisable(false);
            wayRadioButtonSections.setDisable(true);
            go.setDisable(false);
        });

        //

        // remplissage des choiceBox de l'interface

        loadModulesToChoiceBox();
        modulesChoiceBox.valueProperty().addListener(new ChangeListener<Module>() {
            public void changed(ObservableValue<? extends Module> observable, Module oldValue, Module newValue) {
                loadSectionsToChoiceBox();
            }
        });


        sectionsChoiceBox.valueProperty().addListener(new ChangeListener<Section>() {
            public void changed(ObservableValue<? extends Section> observable, Section oldValue, Section newValue) {
                loadGroupesToChoiceBox();
            }
        });


        //
    }

    private void loadSeanceToChoiceBox() {

        Groupe groupe =groupesChoiceBox.getSelectionModel().getSelectedItem();
        Module module= modulesChoiceBox.getSelectionModel().getSelectedItem();

        String idEnseignant;
        String idModule = module.getId();

        if (wayRadioButtonGroupes.isDisable()) {

            idEnseignant = getEnseignantsId(groupe, module);
            String idGroupes = groupe.getId();
            String[] path = {DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant, idModule, DbOperations.GROUPES.substring(1)
                    , idGroupes};
            GUIutils.loadChildrenToChoiceBox(seancesChoiceBox, path, Groupe.class, Seance.class);
        } else {
            Section section= sectionsChoiceBox.getSelectionModel().getSelectedItem();
            idEnseignant = getEnseignantsId(section, module);

            String idSections = section.getId();
            String[] path = {DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant, idModule, DbOperations.SECTIONS.substring(1)
                    , idSections};
            GUIutils.loadChildrenToChoiceBox(seancesChoiceBox, path, Section.class, Seance.class);

        }

    }

    private void loadModulesToChoiceBox() {

        String[] path = {DbOperations.MODULE_ENSEIGNANTS.substring(1)};
        GUIutils.loadChildrenToChoiceBox(modulesChoiceBox, path, Structure.class, Module.class);
    }

    private void loadSectionsToChoiceBox() {

        Module module =modulesChoiceBox.getSelectionModel().getSelectedItem();
        String idCycle = module.getIdCycle();
        String idFilliere=module.getIdFilliere();
        String idPromo=module.getIdPromo();

        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo};

        GUIutils.loadChildrenToChoiceBox(sectionsChoiceBox, path, Promo.class, Section.class);
    }

    private void loadGroupesToChoiceBox() {
        Module module =modulesChoiceBox.getSelectionModel().getSelectedItem();
        String idCycle = module.getIdCycle();
        String idFilliere=module.getIdFilliere();
        String idPromo=module.getIdPromo();
        String idSection= sectionsChoiceBox.getSelectionModel().getSelectedItem().getId();

        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo, idSection};
        GUIutils.loadChildrenToChoiceBox(groupesChoiceBox, path, Section.class, Groupe.class);
    }

    public void ajouterJustification() {

        Justification justification = new Justification();

        try {
            Etudiant etudiant = absencesTableView.getSelectionModel().getSelectedItem();
            if (etudiant == null)
                throw new Exception();

            Seance seance = seancesChoiceBox.getSelectionModel().getSelectedItem();

            justification.setIdCycle(etudiant.getIdCycle());
            justification.setIdFilliere(etudiant.getIdFilliere());
            justification.setIdSpecialite(etudiant.getIdSpecialite());
            justification.setIdPromo(etudiant.getIdPromo());
            justification.setIdSection(etudiant.getIdSection());
            justification.setIdGroupe(etudiant.getIdGroupe());
            justification.setIdEtudiant(etudiant.getId());
            justification.setIdEnseignant(seance.getIdEnseignant());
            justification.setIdModule(seance.getIdModule());
            justification.setIdSeance(seance.getId());
            justification.setMotif(motifTextArea.getText());
            justification.setId(GUIutils.generateId());

            try {
                if (getIdAbsenceAJustifier(seance)!=null) {

                    Absence absence=getIdAbsenceAJustifier(seance);
                    justification.setIdAbsence(absence.getId());

                    final Task<Void> justifierTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {

                            justification.ajouterJustification(absence);
                            DbGestionController.actualiserLaBaseDeDonnee();
                            loadAbsenceToTableView();

                            return null;
                        }
                    };
                    justifierTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        public void handle(WorkerStateEvent event) {

                            GUIutils.taskIndicatorForm.getDialogStage().close();
                        }
                    });
                    GUIutils.taskIndicatorForm.activateProgressBar(justifierTask);
                    GUIutils.taskIndicatorForm.getDialogStage().show();

                    GUIutils.executor.execute(justifierTask);
                    loadAbsenceToTableView();
                } else {
                    throw new Exception();
                }

            } catch (Exception e) {
                Alert dialogE = new Alert(Alert.AlertType.ERROR);
                dialogE.setTitle("Error");
                dialogE.setHeaderText(null);
                dialogE.setContentText("Error : Vous ne pouvez pas justifier une absence qui n'existe pas !");
                dialogE.showAndWait();
            }

        }catch (Exception e){
            Alert dialogE = new Alert(Alert.AlertType.INFORMATION);
            dialogE.setTitle("Eroor");
            dialogE.setHeaderText(null);
            dialogE.setContentText("Error : Selectionnez un étudiant dans la table !");
            dialogE.showAndWait();
        }
    }

    private Absence getIdAbsenceAJustifier(Seance seance) {

        HashMap<String, Absence> absenceHashMap = getEtudiantAbsencesModule();

        Absence returnabs=null;

        for (Map.Entry<String, Absence> absenceEntry : absenceHashMap.entrySet()){
            if(absenceEntry.getValue().getIdSeance().equals(seance.getId())){
                returnabs = absenceEntry.getValue();
                break;
            }
        }

        return returnabs;
    }

    private HashMap<String, Absence> getEtudiantAbsencesModule() {

        HashMap<String, Absence> returnHashMap;
        Etudiant etudiant = absencesTableView.getSelectionModel().getSelectedItem();
        String idModule = modulesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String[] path={DbOperations.CYCLES.substring(1), etudiant.getIdCycle(), etudiant.getIdFilliere(),
                etudiant.getIdPromo(), etudiant.getIdSection(), etudiant.getIdGroupe(),
                etudiant.getId(), idModule};

        returnHashMap = DbOperations.getChildren(Module.class, Absence.class, path);

        return returnHashMap;
    }

    private HashMap<String, Seance> getSeanceHashMap() {

        String idEnseignant;

        Module module = modulesChoiceBox.getSelectionModel().getSelectedItem();
        Groupe groupe= groupesChoiceBox.getSelectionModel().getSelectedItem();

        String idModule = module.getId();

        if (wayRadioButtonGroupes.isDisable()) {

            idEnseignant = getEnseignantsId(groupe, module);
            String idGroupes = groupe.getId();
            String[] path = {DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant, idModule, DbOperations.GROUPES.substring(1)
                    , idGroupes};
            return DbOperations.getChildren(Groupe.class, Seance.class, path);

        } else {

            Section section = sectionsChoiceBox.getSelectionModel().getSelectedItem();
            idEnseignant = getEnseignantsId(section, module);
            String idSections = section.getId();
            String[] path = {DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant, idModule, DbOperations.SECTIONS.substring(1)
                    , idSections};
            return DbOperations.getChildren(Section.class, Seance.class, path);
        }
    }

    private String getEnseignantsId(Object object, Module module) {

        Enseignant enseignant= null;

        if(object instanceof Groupe){

            String[] path ={DbOperations.GROUPE_MODULES.substring(1), ((Groupe) object).getId(), module.getId()};

            try{
                enseignant = DbOperations.getChildren(Module.class, Enseignant.class, path).entrySet().iterator().next().getValue();

            }catch(JSONException e){
                e.printStackTrace();
            }

        }else{
            String[] path ={DbOperations.SECTION_MODULES.substring(1), ((Section) object).getId(), module.getId()};
            try{
                enseignant = DbOperations.getChildren(Module.class, Enseignant.class, path).entrySet().iterator().next().getValue();

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return enseignant.getId();
    }

    private HashMap<Etudiant, HashMap<String, Absence>> getEtudiantsAbsences(){

        HashMap<Etudiant, HashMap<String, Absence>> returnHashMap=new HashMap<>();
        HashMap<String, Etudiant>etudiantHashMap;

        if(!groupesChoiceBox.isDisable())
            etudiantHashMap = getGroupeEtudiants();
        else
            etudiantHashMap = getSectionEtudiants();

        HashMap<String, Absence> absenceHashMap;

        String idCycle=sectionsChoiceBox.getSelectionModel().getSelectedItem().getIdCycle();
        String idFilliere=sectionsChoiceBox.getSelectionModel().getSelectedItem().getIdFilliere();
        String idPromo=sectionsChoiceBox.getSelectionModel().getSelectedItem().getIdPromo();
        String idSection=sectionsChoiceBox.getSelectionModel().getSelectedItem().getId();

        for (Map.Entry<String, Etudiant> etudiantEntry: etudiantHashMap.entrySet()) {

            String idEtudiant= etudiantEntry.getValue().getId();
            String idModule=modulesChoiceBox.getSelectionModel().getSelectedItem().getId();
            String idGroupe=etudiantEntry.getValue().getIdGroupe();
            String[] pathToAbsence= {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo, idSection, idGroupe,
                        idEtudiant, idModule};
            try {
                absenceHashMap = DbOperations.getChildren(Module.class, Absence.class, pathToAbsence);
            }catch(JSONException e){
                absenceHashMap = null;
                e.printStackTrace();
            }
            returnHashMap.put(etudiantEntry.getValue(), absenceHashMap);
        }
        return returnHashMap;
    }

    private HashMap<String, Etudiant> getGroupeEtudiants() {

        String idCycle=groupesChoiceBox.getSelectionModel().getSelectedItem().getIdCycle();
        String idFilliere=groupesChoiceBox.getSelectionModel().getSelectedItem().getIdFilliere();
        String idPromo=groupesChoiceBox.getSelectionModel().getSelectedItem().getIdPromo();
        String idSection=groupesChoiceBox.getSelectionModel().getSelectedItem().getIdSection();
        String idGroupe=groupesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String[] pathToEtudiant= {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo, idSection, idGroupe};
        return DbOperations.getChildren(Groupe.class, Etudiant.class, pathToEtudiant);
    }

    private HashMap<String, Etudiant> getSectionEtudiants() {

        HashMap<String, Etudiant> returnHashMap = new HashMap<>();

        String idCycle=sectionsChoiceBox.getSelectionModel().getSelectedItem().getIdCycle();
        String idFilliere=sectionsChoiceBox.getSelectionModel().getSelectedItem().getIdFilliere();
        String idPromo=sectionsChoiceBox.getSelectionModel().getSelectedItem().getIdPromo();
        String idSection=sectionsChoiceBox.getSelectionModel().getSelectedItem().getId();

        String[] pathToGroupes= {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo, idSection};
        HashMap<String, Groupe> groupeHashMap = DbOperations.getChildren(Section.class, Groupe.class, pathToGroupes);
        for (Map.Entry<String, Groupe> entry : groupeHashMap.entrySet()) {

            String idGroupe = entry.getValue().getId();
            String[] pathToEtudiant= {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo, idSection, idGroupe};

            HashMap<String, Etudiant> etudiantHashMap = DbOperations.getChildren(Groupe.class, Etudiant.class, pathToEtudiant);

            for (Map.Entry<String, Etudiant> etudiantEntry : etudiantHashMap.entrySet()) {
                returnHashMap.put(etudiantEntry.getKey(), etudiantEntry.getValue());
            }

        }

        return returnHashMap;
    }

    private HashMap<Seance, HashMap<String, Absence>> getGroupesOuSectionsAbsence(){

        HashMap<Seance, HashMap<String, Absence>> returnHashMap = new HashMap<>();
        HashMap<String, Seance> seanceHashMap;
        seanceHashMap= getSeanceHashMap();
        HashMap<String, Absence> absenceHashMap;

        for (Map.Entry<String, Seance> seanceEntry: seanceHashMap.entrySet()) {

            String idEnseignant= seanceEntry.getValue().getIdEnseignant();
            String idModule= seanceEntry.getValue().getIdModule();
            String idGroupe= seanceEntry.getValue().getIdGroupe();

            if (wayRadioButtonGroupes.isDisable()) {
                    String[] pathToAbsences = {DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant, idModule,
                            DbOperations.GROUPES.substring(1), idGroupe, seanceEntry.getValue().getId()};

                absenceHashMap= DbOperations.getChildren(Seance.class, Absence.class, pathToAbsences);
                returnHashMap.put(seanceEntry.getValue(), absenceHashMap);

            } else {
                String idSection = sectionsChoiceBox.getSelectionModel().getSelectedItem().getId();
                String[] pathToAbsence = {DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant, idModule,
                        DbOperations.SECTIONS.substring(1), idSection, seanceEntry.getValue().getId()};

                absenceHashMap= DbOperations.getChildren(Seance.class, Absence.class, pathToAbsence);
                returnHashMap.put(seanceEntry.getValue(), absenceHashMap);
            }
        }
        return returnHashMap;
    }

    private HashMap<Etudiant, HashMap<Seance, Absence>> getEtudiantAbsences(){

        HashMap<Etudiant, HashMap<String, Absence>> etudiantAbsenceHashMap=getEtudiantsAbsences();
        HashMap<Seance, HashMap<String, Absence>> groupeAbsences= getGroupesOuSectionsAbsence();
        HashMap<Etudiant, HashMap<Seance, Absence>> etudiantsHashMap =new HashMap<>();
        HashMap<Seance, Absence> valueHashMap=new HashMap<>();

        for (Map.Entry<Etudiant, HashMap<String, Absence>> etudiantEntry: etudiantAbsenceHashMap.entrySet()) {

            for (Map.Entry<Seance, HashMap<String, Absence>> seanceEntry: groupeAbsences.entrySet()) {

                for (Map.Entry<String, Absence> absenceEntry : seanceEntry.getValue().entrySet()) {

                    if(absenceEntry.getValue().getIdEtudiant().equals(etudiantEntry.getKey().getId()))
                    {
                        valueHashMap.put(seanceEntry.getKey(), absenceEntry.getValue());
                        break;
                    }
                }
                if (!valueHashMap.containsKey(seanceEntry.getKey()))
                    valueHashMap.put(seanceEntry.getKey(), null);

            }
            etudiantsHashMap.put(etudiantEntry.getKey(),valueHashMap);
            valueHashMap=new HashMap<>();

        }

        return etudiantsHashMap;
    }

    private void loadAbsenceToTableView(){

        absencesTableView.getColumns().clear();

        TableColumn<Etudiant, String> etudiantTableColumn = new TableColumn<>("Etudiant");
        TableColumn<Etudiant, String> nomTableColumn = new TableColumn<>("Nom");
        TableColumn<Etudiant, String> prenomTableColumn = new TableColumn<>("Prenom");
        nomTableColumn.setCellValueFactory(new PropertyValueFactory<Etudiant, String>("nom"));
        prenomTableColumn.setCellValueFactory(new PropertyValueFactory<Etudiant, String>("prenom"));
        etudiantTableColumn.getColumns().addAll(nomTableColumn, prenomTableColumn);

        absencesTableView.getColumns().add(etudiantTableColumn);
        setSeanceColumnsAndGettingObservableList();

    }

    private void setSeanceColumnsAndGettingObservableList() {

        HashMap<Etudiant, HashMap<Seance, Absence>> etudiantsHashMap = getEtudiantAbsences();

            HashMap<String, Etudiant> etudiantHashMap;
        if(!groupesChoiceBox.isDisable())
            etudiantHashMap = getGroupeEtudiants();
        else
            etudiantHashMap = getSectionEtudiants();

        HashMap<String, Seance> seanceHashMap = getSeanceHashMap();
        ObservableList<Etudiant> etudiantsObservableList = FXCollections.observableArrayList();

        for (Map.Entry<String, Etudiant> etudiantsEntry : etudiantHashMap.entrySet() ) {
            etudiantsObservableList.add(etudiantsEntry.getValue());

        }

        absencesTableView.setItems(etudiantsObservableList);

        for (Map.Entry<String, Seance> columnEntry : seanceHashMap.entrySet() ) {

            TableColumn<Etudiant, String> etatColumn=new TableColumn<>(columnEntry.getValue().toString());
            etatColumn.setPrefWidth(200);

            etatColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Etudiant, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Etudiant, String> p) {

                    HashMap<Seance, Absence> absenceHashMap=new HashMap<>();
                    for (Map.Entry<Etudiant, HashMap<Seance, Absence>> etudiantsEntry : etudiantsHashMap.entrySet() ) {
                        if(etudiantsEntry.getKey().equals(p.getValue())){

                            absenceHashMap=etudiantsEntry.getValue();
                            break;
                        }
                    }

                    return new ReadOnlyObjectWrapper(p.getValue().etat(columnEntry.getValue(), absenceHashMap));
                }
            });

            absencesTableView.getColumns().add(etatColumn);
        }
    }

    @FXML
    private void exporter(){

        XSSFileExchanger xssFileExchanger=new XSSFileExchanger();

        DirectoryChooser directoryChooser= new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        String fileName;
        if(wayRadioButtonGroupes.isSelected())
            fileName = groupesChoiceBox.getSelectionModel().getSelectedItem().getDesignation()+" "+
                    modulesChoiceBox.getSelectionModel().getSelectedItem().getDesignation();
        else
            fileName = sectionsChoiceBox.getSelectionModel().getSelectedItem().getDesignation()+" "+
                    modulesChoiceBox.getSelectionModel().getSelectedItem().getDesignation();


        try {
            xssFileExchanger.exportAbsences(file.getAbsolutePath(), fileName, absencesTableView);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Alert dialogE = new Alert(Alert.AlertType.ERROR);
            dialogE.setTitle("Error");
            dialogE.setHeaderText(null);
            dialogE.setContentText("Il faut choisir un répertoire");
            dialogE.showAndWait();

        }
    }
    @FXML
    private void go(){
        loadSeanceToChoiceBox();
        loadAbsenceToTableView();
        statistique = getGroupesOuSectionsAbsence();
    }

    public void statistique(){

        lineChartStatistique =new LineChart<String, Number>(xAxis,yAxis);

        // Création des séries.
        String titre="Statistique des absences"+" de groupe "+groupesChoiceBox.getSelectionModel().getSelectedItem()+" module "+modulesChoiceBox.getSelectionModel().getSelectedItem();
        lineChartStatistique.setTitle(titre);
        xAxis.setLabel("Sceance");
        yAxis.setLabel("Absence");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Absence ");
        XYChart.Series series2=new XYChart.Series();
        series2.setName("Absence justifier");

        for (Map.Entry<Seance,HashMap<String,Absence>> stat : statistique.entrySet() ){

            series1.getData().add(new XYChart.Data(stat.getKey().getDate()+" "+stat.getKey().getHeureDebut()+"h",stat.getValue().size()));
        }

        ArrayList<Absence> absencesJustifier;
        for (Map.Entry<Seance,HashMap<String,Absence>> stat : statistique.entrySet() ){

            absencesJustifier = new ArrayList<>();
            for (Map.Entry<String,Absence> absenceEntry : stat.getValue().entrySet() ){

                if (absenceEntry.getValue().getJustifier() == true)
                    absencesJustifier.add(absenceEntry.getValue());
            }
            series2.getData().add(new XYChart.Data(stat.getKey().getDate()+" "+stat.getKey().getHeureDebut()+"h",absencesJustifier.size()));

        }

        Stage stage =new Stage();
        BorderPane root=new BorderPane();
        HBox hBox=new HBox();
        Scene scene  = new Scene(root,350,350);
        // root.getChildren().add(lineChartStatistique);
        root.setCenter(lineChartStatistique);
        Button print=new Button();
        print.setText("Imprimer");
        root.setBottom(hBox);
        hBox.getChildren().add(print);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);

        Insets insets =new Insets(10);
        hBox.setPadding(insets);
        hBox.setSpacing(10);



        lineChartStatistique.getData().addAll(series1, series2);
        stage.setScene(scene);
        stage.show();

        print.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lineChartStatistique.setAnimated(false);
                XSSFileExchanger xssFileExchanger=new XSSFileExchanger();
                DirectoryChooser directoryChooser= new DirectoryChooser();
                File file = directoryChooser.showDialog(null);
                saveAsPng(lineChartStatistique, file.getAbsolutePath()+"//"+titre+".png");

            }
        });
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

}
