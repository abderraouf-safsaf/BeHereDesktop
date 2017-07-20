package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Promo extends Structure {

    private String idSpecialite, idFilliere, idCycle;
    private String annee;
    private int niveau, nbEtudiants;

    public Promo()  {

        super();
    }
    public Promo(String designation, String annee)    {

        super(designation);
        this.annee = annee;
    }

    public String getIdSpecialite() {
        return idSpecialite;
    }

    public void setIdSpecialite(String idSpecialite) {
        this.idSpecialite = idSpecialite;
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

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
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
        mapdata.put("idSpecialite", this.getIdSpecialite());
        mapdata.put("idFilliere", this.getIdFilliere());
        mapdata.put("idCycle", this.getIdCycle());
        mapdata.put("annee", this.getAnnee());
        mapdata.put("niveau", this.getNiveau());
        mapdata.put("nbEtudiants", this.getNbEtudiants());

        return mapdata;
    }

    public void setAttributs(HashMap<String, Object> attributs) {

        this.setId((String) attributs.get("id"));
        this.setDesignation((String) attributs.get("designation"));
        this.setIdSpecialite((String) attributs.get("idSpecialite"));
        this.setIdFilliere((String) attributs.get("idFilliere"));
        this.setIdCycle((String) attributs.get("idCycle"));
        this.setAnnee(new String((String) (attributs.get("annee"))));
        this.setNiveau((Integer) attributs.get("niveau"));
        this.setNbEtudiants((Integer) attributs.get("nbEtudiants"));
    }

    public void ajouterDb() {

        String pathToFilliere = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(),
                this.getIdFilliere(), this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(pathToFilliere);

        DbOperations.dbPut(noeud, this.getMap());

        if (!this.getIdSpecialite().equals(Specialite.SANS_SPECIALITE)) {

            String pathToSpecialite_Promo = DbOperations.firebasePath(DbOperations.SPECIALITE_PROMOS, this.getIdSpecialite(),
                                                                                                this.getId());
            noeud = DbOperations.firebaseNoeud(pathToSpecialite_Promo);
            DbOperations.dbPut(noeud, this.getMap());
        }
    }

    public void supprimerDb() {

        String pathToCycles = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(),
                this.getIdFilliere(), this.getId());
        String pathToSpecialitePromos = DbOperations.firebasePath(DbOperations.SPECIALITE_PROMOS,
                this.getIdSpecialite(), this.getId());
        String pathToPromoModules = DbOperations.firebasePath(DbOperations.PROMO_MODULES,
                this.getId());

        DbOperations.dbDelete(pathToCycles);
        DbOperations.dbDelete(pathToSpecialitePromos);
        DbOperations.dbDelete(pathToPromoModules);
    }
}
