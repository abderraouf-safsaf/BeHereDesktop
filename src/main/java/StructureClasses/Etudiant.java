package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;

import java.util.HashMap;
import java.util.Map;


public class Etudiant extends Personne implements Identifiable {


    private String idGroupe, idSection, idPromo, idSpecialite, idFilliere, idCycle;
    private int nbAbsences = 0;
    private String imageBase64;

    public Etudiant()   {   }
    public Etudiant(String nom, String prenom, char sexe)  {

        super(nom, prenom, sexe);

    }

    public String getImageBase64() { return imageBase64; }

    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public String getIdGroupe() {
        return idGroupe;
    }

    public void setIdGroupe(String idGroupe) { this.idGroupe = idGroupe; }

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

    public int getNbAbsences() {
        return nbAbsences;
    }

    public void setNbAbsences(int nbAbsences) {
        this.nbAbsences = nbAbsences;
    }

    public Map<String, Object> getMap() {

        Map<String, Object> mapData = new HashMap<String, Object>();

        mapData.put("id", this.getId());
        mapData.put("idCycle", this.getIdCycle());
        mapData.put("idFilliere", this.getIdFilliere());
        mapData.put("idSpecialite", this.getIdSpecialite());
        mapData.put("idPromo", this.getIdPromo());
        mapData.put("idSection", this.getIdSection());
        mapData.put("idGroupe", this.getIdGroupe());
        mapData.put("imageBase64", this.getImageBase64());
        mapData.put("nom", this.getNom());
        mapData.put("prenom", this.getPrenom());
        mapData.put("email", this.getEmail());
        mapData.put("sexe", this.getSexe());
        mapData.put("nbAbsences", this.getNbAbsences());

        return mapData;
    }
    public void setAttributs(HashMap<String, Object> attributs) {

        this.setId((String) attributs.get("id"));
        this.setIdCycle((String) attributs.get("idCycle"));
        this.setIdFilliere((String) attributs.get("idFilliere"));
        this.setIdSpecialite((String) attributs.get("idSpecialite"));
        this.setIdPromo((String) attributs.get("idPromo"));
        this.setIdSection((String) attributs.get("idSection"));
        this.setIdGroupe((String) attributs.get("idGroupe"));
        this.setImageBase64((String) attributs.get("imageBase64"));
        this.setNom((String) attributs.get("nom"));
        this.setPrenom((String) attributs.get("prenom"));
        this.setEmail((String) attributs.get("email"));
        this.setSexe(((String)attributs.get("sexe")).charAt(0));
        this.setNbAbsences((Integer)attributs.get("nbAbsences"));

    }

    public void ajouterDb() {

        String path = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(),
                this.getIdFilliere(), this.getIdPromo(), this.getIdSection(), this.getIdGroupe(),
                this.getId());
        setImageBase64(DEFAULT_IMAGD_BASE64_STRING);
        Firebase noeud = DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, this.getMap());
        Personne.DEFAULT_IMAGD_BASE64_STRING = "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";


        //incrementerNbEtudiants();
    }
    private void incrementerNbEtudiants() {

        if (this.getIdSpecialite() != Specialite.SANS_SPECIALITE) {
            String path = DbOperations.firebasePath(DbOperations.SPECIALITE_PROMOS, this.getIdSpecialite(),
                    this.getIdPromo());
            incrementerNbEtudiantsChamp(path);
        }

        String pathToGroupe = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(),
                this.getIdFilliere(), this.getIdPromo(), this.getIdSection(), this.getIdGroupe());
        incrementerNbEtudiantsChamp(pathToGroupe);

        String pathToSection = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(),
                this.getIdFilliere(), this.getIdPromo(), this.getIdSection());
        incrementerNbEtudiantsChamp(pathToSection);

        String pathToPromo = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(),
                this.getIdFilliere(), this.getIdPromo());
        incrementerNbEtudiantsChamp(pathToPromo);

        String pathToFilliere = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(),
                this.getIdFilliere());
        incrementerNbEtudiantsChamp(pathToFilliere);

        String pathToCycle = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle());
        incrementerNbEtudiantsChamp(pathToCycle);
    }

    private void incrementerNbEtudiantsChamp(String path){

        Firebase noeud = DbOperations.firebaseNoeud(path);

        Map<String, Object> dataMap = DbOperations.dbGet(path);

        Integer nbEtudiants = (Integer) dataMap.get("nbEtudiants");
        nbEtudiants++;
        dataMap.put("nbEtudiants", nbEtudiants);

        DbOperations.dbPut(noeud, dataMap);

    }
    public void supprimerDb() {

        String path1 = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(), this.getIdFilliere(),
                this.getIdPromo(), this.getIdSection(), this.getIdGroupe(), this.getId());
        DbOperations.dbDelete(path1);

        //decrementerNbEtudiants();
    }
    private void decrementerNbEtudiants() {
        String path = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(), this.getIdFilliere(),
                this.getIdPromo(), this.getIdSection(), this.getIdGroupe());
        decrementerNbEtuddiantsChamp(path);
        path = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(), this.getIdFilliere(),
                this.getIdPromo(), this.getIdSection());
        decrementerNbEtuddiantsChamp(path);
        path = DbOperations.firebasePath(DbOperations.CYCLES, this.getIdCycle(), this.getIdFilliere(), this.getIdPromo());
        decrementerNbEtuddiantsChamp(path);
        if (this.getIdSpecialite() != Specialite.SANS_SPECIALITE) {
            path = DbOperations.firebasePath(DbOperations.SPECIALITE_PROMOS, this.getIdSpecialite(),
                    this.getIdPromo());
            decrementerNbEtuddiantsChamp(path);
        }
    }

    public String etat(Seance seance, HashMap<Seance, Absence> testHashMap){

        boolean absence=false;
        Absence absenceO=null;

        for (Map.Entry<Seance, Absence> absenceEntry : testHashMap.entrySet()) {

            if(seance.getId().equals(absenceEntry.getKey().getId()))
                if(absenceEntry.getValue()!=null){
                    absenceO=absenceEntry.getValue();
                    absence=true;
                    break;
                }
        }

        if (absence)
            if(justifier(absenceO))
                return " X  Justifier ";
            else
                return " X ";
        else
            return "  ";
    }

    private boolean justifier(Absence absence) {
        String[] path = {DbOperations.CYCLES.substring(1), idCycle, idFilliere, idPromo, idSection,
                idGroupe, getId(), absence.getIdModule(), absence.getId()};
        HashMap<String, Justification> hashMap=DbOperations.getChildren(Absence.class, Justification.class, path);
        if(hashMap.size()!=0)
            return true;
        else
            return false;
    }

    @Override
    public boolean equals(Object o) {
        if(o!=null)
            if(((Etudiant)o).getId().equals(this.getId()))
                return true;
            else
                return false;
        else
            return false;
    }

    private void decrementerNbEtuddiantsChamp(String path){

        Firebase noeud = DbOperations.firebaseNoeud(path);

        Map<String, Object> dataMap = DbOperations.dbGet(path);

        Integer nbEtudiants = (Integer) dataMap.get("nbEtudiants");
        nbEtudiants--;
        dataMap.put("nbEtudiants", nbEtudiants);

        DbOperations.dbPut(noeud, dataMap);
    }

    @Override
    public String toString() {
        return getNom()+" "+getPrenom();
    }

}
