package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Specialite extends Structure {

    public static final String SANS_SPECIALITE = "null";
    public static final Specialite PAS_DE_SPECIALITE ;
    static {

        PAS_DE_SPECIALITE = new Specialite("Aucun choix");
        PAS_DE_SPECIALITE.setId(SANS_SPECIALITE);
    }
    private String idFilliere, idCycle;
    private int nbEtudiants;

    public Specialite() {

        super();
    }
    public Specialite(String designation)   {

        super(designation);
    }

    public String getIdFilliere() {
        return idFilliere;
    }

    public void setIdFilliere(String idFilliere) {
        this.idFilliere = idFilliere;
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
        mapdata.put("idFilliere", this.getIdFilliere());
        mapdata.put("idCycle", this.getIdCycle());
        mapdata.put("designation", this.getDesignation());
        mapdata.put("nbEtudiants", this.getNbEtudiants());

        return mapdata;
    }

    public void setAttributs(HashMap<String, Object> attributs) {

        this.setId((String) attributs.get("id"));
        this.setDesignation((String) attributs.get("designation"));
        this.setIdFilliere((String) attributs.get("idFilliere"));
        this.setIdCycle((String) attributs.get("idCycle"));
        this.setNbEtudiants((Integer) attributs.get("nbEtudiants"));
    }

    public void ajouterDb() {

        String path1 = DbOperations.firebasePath(DbOperations.SPECIALITE_PROMOS, this.getId());
        Firebase noeud1 = DbOperations.firebaseNoeud(path1);

        DbOperations.dbPut(noeud1, this.getMap());
        String path2 = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, this.getIdFilliere(),
                this.getId());
        Firebase noeud2 = DbOperations.firebaseNoeud(path2);

        DbOperations.dbPut(noeud2, this.getMap());
    }

    public void supprimerDb() {

        String pathToFillieres = DbOperations.firebasePath(DbOperations.FILIERE_SPECIALITES, this.getIdFilliere(),
                this.getId());
        String pathToSpecialitePromos = DbOperations.firebasePath(DbOperations.SPECIALITE_PROMOS,
                this.getId());

        DbOperations.dbDelete(pathToFillieres);
        DbOperations.dbDelete(pathToSpecialitePromos);
    }
}

