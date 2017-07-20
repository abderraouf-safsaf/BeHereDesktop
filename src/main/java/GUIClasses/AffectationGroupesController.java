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


public class AffectationGroupesController implements Initializable{

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
    private ChoiceBox<Groupe> groupesChoiceBox;
    @FXML
    private ChoiceBox<Module> modulesChoiceBox;
    @FXML
    private TableView<Groupe> groupesTableView;

    public void initialize(URL location, ResourceBundle resources) {

        // remplissage des choiceBox de l'interface

        loadEnseignantsToChoiceBox();
        loadCyclesToChoiceBox();

        enseignantsChoiceBox.valueProperty().addListener(new ChangeListener<Enseignant>() {
            public void changed(ObservableValue<? extends Enseignant> observable, Enseignant oldValue, Enseignant newValue) {

                loadModulesToChoiceBox();
            }
        });
        modulesChoiceBox.valueProperty().addListener(new ChangeListener<Module>() {
            public void changed(ObservableValue<? extends Module> observable, Module oldValue, Module newValue) {

                loadEnseignantGroupesToTableView();
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
                    loadSectionsToChoiceBox();
            }
        });
        sectionsChoiceBox.valueProperty().addListener(new ChangeListener<Section>() {
            public void changed(ObservableValue<? extends Section> observable, Section oldValue, Section newValue) {
                if(sectionsChoiceBox.getSelectionModel().getSelectedItem()!=null)
                    loadGroupesToChoiceBox();
            }
        });

        //

    }

    private void loadEnseignantGroupesToTableView() {

            String idEnseignant = enseignantsChoiceBox.getSelectionModel().getSelectedItem().getId();
            String idModule = modulesChoiceBox.getSelectionModel().getSelectedItem().getId();

            String[] attributs = {"designation"};
            String[] columnsTitle = {"Designation"};
            String[] path ={DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant, idModule, DbOperations.GROUPES.substring(1)};

            GUIutils.loadChildrenToTableView(groupesTableView, path, Structure.class, Groupe.class,
                    attributs, columnsTitle);
    }

    private void loadEnseignantsToChoiceBox() {

        String[] path ={DbOperations.ENSEIGNANT_MODULE.substring(1)};

        GUIutils.loadChildrenToChoiceBox(enseignantsChoiceBox, path, Structure.class, Enseignant.class);
    }

    private void loadCyclesToChoiceBox() {

        String[] path ={DbOperations.CYCLES.substring(1)};
        GUIutils.loadChildrenToChoiceBox(cyclesChoiceBox, path, Structure.class, Cycle.class);
    }

    private void loadFillieresToChoiceBox() {

        specialitesChoiceBox.getItems().clear();
        promosChoiceBox.getItems().clear();
        sectionsChoiceBox.getItems().clear();
        groupesChoiceBox.getItems().clear();

        String cycleId = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, cycleId);
        String[] path ={DbOperations.CYCLES.substring(1), cycleId};

        GUIutils.loadChildrenToChoiceBox(fillieresChoiceBox, path, Cycle.class, Filliere.class);
    }

    private void loadSpecialiteToChoiceBox() {

        promosChoiceBox.getItems().clear();
        sectionsChoiceBox.getItems().clear();
        groupesChoiceBox.getItems().clear();

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

        sectionsChoiceBox.getItems().clear();
        groupesChoiceBox.getItems().clear();

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere);

        String[] path ={DbOperations.CYCLES.substring(1), idCycle, idFilliere};
        GUIutils.loadChildrenToChoiceBox(promosChoiceBox, path, Section.class, Promo.class);
    }
    private void loadSectionsToChoiceBox() {

        groupesChoiceBox.getItems().clear();

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idPromo = promosChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere, idPromo);
        String[] path ={DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo};
        GUIutils.loadChildrenToChoiceBox(sectionsChoiceBox, path, Promo.class, Section.class);
    }
    private void loadGroupesToChoiceBox() {

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idPromo = promosChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idSection = sectionsChoiceBox.getSelectionModel().getSelectedItem().getId();

        String[] path ={DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo, idSection};
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere, idPromo, idSection);
        GUIutils.loadChildrenToChoiceBox(groupesChoiceBox, path, Section.class, Groupe.class);
    }
    private void loadModulesToChoiceBox() {

        groupesTableView.getColumns().clear();

        String idEnseignant = enseignantsChoiceBox.getSelectionModel().getSelectedItem().getId();
        String[] path ={DbOperations.ENSEIGNANT_MODULE.substring(1), idEnseignant};
        //String path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, idEnseignant);

        GUIutils.loadChildrenToChoiceBox(modulesChoiceBox, path, Enseignant.class, Module.class);
    }
    public void affecterGroupeToEnseignant(ActionEvent actionEvent) {

        Enseignant enseignant = enseignantsChoiceBox.getSelectionModel().getSelectedItem();
        Module module = modulesChoiceBox.getSelectionModel().getSelectedItem();
        Groupe groupe = groupesChoiceBox.getSelectionModel().getSelectedItem();


        final Task<Void> affectationTask = affecterGroupeDbTask(enseignant, module, groupe);
        affectationTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadEnseignantGroupesToTableView();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(affectationTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(affectationTask);
    }

    private Task<Void> affecterGroupeDbTask(final Enseignant enseignant, final Module module, final Groupe groupe)  {

        final Task<Void> affectationTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                enseignant.affecterGroupe(module, groupe);
                DbGestionController.actualiserLaBaseDeDonnee();
                loadEnseignantGroupesToTableView();
                return null;
            }
        };

        return affectationTask;
    }

}