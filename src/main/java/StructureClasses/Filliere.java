package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Filliere extends Structure {

    private int nbEtudiants;
    private String idCycle;

    public Filliere()   {

        super();
    }
    public Filliere(String designation) {

        super(designation);
    }

    public String getIdCycle() {
        return idCycle;
    }

    public void setIdCycle(String idCycle) {
        this.idCycle = idCycle;
    }

    public int getNbEtudiants() {
        return nbEtudiants;
    }
    public void setNbEtudiants(int nbEtudiants) {
        this.nbEtudiants = nbEtudiants;
    }
    public Map<String, Object> getMap(){

        Map<String, Object> mapdata = new LinkedHashMap<String, Object>();

        mapdata.put("id", this.getId());
        mapdata.put("designation", this.getDesignation());
        mapdata.put("idCycle", this.getIdCycle());
        mapdata.put("nbEtudiants", this.getNbEtudiants());

        return mapdata;
    }
    public void setAttributs(HashMap<String, Object> attributs)  {

        this.setId((String) attributs.get("id"));
        this.setDesignation((String) attributs.get("designation"));
        this.setIdCycle((String) attributs.get("idCycle"));
        this.setNbEtudiants((Integer) attributs.get("nbEtudiants"));
    }

    public void ajouterDb() {

        String path = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(), this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, this.getMap());

        String path2 = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, this.getId());

        Firebase noeud2 = DbOperations.firebaseNoeud(path2);
        DbOperations.dbPut(noeud2, this.getMap());
    }

    public void supprimerDb() {

        String path = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(),
                this.getId());
        DbOperations.dbDelete(path);

        String path2 = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, this.getId());
        DbOperations.dbDelete(path2);
    }
    public boolean hasSpecialites() {

        String[] pathToFilliereSpecialites = {DbOperations.FILIERE_SPECIALITES.substring(1),
                this.getId()};
        HashMap<String, Specialite> specialitesHashMap = DbOperations.getChildren(Filliere.class, Specialite.class, pathToFilliereSpecialites);

        return (specialitesHashMap.size() > 0);
    }
}
