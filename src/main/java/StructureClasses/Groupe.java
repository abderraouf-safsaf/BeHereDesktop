package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Groupe extends Structure {

    private String idSection, idPromo, idSpecialite, idFilliere, idCycle;
    private int nbEtudiants;
    public Groupe() {

        super();
    }
    public Groupe(String designation)   {

        super(designation);
    }

    public String getIdSection() {
        return idSection;
    }

    public void setIdSection(String idSection) {
        this.idSection = idSection;
    }

    public String getIdPromo() {
        return idPromo;
    }

    public void setIdPromo(String idPromo) {
        this.idPromo = idPromo;
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
        mapdata.put("idFilliere", this.getIdFilliere());
        mapdata.put("idSpecialite", this.getIdSpecialite());
        mapdata.put("idPromo", this.getIdPromo());
        mapdata.put("idSection", this.getIdSection());
        mapdata.put("nbEtudiants", this.getNbEtudiants());

        return mapdata;
    }
    public void setAttributs(HashMap<String, Object> attributs) {

        this.setId((String) attributs.get("id"));
        this.setDesignation((String) attributs.get("designation"));
        this.setIdCycle((String) attributs.get("idCycle"));
        this.setIdFilliere((String) attributs.get("idFilliere"));
        this.setIdSpecialite((String) attributs.get("idSpecialite"));
        this.setIdPromo((String) attributs.get("idPromo"));
        this.setIdSection((String) attributs.get("idSection"));
        this.setNbEtudiants((Integer) attributs.get("nbEtudiants"));
    }

    public void ajouterDb() {

        String pathToPromo = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(), this.getIdFilliere(),
                this.getIdPromo(), this.getIdSection(), this.getId());
        String pathToModule = DbOperations.firebasePath(DbOperations.GROUPE_MODULES, this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(pathToPromo);
        DbOperations.dbPut(noeud, this.getMap());
        noeud = DbOperations.firebaseNoeud(pathToModule);
        DbOperations.dbPut(noeud, this.getMap());
    }

    public void supprimerDb() {
        supprimerGroupe();
        supprimerGroupeFormSection();
        supprimerGroupeFromEnseignant_Modules();

    }

    private void supprimerGroupe() {

        String path = DbOperations.firebasePath(DbOperations.GROUPE_MODULES, this.getId());
        DbOperations.dbDelete(path);
    }

    private void supprimerGroupeFormSection() {

        String path = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(), this.getIdFilliere(),
                this.getIdPromo(), this.getIdSection(), this.getId());
        DbOperations.dbDelete(path);
    }
    private  void supprimerGroupeFromEnseignant_Modules()   {

        String[] pathToPromo_Modules = {DbOperations.PROMO_MODULES.substring(1), this.getIdPromo()};

        HashMap<String, Module> promoModules = DbOperations.getChildren(Promo.class, Module.class, pathToPromo_Modules);

        for (Map.Entry<String, Module> module: promoModules.entrySet()) {

            String idModule = module.getKey();
            String[] pathToModule_Enseignats = {DbOperations.MODULE_ENSEIGNANTS.substring(1), idModule};
            HashMap<String, Enseignant> moduleEnseignantsHashMap = DbOperations.getChildren(Module.class, Enseignant.class, pathToModule_Enseignats);
            supprimerGroupeFormEnseignants(moduleEnseignantsHashMap, module.getValue());
        }
    }
    private void supprimerGroupeFormEnseignants(HashMap<String, Enseignant> enseignantHashMap, Module module) {


        for (Map.Entry<String, Enseignant> enseignant: enseignantHashMap.entrySet())   {

            String path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, enseignant.getKey(), module.getId(),
                    DbOperations.GROUPES, this.getId());
            DbOperations.dbDelete(path);
        }
    }
}
