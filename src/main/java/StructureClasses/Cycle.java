package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;

import java.util.HashMap;
import java.util.Map;


public class Cycle extends Structure {

    private int nbEtudiants;

    public Cycle()  {

        super();
    }
    public Cycle(String designation) {

        super(designation);
    }

    public int getNbEtudiants() {
        return nbEtudiants;
    }

    public void setNbEtudiants(int nbEtudiants) {
        this.nbEtudiants = nbEtudiants;
    }

    public Map<String, Object> getMap(){

        Map<String, Object> mapdata = new HashMap<String, Object>();

        mapdata.put("id", this.getId());
        mapdata.put("designation", this.getDesignation());
        mapdata.put("nbEtudiants", this.getNbEtudiants());

        return mapdata;
    }
    public void setAttributs(HashMap<String, Object> attributs)  {

        this.setId((String) attributs.get("id"));
        this.setDesignation((String) attributs.get("designation"));
//        this.setNbEtudiants((Integer) attributs.get("nbEtudiants"));
    }

    public void ajouterDb() {

        String path = DbOperations.firebasePath(DbOperations.CYCLES, this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, this.getMap());
    }

    public void supprimerDb() {

        String path = DbOperations.firebasePath(DbOperations.CYCLES, this.getId());
        DbOperations.dbDelete(path);
    }
}
