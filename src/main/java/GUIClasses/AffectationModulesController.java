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
import javafx.scene.control.TableView;
import FirebaseClasses.DbOperations;
import StructureClasses.*;

import java.net.URL;
import java.util.ResourceBundle;


public class AffectationModulesController implements Initializable{

    @FXML
    private ChoiceBox<Enseignant> enseignantsChoiceBox;
    @FXML
    private ChoiceBox<Cycle> cyclesChoiceBox;
    @FXML
    private ChoiceBox<Filliere> fillieresChoiceBox;
    @FXML
    private ChoiceBox<Specialite> specialitesChoiceBox;
    @FXML
    private ChoiceBox<Promo> promosChoiceBox;
    @FXML
    private ChoiceBox<Module> modulesChoiceBox;
    @FXML
    private TableView<Module> modulesTableView;

    public void initialize(URL location, ResourceBundle resources) {

        // remplissage des choiceBox de l'interface

        loadEnseignantsToChoiceBox();

        loadCyclesToChoiceBox();

        enseignantsChoiceBox.valueProperty().addListener(new ChangeListener<Enseignant>() {

            public void changed(ObservableValue<? extends Enseignant> observable, Enseignant oldValue, Enseignant newValue) {

                loadEnseignantsModulesToTableView();
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
        promosChoiceBox.valueProperty().addListener(new ChangeListener<Promo>() {
            public void changed(ObservableValue<? extends Promo> observable, Promo oldValue, Promo newValue) {
                if(promosChoiceBox.getSelectionModel().getSelectedItem()!=null)
                    loadModulesToChoiceBox();
            }
        });

        //

    }

    private void loadEnseignantsToChoiceBox() {

        String[] path ={DbOperations.ENSEIGNANT_MODULE.substring(1)};

        GUIutils.loadChildrenToChoiceBox(enseignantsChoiceBox, path, Structure.class, Enseignant.class);    }

    private void loadEnseignantsModulesToTableView() {

        String idEnseignant = enseignantsChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, idEnseignant);

        String[] attributs = {"designation"};
        String[] columnsTitle = {"Module"};
        String[] path = {DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant};

        GUIutils.loadChildrenToTableView(modulesTableView, path, Enseignant.class, Module.class,
                attributs, columnsTitle);
    }

    private void loadCyclesToChoiceBox() {

        String[] path ={DbOperations.CYCLES.substring(1)};
        GUIutils.loadChildrenToChoiceBox(cyclesChoiceBox, path, Structure.class, Cycle.class);    }

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

        if (fillieresChoiceBox.getSelectionModel().getSelectedItem().hasSpecialites()) {

            String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();

            String[] path = {DbOperations.FILIERE_SPECIALITES.substring(1), idFilliere};
            //String path = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, idFilliere);
            GUIutils.loadChildrenToChoiceBox(specialitesChoiceBox, path, Filliere.class, Specialite.class);
        }
        else    {

            specialitesChoiceBox.getItems().clear();
            specialitesChoiceBox.getItems().add(Specialite.PAS_DE_SPECIALITE);
            specialitesChoiceBox.getSelectionModel().select(0);
        }
    }

    private void loadPromosToChoiceBox() {

        modulesChoiceBox.getItems().clear();

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere);

        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere};
        GUIutils.loadChildrenToChoiceBox(promosChoiceBox, path, Section.class, Promo.class);
    }

    private void loadModulesToChoiceBox() {

        String idPromo = promosChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.PROMO_MODULES, idPromo);

        String[] path ={DbOperations.PROMO_MODULES.substring(1), idPromo};

        GUIutils.loadChildrenToChoiceBox(modulesChoiceBox, path, Promo.class, Module.class);
    }

    public void affecterModuleToEnseignant(ActionEvent actionEvent) {

        Enseignant enseignant= enseignantsChoiceBox.getValue();
        Module module = modulesChoiceBox.getSelectionModel().getSelectedItem();

        final Task<Void> affectationTask = affecterModuleDbTask(enseignant, module);
        affectationTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadEnseignantsModulesToTableView();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(affectationTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(affectationTask);
    }

    private Task<Void> affecterModuleDbTask(final Enseignant enseignant, final Module module)  {

        final Task<Void> ajoutTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                enseignant.affecterModule(module);
                DbGestionController.actualiserLaBaseDeDonnee();
                loadEnseignantsModulesToTableView();
                return null;
            }
        };

        return ajoutTask;
    }
}