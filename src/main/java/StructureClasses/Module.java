package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;
import java.util.HashMap;
import java.util.Map;


public class Module extends Structure {

    private String idPromo, idSpecialite, idFilliere, idCycle;
    private int coeff;
    private int credit;
    private int semestre;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private int score;
    private String remarque;
    private double VHcours, VHtd, VHtp;

    public Module() {

        super();
    }
    public Module(String designation)   {

        super(designation);
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
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

    public int getCoeff() {
        return coeff;
    }

    public void setCoeff(int coeff) {
        this.coeff = coeff;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public double getVHcours() {
        return VHcours;
    }

    public void setVHcours(double VHcours) {
        this.VHcours = VHcours;
    }

    public double getVHtd() {
        return VHtd;
    }

    public void setVHtd(double VHtd) {
        this.VHtd = VHtd;
    }

    public double getVHtp() {
        return VHtp;
    }

    public void setVHtp(double VHtp) {
        this.VHtp = VHtp;
    }

    public String getIdPromo() {
        return idPromo;
    }

    public void setIdPromo(String idPromo) {
        this.idPromo = idPromo;
    }

    public Map<String, Object> getMap() {

        Map<String, Object> mapData = new HashMap<String, Object>();

        mapData.put("id", this.getId());
        mapData.put("designation", this.getDesignation());
        mapData.put("idCycle", this.getIdCycle());
        mapData.put("idFilliere", this.getIdFilliere());
        mapData.put("idSpecialite", this.getIdSpecialite());
        mapData.put("idPromo", this.getIdPromo());
        mapData.put("coeff", this.getCoeff());
        mapData.put("credit", this.getCredit());
        mapData.put("semestre", this.getSemestre());
        mapData.put("VHcours", this.getVHcours());
        mapData.put("VHtd", this.getVHtd());
        mapData.put("VHtp", this.getVHtp());
        mapData.put("score", this.getScore());
        mapData.put("remarque", this.getRemarque());


        return mapData;
    }

    public void setAttributs(HashMap<String, Object> attributs) {

        this.setId((String) attributs.get("id"));
        this.setDesignation((String) attributs.get("designation"));
        this.setIdCycle((String) attributs.get("idCycle"));
        this.setIdFilliere((String) attributs.get("idFilliere"));
        this.setIdSpecialite((String) attributs.get("idSpecialite"));
        this.setIdPromo((String) attributs.get("idPromo"));
        this.setCoeff((Integer) attributs.get("coeff"));
        this.setCredit((Integer) attributs.get("credit"));
        this.setSemestre((Integer) attributs.get("semestre"));
        this.setVHcours((Double) attributs.get("VHcours"));
        this.setVHtd((Double) attributs.get("VHtd"));
        this.setVHtp((Double) attributs.get("VHtp"));
    }

    public void ajouterDb() {

        String path = DbOperations.firebasePath(DbOperations.MODULE_ENSEIGNANTS, this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, this.getMap());

        String pathToPromoModules = DbOperations.firebasePath(DbOperations.PROMO_MODULES,
                this.getIdPromo(), this.getId());

        Firebase noeud2 = DbOperations.firebaseNoeud(pathToPromoModules);
        DbOperations.dbPut(noeud2, this.getMap());
    }

    public void supprimerDb() {

        String[] pathToModule_Enseignantsl = {DbOperations.MODULE_ENSEIGNANTS.substring(1),
                this.getId()};

        String pathToModule_Enseignants = DbOperations.firebasePath(DbOperations.MODULE_ENSEIGNANTS, this.getId());

        HashMap<String, Enseignant> enseignantsHashMap = DbOperations.getChildren(Module.class, Enseignant.class, pathToModule_Enseignantsl);

            for (Map.Entry<String, Enseignant> enseignant: enseignantsHashMap.entrySet())   {

            String pathToEnseignant_Modules = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, enseignant.getKey(), this.getId());

            DbOperations.dbDelete(pathToEnseignant_Modules);
        }
        DbOperations.dbDelete(pathToModule_Enseignants);

        HashMap<String, Promo> modulePromos = getModulePromos(this);

            for (Map.Entry<String, Promo> promo: modulePromos.entrySet())  {

            DbOperations.dbDelete(DbOperations.firebasePath(DbOperations.PROMO_MODULES, promo.getKey(),
                    this.getId()));
        }
    }
    private HashMap<String, Promo> getModulePromos(Module module)   {

        String[] path = {DbOperations.CYCLES.substring(1), module.getIdCycle(), module.getIdFilliere()};
        return (DbOperations.getChildren(Filliere.class, Promo.class, path));
    }
}
