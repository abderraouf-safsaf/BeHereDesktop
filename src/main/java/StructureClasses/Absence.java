package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;

import java.util.HashMap;
import java.util.Map;


public class Absence implements Identifiable {

    private String idSeance, idGroupe, idSection, idPromo, idFilliere, idCycle, idModule, idEnseignant, idEtudiant, id, typeSeance, date;

    private Boolean justifier = false;

    public Absence()    {    }

    public Boolean getJustifier() {
        return justifier;
    }

    public void setJustifier(Boolean justifier) {
        this.justifier = justifier;
    }

    public String getTypeSeance() {
        return typeSeance;
    }

    public void setTypeSeance(String typeSeance) {
        this.typeSeance = typeSeance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdSeance() {
        return idSeance;
    }

    public void setIdSeance(String idSeance) {
        this.idSeance = idSeance;
    }

    public String getIdGroupe() {
        return idGroupe;
    }

    public void setIdGroupe(String idGroupe) {
        this.idGroupe = idGroupe;
    }

    public String getIdSection() {
        return idSection;
    }

    public void setIdSection(String idSection) {
        this.idSection = idSection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPromo() {
        return idPromo;
    }

    public void setIdPromo(String idPromo) {
        this.idPromo = idPromo;
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

    public String getIdModule() {
        return idModule;
    }

    public void setIdModule(String idModule) {
        this.idModule = idModule;
    }

    public String getIdEnseignant() {
        return idEnseignant;
    }

    public void setIdEnseignant(String idEnseignant) {
        this.idEnseignant = idEnseignant;
    }

    public String getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(String idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public Map<String, Object> getMap() {

        Map<String, Object> mapdata = new HashMap<String, Object>();

        mapdata.put("id", this.getId());
        mapdata.put("idCycle", this.getIdCycle());
        mapdata.put("idFilliere", this.getIdFilliere());
        mapdata.put("typeSeance", this.getTypeSeance());
        mapdata.put("idPromo", this.getIdPromo());
        mapdata.put("date", this.getDate());
        mapdata.put("idSection", this.getIdSection());
        mapdata.put("idGroupe", this.getIdGroupe());
        mapdata.put("idEnseignant", this.getIdEnseignant());
        mapdata.put("idModule", this.getIdModule());
        mapdata.put("idSeance", this.getIdSeance());
        mapdata.put("idEtudiant", this.getIdEtudiant());
        mapdata.put("justifier", this.getJustifier());
        return mapdata;
    }

    public void setAttributs(HashMap<String, Object> attributs) {

        this.setId((String) attributs.get("id"));
        this.setIdCycle((String) attributs.get("idCycle"));
        this.setIdFilliere((String) attributs.get("idFilliere"));
        this.setDate((String) attributs.get("date"));
        this.setIdPromo((String) attributs.get("idPromo"));
        this.setIdSection((String) attributs.get("idSection"));
        this.setIdGroupe((String) attributs.get("idGroupe"));
        this.setIdEnseignant((String) attributs.get("idEnseignant"));
        this.setIdModule((String) attributs.get("idModule"));
        this.setIdSeance((String) attributs.get("idSeance"));
        this.setIdEtudiant((String) attributs.get("idEtudiant"));
        this.setTypeSeance((String) attributs.get("typeSeance"));
        this.setJustifier((Boolean) attributs.get("justifier"));
    }



    @Override
    public void ajouterDb() {

        String path;
        if(typeSeance.equals(DbOperations.GROUPES))
            path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, this.getIdEnseignant(), this.getIdModule(), typeSeance
                    , getIdGroupe(), this.getIdSeance(), this.getId());
        else
            path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, this.getIdEnseignant(), this.getIdModule(), typeSeance
                    , getIdSection(), this.getIdSeance(), this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, this.getMap());

        path=DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(), this.getIdFilliere(), this.getIdPromo(), this.getIdSection(),
                this.getIdGroupe(), this.getIdEtudiant(), this.getIdModule(), this.getId());

        noeud=DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, this.getMap());

    }

    @Override
    public String toString() {
        return "Absence{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public void supprimerDb() {

    }
}
