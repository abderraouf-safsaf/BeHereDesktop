package GUIClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.FirebaseException;
import JSONClasses.JSONObject;
import StructureClasses.Cycle;
import StructureClasses.Identifiable;
import StructureClasses.Structure;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;


import java.net.URL;
import java.util.ResourceBundle;


public class CyclesController implements Initializable{

    @FXML
    private TableView<Cycle> cyclesTableView;
    @FXML
    private TextField designationTextField;

    public void initialize(URL location, ResourceBundle resources) {

        cyclesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        loadCyclesToTableView();


    }
    public void loadCyclesToTableView()    {

        String[] attributs = {"designation"};
        String[] columnsTitle = {"Designation"};
        String[] path = {DbOperations.CYCLES.substring(1)};

        GUIutils.loadChildrenToTableView(cyclesTableView, path, Structure.class, Cycle.class,
                attributs, columnsTitle);
    }

    public void ajouterCycle(ActionEvent actionEvent) {

        final Cycle cycle = new Cycle(designationTextField.getText());
        cycle.setId(GUIutils.generateId(cycle));

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(cycle);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadCyclesToTableView();
                designationTextField.clear();
            }
        });

        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);
    }

    public void supprimerCycles(ActionEvent actionEvent){

        GUIutils.supprmerObj(cyclesTableView);
        DbGestionController.actualiserLaBaseDeDonnee();
        loadCyclesToTableView();
    }



}
