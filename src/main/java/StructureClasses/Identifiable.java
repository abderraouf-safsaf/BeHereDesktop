package StructureClasses;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by redjohn on 10/03/17.
 */
public interface Identifiable {

    public Map<String, Object> getMap();
    public void setAttributs(HashMap<String, Object> attributs);
    public void ajouterDb();
    public void supprimerDb();
}
