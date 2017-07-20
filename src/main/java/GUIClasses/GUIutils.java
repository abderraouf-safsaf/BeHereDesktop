package GUIClasses;

import JSONClasses.JSONException;
import StructureClasses.Absence;
import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import FirebaseClasses.DbOperations;
import StructureClasses.Identifiable;
import StructureClasses.Specialite;
import StructureClasses.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class GUIutils {

    public static Executor executor;

    static {

        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(@NotNull Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
    }

    public static class ProgressForm {

        private final Stage dialogStage;
        private final ProgressIndicator pin = new ProgressIndicator();

        public ProgressForm() {

            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            pin.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            final HBox hb = new HBox();
            hb.setSpacing(5);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().add(pin);

            Scene scene = new Scene(hb);
            dialogStage.setAlwaysOnTop(true);
            dialogStage.setScene(scene);
        }

        public void activateProgressBar(final Task<?> task)  {

            pin.progressProperty().bind(task.progressProperty());
            dialogStage.show();
        }

        public Stage getDialogStage() {

            return dialogStage;
        }
    }

    public final static ProgressForm taskIndicatorForm = new ProgressForm();

    public static <V extends Identifiable, T extends Identifiable> void loadChildrenToTableView(final TableView<T> tableView,
                                                                                                String path, Class<V> parentClass, Class<T> childClass,
                                                                                                final String[] attributs, final String[] columnsTitle)  {

        final Task<HashMap<String, T>> getterTask = DbOperations.getChildrenTask(
                path, parentClass, childClass);

        final ProgressForm taskIndicatorForm = new ProgressForm();

        taskIndicatorForm.activateProgressBar(getterTask);
        getterTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            public void handle(WorkerStateEvent event) {

                HashMap<String, T> childrenHashMap = getterTask.getValue();
                loadHashMapToTableView(tableView, childrenHashMap, attributs, columnsTitle);
                taskIndicatorForm.getDialogStage().close();
            }
        });
        taskIndicatorForm.getDialogStage().show();

        executor.execute(getterTask);
    }

    public static <T extends Identifiable> void loadHashMapToTableView(TableView<T> tableView,
                                                                       HashMap<String, T> childrenHashMap,
                                                                       String[] attributs, String[] columnsTitle) {
        tableView.getColumns().clear();

        for (int i = 0 ; i < attributs.length; i++) {

            TableColumn<T, String> tableColumn = new TableColumn<T, String>(columnsTitle[i]);
            tableColumn.setPrefWidth(150);
            tableColumn.setCellValueFactory(new PropertyValueFactory<T, String> (attributs[i]));
            tableView.getColumns().add(tableColumn);
        }

        if(childrenHashMap!=null){

            ObservableList<T> childrenObservableList = FXCollections.observableList(
                    new ArrayList<T>(childrenHashMap.values()));
            tableView.setItems(childrenObservableList);
        }

    }

    public static <V extends Identifiable, T extends Identifiable> void loadChildrenToChoiceBox(final ChoiceBox<T> choiceBox,
                                                                                                String path, Class<V> parentClass, Class<T> childClass) {

        final Task<HashMap<String, T>> getterTask = DbOperations.getChildrenTask(
                path, parentClass, childClass);

        final ProgressForm taskIndicatorForm = new ProgressForm();

        taskIndicatorForm.activateProgressBar(getterTask);
        getterTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            public void handle(WorkerStateEvent event) {

                HashMap<String, T> childrenHashMap = getterTask.getValue();
                loadHashMapToChoiceBox(choiceBox, childrenHashMap);
                taskIndicatorForm.getDialogStage().close();
            }
        });
        taskIndicatorForm.getDialogStage().show();

        executor.execute(getterTask);
    }

    private static <T> void loadHashMapToChoiceBox(ChoiceBox<T> choiceBox, HashMap<String, T> childrenHashMap)  {

        choiceBox.getItems().clear();

        choiceBox.getItems().addAll(childrenHashMap.values());
        if(choiceBox.getId().equals("specialitesChoiceBox"))
            choiceBox.getItems().add((T) Specialite.PAS_DE_SPECIALITE);

    }

    ///////////superSpeed////////////////
    public static <V extends Identifiable, T extends Identifiable> void loadChildrenToChoiceBox(final ChoiceBox<T> choiceBox,
                                                                                                String[] path, Class<V> parentClass, Class<T> childClass) {

        HashMap<String, T>  childrenHashMap = DbOperations.getChildren(parentClass, childClass, path);
        loadHashMapToChoiceBox(choiceBox, childrenHashMap);

    }

    public static <V extends Identifiable, T extends Identifiable> void loadChildrenToTableView(final TableView<T> tableView,
                                                                                                String[] path, Class<V> parentClass, Class<T> childClass,
                                                                                                final String[] attributs, final String[] columnsTitle)  {

        HashMap<String, T>  childrenHashMap;
        try {

            childrenHashMap = DbOperations.getChildren(parentClass, childClass, path);

        }catch (JSONException e){
            childrenHashMap=null;
            e.printStackTrace();
        }
        loadHashMapToTableView(tableView, childrenHashMap, attributs, columnsTitle);

    }

    //////////////////////////////////////

    public static <T extends Identifiable> Task<Void> ajouterDbTask(final T obj)  {

        final Task<Void> ajoutTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                obj.ajouterDb();
                return null;
            }
        };

        return ajoutTask;
    }

    public static <T extends Identifiable>void supprmerObj(TableView<T> tableView){

        try {
            ObservableList<T> selection;
            selection=tableView.getSelectionModel().getSelectedItems();
            if(selection==null)
                throw new Exception();
            else{
                for (T obj : selection) {

                    final Task<Void> suppressionTask = supprimerDbTask(obj);
                    suppressionTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        public void handle(WorkerStateEvent event) {

                            taskIndicatorForm.getDialogStage().close();
                        }
                    });
                    taskIndicatorForm.activateProgressBar(suppressionTask);
                    taskIndicatorForm.getDialogStage().show();

                    executor.execute(suppressionTask);
                }
            }
        } catch (Exception e) {
            Alert dialogE = new Alert(Alert.AlertType.ERROR);
            dialogE.setTitle("Error");
            dialogE.setHeaderText(null);
            dialogE.setContentText("Error : séléctionez un élément dans la table pour le supprimer!");
            dialogE.showAndWait();
        }
    }

    private static <T extends Identifiable> Task<Void> supprimerDbTask(final T obj)  {

        final Task<Void> suppressionTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                obj.supprimerDb();
                return null;
            }
        };

        return suppressionTask;
    }

    public static String generateId()   {

        return UUID.randomUUID().toString();
    }

    public static String generateId(Structure obj)   {

        String designation = obj.getDesignation();
        designation = designation.replaceAll("\\s+","");

        String id = String.format("%s_%s", designation, UUID.randomUUID().toString());
        return id;
    }

    public static int getMonthFromDate(String date) {
        String string = date.substring(date.indexOf('/') + 1, date.lastIndexOf('/'));

        int i = Integer.decode(string);
        return i;
    }
    public static String getMonthFromInt(int month){
        String months =null;
        switch (month) {
            case 1: months = "Jan"; break;
            case 2: months = "Feb"; break;
            case 3: months = "Mar"; break;
            case 4: months = "Apr"; break;
            case 5: months = "May"; break;
            case 6: months = "June"; break;
            case 7: months = "July"; break;
            case 8: months = "Aug"; break;
            case 9: months = "Sept"; break;
            case 10: months = "Oct"; break;
            case 11: months = "Nov"; break;
            case  12: months = "Dec"; break;
        }
        return months;

    }

    public static int[] getNombreAbsencesByMonth(HashMap<String, Absence> absenceHashMap){
        int[] absences  =new int[12];
        for (int i =0 ;i<absences.length;i++)absences[i]=0;

        for (Map.Entry<String,Absence> absenceEntry:absenceHashMap.entrySet()){
            switch (getMonthFromDate(absenceEntry.getValue().getDate())){

                case 1: absences[0]++;break;
                case 2: absences[1]++;break;
                case 3: absences[2]++;break;
                case 4: absences[3]++;break;
                case 5: absences[4]++;break;
                case 6: absences[5]++;break;
                case 7: absences[6]++;break;
                case 8: absences[7]++;break;
                case 9: absences[8]++;break;
                case 10: absences[9]++;break;
                case 11: absences[10]++;break;
                case 12: absences[11]++;break;

            }


        }
        return absences;

    }
}