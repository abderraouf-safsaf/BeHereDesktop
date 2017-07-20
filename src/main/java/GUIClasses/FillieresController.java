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
import StructureClasses.Structure;

import java.net.URL;
import java.util.ResourceBundle;


public class FillieresController implements Initializable{

    @FXML
    private ChoiceBox<Cycle> cyclesChoiceBox;
    @FXML
    private TableView fillieresTableView;
    @FXML
    private TextField designationTextField;

    public void initialize(URL location, ResourceBundle resources) {


        //activer la selection multiple

        fillieresTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //

        // remplissage des choiceBox de l'interface

        loadCyclesToChoiceBox();

        cyclesChoiceBox.valueProperty().addListener(new ChangeListener<Cycle>() {

            public void changed(ObservableValue<? extends Cycle> observable, Cycle oldValue, Cycle newValue) {

                loadFillieresToTableView();
            }
        });
        //

    }
    private void loadCyclesToChoiceBox() {

        String[] path ={DbOperations.CYCLES.substring(1)};

        GUIutils.loadChildrenToChoiceBox(cyclesChoiceBox, path, Structure.class, Cycle.class);

    }
    private void loadFillieresToTableView() {

        String cycleId = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, cycleId);

        String[] attributs = {"designation"};
        String[] columnsTitle = {"Designation"};
        String[] path2 = {DbOperations.CYCLES.substring(1), cycleId};

        GUIutils.loadChildrenToTableView(fillieresTableView, path2, Cycle.class, Filliere.class,
                attributs, columnsTitle);
    }

    public void ajouterFilliere(ActionEvent actionEvent) {

        Filliere filliere = new Filliere(designationTextField.getText());
        filliere.setIdCycle(cyclesChoiceBox.getSelectionModel().getSelectedItem().getId());
        filliere.setId(GUIutils.generateId(filliere));

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(filliere);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadFillieresToTableView();
                designationTextField.clear();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);
    }
    public void supprimerFillieres(ActionEvent actionEvent){

        GUIutils.supprmerObj(fillieresTableView);
        DbGestionController.actualiserLaBaseDeDonnee();
        loadFillieresToTableView();
    }
}