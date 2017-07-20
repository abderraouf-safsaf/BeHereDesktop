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
import StructureClasses.*;

import java.net.URL;
import java.util.ResourceBundle;


public class SectionsController implements Initializable{

    @FXML
    private ChoiceBox<Cycle> cyclesChoiceBox;
    @FXML
    private ChoiceBox<Filliere> fillieresChoiceBox;
    @FXML
    private ChoiceBox<Specialite> specialitesChoiceBox;
    @FXML
    private ChoiceBox<Promo> promosChoiceBox;
    @FXML
    private TableView<Section> sectionsTableView;
    @FXML
    private TextField designationTextField;

    public void initialize(URL location, ResourceBundle resources) {

        //activer la selection multiple
        sectionsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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

                if(specialitesChoiceBox.getSelectionModel().getSelectedItem()!=null)
                    loadPromosToChoiceBox();
            }
        });
        promosChoiceBox.valueProperty().addListener(new ChangeListener<Promo>() {
            public void changed(ObservableValue<? extends Promo> observable, Promo oldValue, Promo newValue) {

                if(promosChoiceBox.getSelectionModel().getSelectedItem()!=null)
                    loadSectionsToTableView();
            }
        });

        //
    }

    private void loadCyclesToChoiceBox() {

        String[] path = {DbOperations.CYCLES.substring(1)};
        GUIutils.loadChildrenToChoiceBox(cyclesChoiceBox, path, Structure.class, Cycle.class);
    }

    private void loadFillieresToChoiceBox() {

        specialitesChoiceBox.getItems().clear();
        promosChoiceBox.getItems().clear();
        sectionsTableView.getColumns().clear();

        String cycleId = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String[] path = {DbOperations.CYCLES.substring(1), cycleId};
        //String path = DbOperations.firebasePath(DbOperations.CYCLES, cycleId);

        GUIutils.loadChildrenToChoiceBox(fillieresChoiceBox, path, Cycle.class, Filliere.class);
    }

    private void loadSpecialiteToChoiceBox() {

        promosChoiceBox.getItems().clear();
        sectionsTableView.getColumns().clear();

        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        //String path = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, idFilliere);
        String[] path = {DbOperations.FILIERE_SPECIALITES.substring(1), idFilliere};
        GUIutils.loadChildrenToChoiceBox(specialitesChoiceBox, path, Filliere.class, Specialite.class);
    }
    private void loadPromosToChoiceBox() {

        sectionsTableView.getColumns().clear();

        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere);
        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere};
        GUIutils.loadChildrenToChoiceBox(promosChoiceBox, path, Section.class, Promo.class);

    }
    private void loadSectionsToTableView() {


        String idCycle = cyclesChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idFilliere = fillieresChoiceBox.getSelectionModel().getSelectedItem().getId();
        String idPromo = promosChoiceBox.getSelectionModel().getSelectedItem().getId();

        String[] attributs = {"designation"};
        String[] columnsTitle = {"Designation"};
        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo};

        //String path = DbOperations.firebasePath(DbOperations.CYCLES, idCycle, idFilliere, idPromo);
        GUIutils.loadChildrenToTableView(sectionsTableView, path, Promo.class, Section.class,
                attributs, columnsTitle);


    }
    public void ajouterSection(ActionEvent actionEvent) {

        Section section = new Section(designationTextField.getText());
        section.setId(GUIutils.generateId(section));
        section.setIdCycle(cyclesChoiceBox.getSelectionModel().getSelectedItem().getId());
        section.setIdFilliere(fillieresChoiceBox.getSelectionModel().getSelectedItem().getId());
        section.setIdSpecialite(specialitesChoiceBox.getSelectionModel().getSelectedItem().getId());
        section.setIdPromo(promosChoiceBox.getSelectionModel().getSelectedItem().getId());

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(section);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadSectionsToTableView();
                designationTextField.clear();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);
    }

    public void supprimerSections(ActionEvent actionEvent){

        GUIutils.supprmerObj(sectionsTableView);
        DbGestionController.actualiserLaBaseDeDonnee();
        loadSectionsToTableView();
    }
}