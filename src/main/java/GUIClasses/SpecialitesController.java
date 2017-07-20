package GUIClasses;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import FirebaseClasses.DbOperations;
import StructureClasses.Cycle;
import StructureClasses.Filliere;
import StructureClasses.Specialite;
import StructureClasses.Structure;

import java.net.URL;
import java.util.ResourceBundle;


public class SpecialitesController implements Initializable{

    @FXML
    private ChoiceBox<Cycle> cyclesChoiceBox;
    @FXML
    private ChoiceBox<Filliere> fillieresChoiceBox;
    @FXML
    private TableView<Specialite> specialitesTableView;
    @FXML
    private TextField designationTextField;

    public void initialize(URL location, ResourceBundle resources) {

        //activer la slection multiple
        specialitesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //remplissage des choiceBox de l'interface

        loadCyclesToChoiceBox();

        cyclesChoiceBox.valueProperty().addListener(new ChangeListener<Cycle>() {

            public void changed(ObservableValue<? extends Cycle> observable, Cycle oldValue, Cycle newValue) {

                loadFillieresToChoiceBox();
            }
        });
        fillieresChoiceBox.valueProperty().addListener(new ChangeListener<Filliere>() {

            public void changed(ObservableValue<? extends Filliere> observable, Filliere oldValue, Filliere newValue) {

                loadSpecialitesToTableView();
            }
        });
    }

    private void loadCyclesToChoiceBox() {

        String[] path = {DbOperations.CYCLES.substring(1)};

        GUIutils.loadChildrenToChoiceBox(cyclesChoiceBox, path, Structure.class, Cycle.class);
    }

    private void loadFillieresToChoiceBox() {

        specialitesTableView.getColumns().clear();

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle);
        String[] path = {DbOperations.CYCLES.substring(1), idCycle};
        GUIutils.loadChildrenToChoiceBox(fillieresChoiceBox, path, Cycle.class, Filliere.class);
    }
    private void loadSpecialitesToTableView() {

        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, idFilliere);

        String[] attributs = {"designation"};
        String[] columnsTitle = {"Designation"};
        String[] path = {DbOperations.FILIERE_SPECIALITES.substring(1), idFilliere};

        GUIutils.loadChildrenToTableView(specialitesTableView, path, Filliere.class, Specialite.class,
                attributs, columnsTitle);
    }

    public void ajouterSpecialite(ActionEvent actionEvent) {

        Specialite specialite = new Specialite(designationTextField.getText());
        specialite.setIdCycle(cyclesChoiceBox.getSelectionModel().getSelectedItem().getId());
        specialite.setIdFilliere(fillieresChoiceBox.getSelectionModel().getSelectedItem().getId());
        specialite.setId(GUIutils.generateId(specialite));

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(specialite);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadSpecialitesToTableView();
                designationTextField.clear();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);
    }
    public void supprimerSpecialites(ActionEvent actionEvent){

        GUIutils.supprmerObj(specialitesTableView);
        DbGestionController.actualiserLaBaseDeDonnee();
        loadSpecialitesToTableView();
    }
}