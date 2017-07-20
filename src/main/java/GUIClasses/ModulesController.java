package GUIClasses;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import FirebaseClasses.DbOperations;
import StructureClasses.*;

import java.net.URL;
import java.util.ResourceBundle;


public class ModulesController implements Initializable{

    public static final Integer SEMESTRE_SPINNER_VALEUR = 1;
    @FXML
    private ChoiceBox<Cycle> cyclesChoiceBox;
    @FXML
    private ChoiceBox<Filliere> fillieresChoiceBox;
    @FXML
    private ChoiceBox<Specialite> specialitesChoiceBox;
    @FXML
    private ChoiceBox<Promo> promosChoiceBox;
    @FXML
    private TableView<Module> modulesTableView;
    @FXML
    private TitledPane ajouterModuleTitledPane;
    @FXML
    private TextField designationTextField;
    @FXML
    private TextField vhCoursTextField;
    @FXML
    private TextField vhTdTextField;
    @FXML
    private TextField vhTpTextField;
    @FXML
    private TextField coeffTextField;
    @FXML
    private TextField creditTextField;
    @FXML
    private Spinner<Integer> semestreSpinner;

    public void initialize(URL location, ResourceBundle resources) {

        //activer la selection multiple
        modulesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //

        //remplissage des choiceBox de l'interface

        SpinnerValueFactory<Integer> semestreSpinnerValueFactory = new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                this.setValue(getValue() - 1);
            }

            @Override
            public void increment(int steps) {
                this.setValue(getValue() + 1);
            }
        };
        semestreSpinnerValueFactory.setValue(SEMESTRE_SPINNER_VALEUR);
        semestreSpinner.setValueFactory(semestreSpinnerValueFactory);


        ajouterModuleTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (newValue)   {
                    loadCyclesToChoiceBox();
                }
            }
        });

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
        loadModulesToTableView();
    }

    private void loadCyclesToChoiceBox() {

        String[] path = {DbOperations.CYCLES.substring(1)};
        GUIutils.loadChildrenToChoiceBox(cyclesChoiceBox, path, Structure.class, Cycle.class);
    }

    private void loadFillieresToChoiceBox() {

        specialitesChoiceBox.getItems().clear();
        promosChoiceBox.getItems().clear();

        String cycleId = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, cycleId);

        String[] path = {DbOperations.CYCLES.substring(1), cycleId};
        GUIutils.loadChildrenToChoiceBox(fillieresChoiceBox, path, Cycle.class, Filliere.class);
    }

    private void loadSpecialiteToChoiceBox() {

        promosChoiceBox.getItems().clear();

        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, idFilliere);
        String[] path = {DbOperations.FILIERE_SPECIALITES.substring(1), idFilliere};
        GUIutils.loadChildrenToChoiceBox(specialitesChoiceBox, path, Filliere.class, Specialite.class);
    }
    private void loadPromosToChoiceBox() {

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere);
        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere};
        GUIutils.loadChildrenToChoiceBox(promosChoiceBox, path, Section.class, Promo.class);

    }
    private void loadModulesToTableView() {


        String[] attributs = {"designation", "coeff", "semestre", "credit", "VHcours", "VHtd",
        "VHtp"};
        String[] columnsTitle = {"Designation", "Coefficient", "Semestre", "Credits", "VH Cours",
        "VH TD", "VH TP"};

        //String path = DbOperations.firebasePath(DbOperations.MODULE_ENSEIGNANTS);
        String[] path = {DbOperations.MODULE_ENSEIGNANTS.substring(1)};
        GUIutils.loadChildrenToTableView(modulesTableView, path, Structure.class, Module.class,
                attributs, columnsTitle);
    }
    public void ajouterModule(ActionEvent actionEvent) {

        Module module = new Module(designationTextField.getText());
        module.setId(GUIutils.generateId(module));
        module.setIdCycle(cyclesChoiceBox.getSelectionModel().getSelectedItem().getId());
        module.setIdFilliere(fillieresChoiceBox.getSelectionModel().getSelectedItem().getId());
        module.setIdSpecialite(specialitesChoiceBox.getSelectionModel().getSelectedItem().getId());
        module.setIdPromo(promosChoiceBox.getSelectionModel().getSelectedItem().getId());
        module.setCoeff(Integer.parseInt(coeffTextField.getText()));
        module.setCredit(Integer.parseInt(creditTextField.getText()));
        module.setSemestre(semestreSpinner.getValue());
        module.setVHcours(Integer.parseInt(vhCoursTextField.getText()));
        module.setVHtd(Integer.parseInt(vhTdTextField.getText()));
        module.setVHtp(Integer.parseInt(vhTpTextField.getText()));

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(module);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadModulesToTableView();
                designationTextField.clear();
                coeffTextField.clear();
                creditTextField.clear();
                vhCoursTextField.clear();
                vhTdTextField.clear();
                vhTpTextField.clear();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);
    }

    public void supprimerModules(ActionEvent actionEvent){

        GUIutils.supprmerObj(modulesTableView);
        DbGestionController.actualiserLaBaseDeDonnee();
        loadModulesToTableView();
    }
}