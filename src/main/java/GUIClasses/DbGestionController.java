package GUIClasses;

import FirebaseClasses.DbOperations;
import StructureClasses.*;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DbGestionController implements Initializable{

    @FXML
    private SplitPane mainSplitPane;
    @FXML
    public AnchorPane mainAnchorPane;
    @FXML
    private TreeView<String> mainTreeView;
    @FXML
    private TreeItem<String> rootTreeItem;
    @FXML
    private BorderPane borderPane;


    HashMap<TreeItem<String>, String> itemsFXMLpathsHashMap = new HashMap<TreeItem<String>, String>();


    public void initialize(URL location, ResourceBundle resources) {

        mainSplitPane.setResizableWithParent(mainAnchorPane, false);

        borderPane.setMinWidth(600);
        mainTreeView.setMinWidth(200);

        createAndInsertTreeItems();

        try {
            borderPane.setCenter(((Node) FXMLLoader.load(getClass().getResource("/home.fxml"))));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private void createAndInsertTreeItems() {

        ChangeListener<Boolean> expandedListener = (obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) {
                ReadOnlyProperty<?> expandedProperty = (ReadOnlyProperty<?>) obs ;
                Object itemThatWasJustExpanded = expandedProperty.getBean();
                for (TreeItem<String > item : mainTreeView.getRoot().getChildren()) {
                    if (item != itemThatWasJustExpanded) {
                        item.setExpanded(false);
                    }
                }
            }
        };

        PseudoClass subElementPseudoClass = PseudoClass.getPseudoClass("sub-tree-item");

        mainTreeView.setCellFactory(tv -> {
            TreeCell<String> cell = new TreeCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem("", empty);
                    setText(item);
                }
            };

            cell.treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> {
                cell.pseudoClassStateChanged(subElementPseudoClass,
                        newTreeItem != null && newTreeItem.getParent() != cell.getTreeView().getRoot());
            });

            return cell ;
        });

        TreeItem<String> acceuilTreeItem = new TreeItem<String>("Acceuil");
        itemsFXMLpathsHashMap.put(acceuilTreeItem, "/home.fxml");
        TreeItem<String> structureTreeItem = new TreeItem<String>("Structures");
        structureTreeItem.expandedProperty().addListener(expandedListener);
        itemsFXMLpathsHashMap.put(structureTreeItem, null);
        TreeItem<String> moduleTreeItem = new TreeItem<String>("Modules");
        moduleTreeItem.expandedProperty().addListener(expandedListener);
        itemsFXMLpathsHashMap.put(moduleTreeItem, "/modules.fxml");
        TreeItem<String> enseignantsTreeItem = new TreeItem<String>("Enseignants");
        enseignantsTreeItem.expandedProperty().addListener(expandedListener);
        itemsFXMLpathsHashMap.put(enseignantsTreeItem, "/enseignants.fxml");
        TreeItem<String> etudiantsTreeItem = new TreeItem<String>("Etudiants");
        itemsFXMLpathsHashMap.put(etudiantsTreeItem, "/etudiants.fxml");
        TreeItem<String> absencesTreeItem = new TreeItem<String>("Absences");
        itemsFXMLpathsHashMap.put(absencesTreeItem, "/absences.fxml");

        rootTreeItem.getChildren().addAll(acceuilTreeItem,structureTreeItem, moduleTreeItem, enseignantsTreeItem,
                etudiantsTreeItem, absencesTreeItem);

        TreeItem<String> cycleTreeItem = new TreeItem<String>("Cycles");
        itemsFXMLpathsHashMap.put(cycleTreeItem, "/cycles.fxml");
        TreeItem<String> filliereTreeItem = new TreeItem<String>("Filiéres");
        itemsFXMLpathsHashMap.put(filliereTreeItem, "/fillieres.fxml");
        TreeItem<String> specialiteTreeItem = new TreeItem<String>("Spécialités");
        itemsFXMLpathsHashMap.put(specialiteTreeItem, "/specialites.fxml");
        TreeItem<String> promoTreeItem = new TreeItem<String>("Promos");
        itemsFXMLpathsHashMap.put(promoTreeItem, "/promos.fxml");
        final TreeItem<String> sectionTreeItem = new TreeItem<String>("Sections");
        itemsFXMLpathsHashMap.put(sectionTreeItem, "/sections.fxml");
        TreeItem<String> groupeTreeItem = new TreeItem<String>("Groupes");
        itemsFXMLpathsHashMap.put(groupeTreeItem, "/groupes.fxml");

        structureTreeItem.getChildren().addAll(cycleTreeItem, filliereTreeItem, specialiteTreeItem,
                promoTreeItem, sectionTreeItem, groupeTreeItem);


        TreeItem<String> moduleAffTreeItem = new TreeItem<String>("Affectation");
        itemsFXMLpathsHashMap.put(moduleAffTreeItem, "/affectationModule.fxml");
        moduleTreeItem.getChildren().add(moduleAffTreeItem);

        TreeItem<String> enseignantAffTreeItem = new TreeItem<String>("Affectation");
        enseignantsTreeItem.getChildren().add(enseignantAffTreeItem);


        final TreeItem<String> enseignantAffGroupeTreeItem = new TreeItem<String>("Groupe");
        itemsFXMLpathsHashMap.put(enseignantAffGroupeTreeItem, "/affectationGroupe.fxml");
        TreeItem<String> enseignantAffSectionTreeItem = new TreeItem<String>("Section");
        itemsFXMLpathsHashMap.put(enseignantAffSectionTreeItem, "/affectationSection.fxml");

        enseignantAffTreeItem.getChildren().addAll(enseignantAffGroupeTreeItem,
                enseignantAffSectionTreeItem);



        mainTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {

                TreeItem<String> selectedTreeItem = mainTreeView.getSelectionModel().getSelectedItem();
                changeSceneAccordingToTreeItem(selectedTreeItem);
            }
        });
    }

    private void changeSceneAccordingToTreeItem(TreeItem<String> treeItem)  {

        String path = itemsFXMLpathsHashMap.get(treeItem);

        if (path != null) {

            try {

                borderPane.setCenter(((Node) FXMLLoader.load(getClass().getResource(path))));

            } catch (IOException e) {  e.printStackTrace(); }
        }
    }


    public void closeApp(){

        Main.stage.close();
    }

    public void help(){

    }

    public void about(){

        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getResource("/about.fxml"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Stage aboutStage=new Stage();
        aboutStage.setTitle("A propos");
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(Main.class.getResource("/bootstrap3.css").toExternalForm());
        aboutStage.setScene(scene);
        aboutStage.show();
    }


    public static void actualiserLaBaseDeDonnee(){

        DbOperations.initialiserLaBaseDeDonnee();
    }

}
