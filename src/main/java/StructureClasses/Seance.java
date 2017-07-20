package StructureClasses;



import com.sun.javafx.image.IntPixelGetter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Seance implements Identifiable {

    private String id;
    private String idSection, idGroupe;
    private String idEnseignant;
    private String idModule;
    private String typeSeance;
    private String date;
    private String heureDebut, minuteDebut;



    public Seance(){    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getMinuteDebut() {
        return minuteDebut;
    }

    public void setMinuteDebut(String minuteDebut) {
        this.minuteDebut = minuteDebut;
    }

    public String getIdSection() {
        return idSection;
    }

    public void setIdSection(String idSection) {
        this.idSection = idSection;
    }

    public String getIdGroupe() {
        return idGroupe;
    }

    public void setIdGroupe(String idGroupe) {
        this.idGroupe = idGroupe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEnseignant() {
        return idEnseignant;
    }

    public void setIdEnseignant(String idEnseignant) {
        this.idEnseignant = idEnseignant;
    }


    public String getIdModule() {
        return idModule;
    }

    public void setIdModule(String idModule) {
        this.idModule = idModule;
    }

    public String getTypeSeance() {
        return typeSeance;
    }

    public void setTypeSeance(String typeSeance) {
        this.typeSeance = typeSeance;
    }

    public Map<String, Object> getMap(){

        Map<String, Object> mapdata = new LinkedHashMap<String, Object>();

        mapdata.put("id", getId());
        mapdata.put("idSection", getIdSection());
        mapdata.put("idGroupe", getIdGroupe());
        mapdata.put("idEnseignant", getIdEnseignant());
        mapdata.put("idModule", getIdModule());
        mapdata.put("typeSeance", getTypeSeance());
        mapdata.put("date", getDate());
        mapdata.put("minuteDebut", getMinuteDebut());
        mapdata.put("heureDebut", getHeureDebut());

        return mapdata;
    }

    @Override
    public void setAttributs(HashMap<String, Object> attributs) {

        this.setId((String) attributs.get("id"));
        this.setIdSection((String) attributs.get("idSection"));
        this.setIdGroupe((String) attributs.get("idGroupe"));
        this.setIdEnseignant((String) attributs.get("idEnseignant"));
        this.setIdModule((String) attributs.get("idModule"));
        this.setTypeSeance((String) attributs.get("typeSeance"));
        this.setDate((String) attributs.get("date"));
        this.setMinuteDebut((String) attributs.get("minuteDebut"));
        this.setHeureDebut((String) attributs.get("heureDebut"));

    }

    @Override
    public void ajouterDb() {

    }

    @Override
    public void supprimerDb() {

    }

    @Override
    public String toString() {
        return String.format(" %s - %s:%s ", getDate(), getHeureDebut(), getMinuteDebut());
    }
}