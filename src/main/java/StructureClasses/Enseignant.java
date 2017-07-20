package StructureClasses;

import FirebaseClasses.DbOperations;
import FirebaseClasses.Firebase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Enseignant extends Personne {

    public static final String PR = "Pr", MCA = "MCA", MCB = "MCB", MAA = "MAA",
            MAB = "MAB";

    private String grade;

    public Enseignant() {

        super();
    }
    public Enseignant(String nom, String prenom, char sexe)    {

        super(nom, prenom, sexe);
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Map<String, Object> getMap(){

        Map<String, Object> mapdata = new LinkedHashMap<String, Object>();

        mapdata.put("id", this.getId());
        mapdata.put("nom", this.getNom());
        mapdata.put("prenom", this.getPrenom());
        mapdata.put("email", this.getEmail());
        mapdata.put("sexe", this.getSexe());
        mapdata.put("grade", getGrade());

        return mapdata;
    }

    public void setAttributs(HashMap<String, Object> attributs) {

        this.setId((String) attributs.get("id"));
        this.setNom((String) attributs.get("nom"));
        this.setPrenom((String) attributs.get("prenom"));
        this.setEmail((String) attributs.get("email"));
        this.setSexe(((String)attributs.get("sexe")).charAt(0));
        this.setGrade((String) attributs.get("grade"));
    }

    public void ajouterDb() {

        String path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, this.getMap());
    }

    public void supprimerDb() {

        String[] path0 = {DbOperations.ENSEIGNANT_MODULE.substring(1), this.getId()};

        String path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, this.getId());


        HashMap<String, Module> enseignantModulesHashMap = DbOperations.getChildren(Enseignant.class,
                Module.class, path0);

        for (Map.Entry<String, Module> module: enseignantModulesHashMap.entrySet()){

            Module module1 = module.getValue();
            supprimerEnseignantFormModule(module1);
        }
        DbOperations.dbDelete(path);
    }
    private void supprimerEnseignantFormModule(Module module){

        String path = DbOperations.firebasePath(DbOperations.MODULE_ENSEIGNANTS,module.getId(),this.getId());
        DbOperations.dbDelete(path);
    }
    public void affecterModule(Module module)    {

        String path1 = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, this.getId(),
                module.getId());
        String path2 = DbOperations.firebasePath(DbOperations.MODULE_ENSEIGNANTS, module.getId(),
                this.getId());

        Firebase noeud1 = DbOperations.firebaseNoeud(path1);
        Firebase noeud2 = DbOperations.firebaseNoeud(path2);

        DbOperations.dbPut(noeud1, module.getMap());
        DbOperations.dbPut(noeud2, this.getMap());
    }
    public void affecterGroupe(Module module, Groupe groupe) {

        String pathToEnseignant_Module = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE,
                this.getId(), module.getId(), DbOperations.GROUPES, groupe.getId());
        String pathToGroupe = DbOperations.firebasePath(DbOperations.GROUPE_MODULES, groupe.getId(), module.getId(), this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(pathToEnseignant_Module);
        DbOperations.dbPut(noeud, groupe.getMap());

        noeud=DbOperations.firebaseNoeud(pathToGroupe);
        DbOperations.dbPut(noeud,this.getMap());
    }
    public void affecterSection(Module module, Section section)  {

        String path = DbOperations.firebasePath(DbOperations.ENSEIGNANT_MODULE, this.getId(),
                module.getId(), DbOperations.SECTIONS, section.getId());
        String pathToSection = DbOperations.firebasePath(DbOperations.SECTION_MODULES, section.getId(), module.getId(), this.getId());

        Firebase noeud = DbOperations.firebaseNoeud(path);
        DbOperations.dbPut(noeud, section.getMap());

        noeud=DbOperations.firebaseNoeud(pathToSection);
        DbOperations.dbPut(noeud,this.getMap());
    }
}
