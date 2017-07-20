package GUIClasses;

import FirebaseClasses.DbOperations;
import PoiClasses.XSSFileExchanger;
import StructureClasses.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.formula.functions.IDStarAlgorithm;
import org.apache.poi.ss.formula.functions.T;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class EtudiantsController implements Initializable{

    @FXML
    private ChoiceBox<Cycle> cyclesChoiceBox;
    @FXML
    private ChoiceBox<Filliere> fillieresChoiceBox;
    @FXML
    private ChoiceBox<Specialite> specialitesChoiceBox;
    @FXML
    private ChoiceBox<Promo> promosChoiceBox;
    @FXML
    private ChoiceBox<Section> sectionsChoiceBox;
    @FXML
    private ChoiceBox<Groupe> groupesChoiceBox;
    @FXML
    private TableView<Etudiant> etudiantsTableView;
    @FXML
    private TextField nomTextField;
    @FXML
    private TextField prenomTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private ChoiceBox<Character> sexeChoiceBox;
    @FXML
    private CheckBox modifierCheck;

    public void initialize(URL location, ResourceBundle resources) {

        //remplissage du choiceBox sexe

        sexeChoiceBox.getItems().addAll(Personne.FEMME, Personne.HOMME);

        //

        // remplissage des choiceBox de l'interface

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
                if(specialitesChoiceBox.getSelectionModel().getSelectedItem()!=null)
                    loadPromosToChoiceBox();
            }
        });
        promosChoiceBox.valueProperty().addListener(new ChangeListener<Promo>() {
            public void changed(ObservableValue<? extends Promo> observable, Promo oldValue, Promo newValue) {
                if(promosChoiceBox.getSelectionModel().getSelectedItem()!=null)
                    loadSectionsToChoiceBox();
            }
        });
        sectionsChoiceBox.valueProperty().addListener(new ChangeListener<Section>() {
            public void changed(ObservableValue<? extends Section> observable, Section oldValue, Section newValue) {
                if(sectionsChoiceBox.getSelectionModel().getSelectedItem()!=null)
                    loadGroupesToChoiceBox();
            }
        });
        groupesChoiceBox.valueProperty().addListener(new ChangeListener<Groupe>() {
            public void changed(ObservableValue<? extends Groupe> observable, Groupe oldValue, Groupe newValue) {
                if(groupesChoiceBox.getSelectionModel().getSelectedItem()!=null)
                    loadEtudiantsToTableView();

            }
        });
        //

        /*
            l'Action du checkBox Modifier
                qui permet de modifier les atribus d'un etudiant
                                                        */

        modifierCheck.setOnAction(event -> {
            if(!modifierCheck.isSelected())
                modifierCheck.setText("Modifier");
            else
                modifierCheck.setText("Selectionez l'étudiant dans la table");


        });

        //

        //le chiox et detection de l'etudiant a modifier


        etudiantsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                Etudiant etudiant = etudiantsTableView.getSelectionModel().getSelectedItem();
                if(modifierCheck.isSelected()){
                    consulter(etudiant);
                }
            }
        });

    }

    private void loadCyclesToChoiceBox() {


        String[] path = {DbOperations.CYCLES.substring(1)};
        GUIutils.loadChildrenToChoiceBox(cyclesChoiceBox, path, Structure.class, Cycle.class);
    }


    /*private void loadFillieresToChoiceBox() {

        specialitesChoiceBox.getItems().clear();
        promosChoiceBox.getItems().clear();
        sectionsChoiceBox.getItems().clear();
        groupesChoiceBox.getItems().clear();
        etudiantsTableView.getColumns().clear();

        String cycleId = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, cycleId);

        String[] path = {DbOperations.CYCLES.substring(1), cycleId};
        GUIutils.loadChildrenToChoiceBox(fillieresChoiceBox, path, Cycle.class, Filliere.class);
    }

    private void loadSpecialiteToChoiceBox() {

        promosChoiceBox.getItems().clear();
        sectionsChoiceBox.getItems().clear();
        groupesChoiceBox.getItems().clear();
        etudiantsTableView.getColumns().clear();

        if (fillieresChoiceBox.getSelectionModel().getSelectedItem().hasSpecialites()) {

            String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();

            String[] path = {DbOperations.FILIERE_SPECIALITES.substring(1), idFilliere};
            GUIutils.loadChildrenToChoiceBox(specialitesChoiceBox, path, Filliere.class, Specialite.class);
        }

    }*/

    private void loadFillieresToChoiceBox() {

        specialitesChoiceBox.getItems().clear();
        promosChoiceBox.getItems().clear();
        sectionsChoiceBox.getItems().clear();
        groupesChoiceBox.getItems().clear();
        etudiantsTableView.getColumns().clear();

        String cycleId = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, cycleId);

        String[] path ={DbOperations.CYCLES.substring(1), cycleId};

        GUIutils.loadChildrenToChoiceBox(fillieresChoiceBox, path, Cycle.class, Filliere.class);
    }
    private void loadSpecialiteToChoiceBox() {

        promosChoiceBox.getItems().clear();
        sectionsChoiceBox.getItems().clear();
        groupesChoiceBox.getItems().clear();
        etudiantsTableView.getColumns().clear();


        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, idFilliere);
        String[] path = {DbOperations.FILIERE_SPECIALITES.substring(1), idFilliere};
        GUIutils.loadChildrenToChoiceBox(specialitesChoiceBox, path, Filliere.class, Specialite.class);
    }

    private void loadPromosToChoiceBox() {

        sectionsChoiceBox.getItems().clear();
        groupesChoiceBox.getItems().clear();
        etudiantsTableView.getColumns().clear();

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere);
        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere};
        GUIutils.loadChildrenToChoiceBox(promosChoiceBox, path, Section.class, Promo.class);
    }

    private void loadSectionsToChoiceBox() {

        groupesChoiceBox.getItems().clear();
        etudiantsTableView.getColumns().clear();

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idPromo = promosChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere, idPromo);

        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo};


        GUIutils.loadChildrenToChoiceBox(sectionsChoiceBox, path, Promo.class, Section.class);
    }

    private void loadGroupesToChoiceBox() {

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idPromo = promosChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idSection = sectionsChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere, idPromo, idSection);

        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo, idSection};
        GUIutils.loadChildrenToChoiceBox(groupesChoiceBox, path, Section.class, Groupe.class);
    }

    private void loadEtudiantsToTableView() {

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idPromo = promosChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idSection = sectionsChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idGroupe = groupesChoiceBox.getSelectionModel().getSelectedItem().getId();

        String[] attributs = {"nom", "prenom", "sexe", "email", "nbAbsences"};
        String[] columnsTitle = {"Nom", "Prénom", "Sexe", "Email", "Nombre d'absences"};

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere, idPromo, idSection, idGroupe);

        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo, idSection, idGroupe};
        GUIutils.loadChildrenToTableView(etudiantsTableView, path, Groupe.class, Etudiant.class,
                attributs, columnsTitle);

    }

    private void consulter(Etudiant etudiant) {

        HBox all = new HBox(20);
        Stage stage=new Stage();
        stage.setTitle("Fiche d'étudiant");
        Scene scene = new Scene(all, 300, 200);
        scene.getStylesheets().add(Main.class.getResource("/bootstrap3.css").toExternalForm());
        stage.setScene(scene);

        VBox profile = new VBox(10);
        VBox info = new VBox(10);
        javafx.scene.image.ImageView photo = null;
        try {
            photo = new javafx.scene.image.ImageView(base64ToImage(etudiant.getImageBase64()));
            photo.setFitHeight(100);
            photo.setFitWidth(100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Button changePhoto = new Button("Changer");
        Button ok = new Button("Ok");
        TextField nom = new TextField(etudiant.getNom());
        TextField prenom = new TextField(etudiant.getPrenom());
        TextField email = new TextField(etudiant.getEmail());

        profile.getChildren().addAll(photo,changePhoto);
        info.getChildren().addAll(nom, prenom, email, ok);

        all.getChildren().addAll(profile, info);

        changePhoto.setOnAction(event -> {
            etudiant.setImageBase64(importPhoto());
        });

        ok.setOnAction(event->{
            etudiant.setNom(nom.getText());
            etudiant.setPrenom(prenom.getText());
            etudiant.setEmail(email.getText());
            ajouterModifier(etudiant);
            stage.close();

        });

        stage.show();


    }

    private void ajouterModifier(Etudiant etudiant) {

        HashMap<String, HashMap<Absence, Justification>> result = new HashMap<>();


        String[] path = {DbOperations.CYCLES.substring(1), etudiant.getIdCycle(), etudiant.getIdFilliere(), etudiant.getIdPromo(),
                etudiant.getIdSection(), etudiant.getIdGroupe(), etudiant.getId()};

        HashMap<String, Module> module = DbOperations.getChildren(Etudiant.class, Module.class, path);

        for (Map.Entry<String, Module> moduleEntry : module.entrySet()) {


            path = new String[]{DbOperations.CYCLES.substring(1), etudiant.getIdCycle(), etudiant.getIdFilliere(), etudiant.getIdPromo(),
                    etudiant.getIdSection(), etudiant.getIdGroupe(), etudiant.getId(), moduleEntry.getKey()};

            HashMap<String, Absence> absenceHashMap = DbOperations.getChildren(Module.class, Absence.class, path);

            HashMap<Absence, Justification> demiResult=new HashMap<>();
            for (Map.Entry<String, Absence> absenceEntry : absenceHashMap.entrySet()) {

                path = new String[]{DbOperations.CYCLES.substring(1), etudiant.getIdCycle(), etudiant.getIdFilliere(), etudiant.getIdPromo(),
                        etudiant.getIdSection(), etudiant.getIdGroupe(), etudiant.getId(), moduleEntry.getKey(), absenceEntry.getKey()};

                HashMap<String, Justification> justificationHashMap = DbOperations.getChildren(Absence.class, Justification.class, path );
                demiResult.put(absenceEntry.getValue(),justificationHashMap.get(0));
            }
            result.put(moduleEntry.getKey(), demiResult);
        }

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(etudiant);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);

        for (Map.Entry<String, HashMap<Absence, Justification>> entry : result.entrySet()) {

            for (Map.Entry<Absence, Justification> entry1 : entry.getValue().entrySet()) {

                entry1.getKey().ajouterDb();
                if(entry1.getValue()!=null)
                    entry1.getValue().ajouterJustification(entry1.getKey());
            }
        }
    }

    public String importPhoto(){
        try {

            String imageString = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String extension = file.getName().substring(file.getName().indexOf(".") + 1,file.getName().length());

                try {

                    BufferedImage image = ImageIO.read(file);
                    BufferedImage img = resize(image, 400, 400);
                    ImageIO.write(img, extension, bos);
                    byte[] imageBytes = bos.toByteArray();

                    BASE64Encoder encoder = new BASE64Encoder();
                    imageString = encoder.encode(imageBytes);

                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Personne.DEFAULT_IMAGD_BASE64_STRING = imageString;
                Alert dialogE = new Alert(Alert.AlertType.INFORMATION);
                dialogE.setTitle("Succés");
                dialogE.setHeaderText(null);
                dialogE.setContentText("Photo importer avec succés ");
                dialogE.showAndWait();

            } else
                throw new NullPointerException();

        }catch (NullPointerException e){
            Alert dialogE = new Alert(Alert.AlertType.ERROR);
            dialogE.setTitle("Error");
            dialogE.setHeaderText(null);
            dialogE.setContentText("Il faut choisir une photo");
            dialogE.showAndWait();

        }
        return Personne.DEFAULT_IMAGD_BASE64_STRING ;

    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    public void ajouterEtudiant(ActionEvent actionEvent) {

        Etudiant etudiant = new Etudiant(nomTextField.getText(), prenomTextField.getText(),
                sexeChoiceBox.getSelectionModel().getSelectedItem());
        etudiant.setId(GUIutils.generateId());
        etudiant.setIdCycle(cyclesChoiceBox.getSelectionModel().getSelectedItem().getId());
        etudiant.setIdFilliere(fillieresChoiceBox.getSelectionModel().getSelectedItem().getId());
        etudiant.setIdSpecialite(specialitesChoiceBox.getSelectionModel().getSelectedItem().getId());
        etudiant.setIdPromo(promosChoiceBox.getSelectionModel().getSelectedItem().getId());
        etudiant.setIdSection(sectionsChoiceBox.getSelectionModel().getSelectedItem().getId());
        etudiant.setIdGroupe(groupesChoiceBox.getSelectionModel().getSelectedItem().getId());
        etudiant.setEmail(emailTextField.getText());

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(etudiant);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadEtudiantsToTableView();
                nomTextField.clear();
                prenomTextField.clear();
                emailTextField.clear();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);
    }

    public void supprimerEtudiants(ActionEvent actionEvent){

        GUIutils.supprmerObj(etudiantsTableView);
        DbGestionController.actualiserLaBaseDeDonnee();
        loadEtudiantsToTableView();
    }

    public void exporterListEtudiants(){

        XSSFileExchanger xssFileExchanger=new XSSFileExchanger();

        DirectoryChooser directoryChooser= new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        String fileName = sectionsChoiceBox.getSelectionModel().getSelectedItem().getDesignation()+" "+
                groupesChoiceBox.getSelectionModel().getSelectedItem().getDesignation();

            try {
                xssFileExchanger.excelCreate(file.getAbsolutePath(), fileName, etudiantsTableView);
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

    public void importerListEtudiants(){

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idSpecialite= specialitesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idPromo = promosChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idSection = sectionsChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idGroupe = groupesChoiceBox.getSelectionModel().getSelectedItem().getId();

        XSSFileExchanger xssFileExchanger=new XSSFileExchanger();

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        HashMap<String, HashMap<String, Object>> atrebutes = null;

        try {
            atrebutes = xssFileExchanger.excelRead(file.getAbsolutePath());
            for (Map.Entry<String, HashMap<String, Object>> entry : atrebutes.entrySet()) {

                Etudiant etudiant = new Etudiant();
                etudiant.setId(GUIutils.generateId());
                etudiant.setIdCycle(idCycle);
                etudiant.setIdFilliere(idFilliere);
                etudiant.setIdSpecialite(idSpecialite);
                etudiant.setIdPromo(idPromo);
                etudiant.setIdSection(idSection);
                etudiant.setIdGroupe(idGroupe);

                etudiant.setNom((String) entry.getValue().get("nom"));
                etudiant.setPrenom((String) entry.getValue().get("prenom"));
                etudiant.setSexe(((String) entry.getValue().get("sexe")).charAt(0));
                etudiant.setEmail((String) entry.getValue().get("email"));
                etudiant.setNbAbsences(0);

                ajouterImport(etudiant);

            }


            etudiantsTableView.getItems().clear();
            loadEtudiantsToTableView();

        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException E ){

            Alert dialogE = new Alert(Alert.AlertType.ERROR);
            dialogE.setTitle("Error");
            dialogE.setHeaderText(null);
            dialogE.setContentText("Il faut choisir un ficier xlsx");
            dialogE.showAndWait();

        }

    }

    private void ajouterImport(Etudiant etudiant) {

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(etudiant);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                DbGestionController.actualiserLaBaseDeDonnee();
                loadEtudiantsToTableView();
                GUIutils.taskIndicatorForm.getDialogStage().close();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);
    }

    private Image base64ToImage(String value) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] imgBytes = decoder.decodeBuffer(value);
        return new Image(new ByteArrayInputStream(imgBytes));

    }

}