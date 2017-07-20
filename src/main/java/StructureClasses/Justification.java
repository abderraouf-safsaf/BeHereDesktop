package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;

import java.util.HashMap;
import java.util.Map;


public class Justification implements Identifiable {

    private String idSeance, idGroupe, idSection, idPromo, idSpecialite,
            idFilliere, idCycle, idModule, idEnseignant, idEtudiant, idAbsence, motif, id;

    public Justification() {
    }

    public String getIdAbsence() {
        return idAbsence;
    }

    public void setIdAbsence(String idAbsence) {
        this.idAbsence = idAbsence;
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

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Map<String, Object> getMap() {

        Map<String, Object> mapdata = new HashMap<String, Object>();

        mapdata.put("id", this.getId());
        mapdata.put("idCycle", this.getIdCycle());
        mapdata.put("idFilliere", this.getIdFilliere());
        mapdata.put("idSpecialite", this.getIdSpecialite());
        mapdata.put("idPromo", this.getIdPromo());
        mapdata.put("idSection", this.getIdSection());
        mapdata.put("idGroupe", this.getIdGroupe());
        mapdata.put("idEtudiant", this.getIdEtudiant());
        mapdata.put("idEnseignant", this.getIdEnseignant());
        mapdata.put("idModule", this.getIdModule());
        mapdata.put("idSeance", this.getIdSeance());
        mapdata.put("idAbsence", this.getIdAbsence());
        mapdata.put("motif", this.getMotif());
        return mapdata;
    }

    public void setAttributs(HashMap<String, Object> attributs) {

        this.setId((String) attributs.get("id"));
        this.setIdCycle((String) attributs.get("idCycle"));
        this.setIdFilliere((String) attributs.get("idFilliere"));
        this.setIdSpecialite((String) attributs.get("idSpecialite"));
        this.setIdPromo((String) attributs.get("idPromo"));
        this.setIdSection((String) attributs.get("idSection"));
        this.setIdGroupe((String) attributs.get("idGroupe"));
        this.setIdEtudiant((String) attributs.get("idEtudiant"));
        this.setIdEnseignant((String) attributs.get("idEnseignant"));
        this.setIdModule((String) attributs.get("idModule"));
        this.setIdSeance((String) attributs.get("idSeance"));
        this.setIdAbsence((String) attributs.get("idAbsence"));
        this.setMotif((String) attributs.get("motif"));
    }

    @Override
    public void ajouterDb() {

    }

    @Override
    public void supprimerDb() {

    }


    public void ajouterJustification(Absence absence) {

        absence.setJustifier(true);
        absence.ajouterDb();

        String path;
        if(absence.getTypeSeance().equals(DbOperations.GROUPES))
            path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, this.getIdEnseignant(), this.getIdModule(), absence.getTypeSeance()
                , getIdGroupe(), this.getIdSeance(), this.getIdAbsence(), this.getId());
        else
            path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, this.getIdEnseignant(), this.getIdModule(), absence.getTypeSeance()
                    , getIdSection(), this.getIdSeance(), this.getIdAbsence(), this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, this.getMap());

        path=DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(), this.getIdFilliere(), this.getIdPromo(), this.getIdSection(),
                this.getIdGroupe(), this.getIdEtudiant(), this.getIdModule(), this.getIdAbsence(), this.getId());

        noeud=DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, this.getMap());
    }
}
