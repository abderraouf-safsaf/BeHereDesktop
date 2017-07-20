package GUIClasses;

import StructureClasses.*;
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


import java.net.URL;
import java.util.ResourceBundle;


public class AffectationSectionsController implements Initializable{

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
    private ChoiceBox<Section> sectionsChoiceBox;
    @FXML
    private ChoiceBox<Module> modulesChoiceBox;
    @FXML
    private TableView<Section> sectionsTableView;

    public void initialize(URL location, ResourceBundle resources) {

        // remplissage des choiceBox de l'interface

        loadEnseignantsToChoiceBox();
        loadCyclesToChoiceBox();

        enseignantsChoiceBox.valueProperty().addListener(new ChangeListener<Enseignant>() {
            public void changed(ObservableValue<? extends Enseignant> observable, Enseignant oldValue, Enseignant newValue) {

                loadModulesToChoiceBox();

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
                if(promosChoiceBox.getSelectionModel().getSelectedItem()!=null){
                    loadSectionsToChoiceBox();
                }
            }
        });

        modulesChoiceBox.valueProperty().addListener(new ChangeListener<Module>() {

            public void changed(ObservableValue<? extends Module> observable, Module oldValue, Module newValue) {

                loadEnseignantSectionsToTableView();
            }
        });

        //


    }

    private void loadEnseignantSectionsToTableView() {

            String idEnseignant = enseignantsChoiceBox.getSelectionModel().getSelectedItem().getId();
            String idModule = modulesChoiceBox.getSelectionModel().getSelectedItem().getId();

            String[] attributs = {"designation"};
            String[] columnsTitle = {"Designation"};

            String[] path = {DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant, idModule, DbOperations.SECTIONS.substring(1)};

            //String path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, idEnseignant, idModule, DbOperations.SECTIONS);

        GUIutils.loadChildrenToTableView(sectionsTableView, path, Structure.class, Section.class,
                    attributs, columnsTitle);
    }

    private void loadEnseignantsToChoiceBox() {

        String[] path = {DbOperations.ENSEIGNANT_MODULE.substring(1)};

        GUIutils.loadChildrenToChoiceBox(enseignantsChoiceBox, path, Structure.class, Enseignant.class);
    }

    private void loadCyclesToChoiceBox() {


        String[] path = {DbOperations.CYCLES.substring(1)};

        GUIutils.loadChildrenToChoiceBox(cyclesChoiceBox, path, Structure.class, Cycle.class);
    }

    private void loadFillieresToChoiceBox() {

        sectionsChoiceBox.getItems().clear();
        promosChoiceBox.getItems().clear();
        sectionsChoiceBox.getItems().clear();

        String cycleId = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, cycleId);
        String[] path = {DbOperations.CYCLES.substring(1), cycleId};

        GUIutils.loadChildrenToChoiceBox(fillieresChoiceBox, path, Cycle.class, Filliere.class);
    }

    private void loadSpecialiteToChoiceBox() {

        promosChoiceBox.getItems().clear();
        sectionsChoiceBox.getItems().clear();

        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, idFilliere);
        String[] path = {DbOperations.FILIERE_SPECIALITES.substring(1), idFilliere};
        GUIutils.loadChildrenToChoiceBox(specialitesChoiceBox, path, Filliere.class, Specialite.class);
    }
    private void loadPromosToChoiceBox() {

        sectionsChoiceBox.getItems().clear();

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();

        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere};
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere);
        GUIutils.loadChildrenToChoiceBox(promosChoiceBox, path, Section.class, Promo.class);
    }
    private void loadSectionsToChoiceBox() {

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idPromo = promosChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere, idPromo);

        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo};

        GUIutils.loadChildrenToChoiceBox(sectionsChoiceBox, path, Promo.class, Section.class);
    }
    private void loadModulesToChoiceBox() {

        sectionsTableView.getColumns().clear();

        String idEnseignant =enseignantsChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, idEnseignant);

        String[] path = {DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant};

        GUIutils.loadChildrenToChoiceBox(modulesChoiceBox, path, Enseignant.class, Module.class);
    }
    public void affecterSectionToEnseignant(ActionEvent actionEvent) {

        Enseignant enseignant = enseignantsChoiceBox.getSelectionModel().getSelectedItem();
        Module module = modulesChoiceBox.getSelectionModel().getSelectedItem();
        Section section = sectionsChoiceBox.getSelectionModel().getSelectedItem();


        final Task<Void> affectationTask = affecterSectionDbTask(enseignant, module, section);
        affectationTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadEnseignantSectionsToTableView();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(affectationTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(affectationTask);
    }

    private Task<Void> affecterSectionDbTask(final Enseignant enseignant, final Module module, final Section section)  {

        final Task<Void> affectationTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                enseignant.affecterSection(module, section);
                DbGestionController.actualiserLaBaseDeDonnee();
                loadEnseignantSectionsToTableView();
                return null;
            }
        };

        return affectationTask;
    }
}