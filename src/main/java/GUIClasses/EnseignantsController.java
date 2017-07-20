package GUIClasses;

import StructureClasses.Cycle;
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
import StructureClasses.Enseignant;
import StructureClasses.Personne;
import StructureClasses.Structure;

import java.net.URL;
import java.util.ResourceBundle;


public class EnseignantsController implements Initializable{

    @FXML
    private TableView<Enseignant> enseignantsTableView;
    @FXML
    private TextField nomTextField;
    @FXML
    private TextField prenomTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private ChoiceBox<Character> sexeChoiceBox;
    @FXML
    private ChoiceBox<String> gradeChoiceBox;

    public void initialize(URL location, ResourceBundle resources) {

        //// remplissage des choiceBox de l'interface

        enseignantsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        sexeChoiceBox.getItems().addAll(Personne.FEMME, Personne.HOMME);
        gradeChoiceBox.getItems().addAll(Enseignant.PR, Enseignant.MCA, Enseignant.MCB, Enseignant.MAA,
                Enseignant.MAB);

        loadEnseignantsToTableView();

        //
    }


    public void loadEnseignantsToTableView()    {

        String[] attributs = {"nom", "prenom", "sexe", "email", "grade"};
        String[] columnsTitle = {"Nom", "Prénom", "Sexe", "Email", "Grade"};

        String[] path = {DbOperations.ENSEIGNANT_MODULE.substring(1)};
        GUIutils.loadChildrenToTableView(enseignantsTableView, path, Structure.class, Enseignant.class, attributs, columnsTitle);
    }

    public void ajouterEnseignant(ActionEvent actionEvent) {

        final Enseignant enseignant = new Enseignant(nomTextField.getText(), prenomTextField.getText(),
                sexeChoiceBox.getSelectionModel().getSelectedItem());
        enseignant.setId(GUIutils.generateId());
        enseignant.setEmail(emailTextField.getText());
        enseignant.setGrade(gradeChoiceBox.getSelectionModel().getSelectedItem());

        final Task<Void> ajoutTask = GUIutils.ajouterDbTask(enseignant);
        ajoutTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {

                GUIutils.taskIndicatorForm.getDialogStage().close();
                DbGestionController.actualiserLaBaseDeDonnee();
                loadEnseignantsToTableView();
                nomTextField.clear();
                prenomTextField.clear();
                emailTextField.clear();
            }
        });
        GUIutils.taskIndicatorForm.activateProgressBar(ajoutTask);
        GUIutils.taskIndicatorForm.getDialogStage().show();

        GUIutils.executor.execute(ajoutTask);
    }

    public void supprimerEnseignants(ActionEvent actionEvent){

        GUIutils.supprmerObj(enseignantsTableView);
        DbGestionController.actualiserLaBaseDeDonnee();
        if(!GUIutils.taskIndicatorForm.getDialogStage().isShowing())loadEnseignantsToTableView();
    }

}
