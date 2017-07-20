package FirebaseClasses;

import JSONClasses.JSONException;
import JSONClasses.JSONObject;
import StructureClasses.Identifiable;
import StructureClasses.Main;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.util.*;


public class DbOperations   {


    public final static String  CYCLES = "/Cycles", SPECIALITE_PROMOS = "/Specialite_Promos",
                                FILIERE_SPECIALITES = "/Filliere_Specialites",
                                MODULE_ENSEIGNANTS = "/Module_Enseignants",
                                ENSEIGNANT_MODULE = "/Enseignant_Modules",
                                PROMO_MODULES = "/Promo_Modules",
                                GROUPES = "/Groupes", SECTIONS = "/Sections",
                                GROUPE_MODULES="/Groupe_Modules", SECTION_MODULES="/Section_Modules";


    private static String root;

    private static Firebase firebase = null;

    public static JSONObject baseDonnees;
    ///////////////////////////////////////////////////////
    public static void actualiserLaBaseDeDonnee(){

        if(Main.testInet("google.com")){

            try {

                baseDonnees = new JSONObject(firebase.get().getBody());
            } catch (FirebaseException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            Alert dialogE = new Alert(Alert.AlertType.ERROR);
            dialogE.setTitle("Error");
            dialogE.setHeaderText(null);
            dialogE.setContentText(" Ce logiciel a besoin d'une connexion internet verifiez la votre");
            dialogE.showAndWait();
        }

    }
    public static void initialiserLaBaseDeDonnee() {

        try {

                baseDonnees = new JSONObject(firebase.get().getBody());
            } catch (FirebaseException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


    ///////////////////////////////////////////////////////////


    public DbOperations()  {
    }

    public DbOperations(String root) {

        this.root = root;
        firebase = firebaseNoeud("");

    }

    public static String getRoot() {
        return root;
    }

    public static void setRoot(String root) {
        DbOperations.root = root;
        firebase = firebaseNoeud("");
    }

    public static String firebasePath(String ... noeuds)   {

        String path = "";
        for (int i = 0; i < noeuds.length; i++) {

            path += String.format("%s/", noeuds[i]);
        }
        return path;
    }
    public static Firebase firebaseNoeud(String path) {

        Firebase firebaseNoeud = null;
        try {
            firebaseNoeud = new Firebase(root + path);
        } catch(FirebaseException e)    {   }

        return firebaseNoeud;
    }
    public static FirebaseResponse dbPut(Firebase noeud, Map<String, Object> dataMap)    {

        FirebaseResponse response = null;
        try {

            response = noeud.put(dataMap);
        }
        catch (FirebaseException firebaseE)   {   }
        catch (JacksonUtilityException jacksonE)    {   }
        catch (UnsupportedEncodingException encodingE)  {   }

        return response;
    }
    public static HashMap<String, Object> dbGet(String path)    {

        Firebase noeud = firebaseNoeud(path);
        FirebaseResponse response = null;

        try {

            response  = noeud.get();
        }
        catch (FirebaseException e) { }
        catch (UnsupportedEncodingException e) {    }

        return ((HashMap<String, Object>) response.getBody());
    }

    public static FirebaseResponse dbDelete(String path)   {

        Firebase noeud = firebaseNoeud("");
        FirebaseResponse response = null;
        try {

            response = noeud.delete(path);
        }
        catch (FirebaseException e) { }
        catch (UnsupportedEncodingException e) {    }

        return response;
    }
    public static <V extends Identifiable, T extends Identifiable> Task<HashMap<String, T>> getChildrenTask(
            final String path, final Class<V> classParent, final Class<T> classChild) {

        Task<HashMap<String, T>> task = new Task<HashMap<String, T>>() {
            @Override
            protected HashMap<String, T> call() throws Exception {

                return getChildren(path, classParent, classChild);
            }
        };
        return task;
    }


    public static <V extends Identifiable, T extends Identifiable> HashMap<String, T> getChildren(
            String path, Class<V> classParent, Class<T> classChild) {

        V objectParent = null; T objectChild = null;
        try {

            if (!Modifier.isAbstract(classParent.getModifiers()))
                objectParent = classParent.newInstance();
            objectChild = classChild.newInstance();
        } catch (Exception e)   {   }

        Firebase noeud = firebaseNoeud(path);
        FirebaseResponse response = null;
        try {
            response = noeud.get();
        } catch (FirebaseException e) { }
        catch (UnsupportedEncodingException e) {    }



        JSONObject dbChildrenJson = new JSONObject(response.getBody());

        if (!Modifier.isAbstract(classParent.getModifiers()))
            deleteAttributsFromJSONObject(dbChildrenJson, objectParent.getMap());

        HashMap<String, T> childrenMap = new HashMap<String, T>();

        T obj = null;
        for (String key: dbChildrenJson.keySet())  {

            JSONObject jsonObject = dbChildrenJson.getJSONObject(key);
            HashMap<String, Object> attributs = getOnlyAttributsJSON(jsonObject, objectChild.getMap());

            try {
                obj = (T) classChild.newInstance();
            } catch (Exception e)   {   }

            obj.setAttributs(attributs);
            childrenMap.put(key , obj);
        }
        return childrenMap;
    }
    //***************************SpeedyMethode***************************//
    public static <V extends Identifiable, T extends Identifiable> HashMap<String, T> getChildren(Class<V> classParent, Class<T> classChild,
                                                                                                  String[] path) throws JSONException {

        V objectParent = null; T objectChild = null;
        try {
            if (!Modifier.isAbstract(classParent.getModifiers()))
                objectParent = classParent.newInstance();
            objectChild = classChild.newInstance();
        } catch (Exception e)   {   }

        JSONObject base = baseDonnees;

        for (int i = 0; i <path.length ; i++) {

            if(base.length()!=0)
                base = base.getJSONObject(path[i]);

            System.out.println(base.names());
        }

        System.out.println("  \n");


        HashMap<String, T> childrenMap = new HashMap<String, T>();

        T obj = null;
        for (String key: base.keySet())  {

            if(objectParent == null){

                JSONObject jsonObject = base.getJSONObject(key);
                HashMap<String, Object> attributs = getOnlyAttributsJSON(jsonObject, objectChild.getMap());

                try {
                    obj = (T) classChild.newInstance();
                } catch (Exception e)   {   }

                obj.setAttributs(attributs);
                childrenMap.put(key , obj);
            }else
                if (! objectParent.getMap().containsKey(key)){

                    JSONObject jsonObject = base.getJSONObject(key);
                    HashMap<String, Object> attributs = getOnlyAttributsJSON(jsonObject, objectChild.getMap());

                    try {
                        obj = (T) classChild.newInstance();
                    } catch (Exception e)   {   }

                    obj.setAttributs(attributs);
                    childrenMap.put(key , obj);
                }
        }
        return childrenMap;
    }
    //

    private static void deleteAttributsFromJSONObject(JSONObject dbChildrenJson,
                                                      Map<String, Object> attributsMap) {

        for(Map.Entry<String, Object> attributs: attributsMap.entrySet()) {

            dbChildrenJson.remove(attributs.getKey());
        }
    }
    private static HashMap<String, Object> getOnlyAttributsJSON(JSONObject jsonObject,
                                                                Map<String, Object> atrributsMap)   {

        HashMap<String, Object> attributsHashMap = new HashMap<String, Object>();

        for (String key: jsonObject.keySet())   {

            if (atrributsMap.containsKey(key))  {

                attributsHashMap.put(key, jsonObject.get(key));
            }
        }

        return attributsHashMap;
    }

}
