package StructureClasses;


import java.util.HashMap;
import java.util.Map;

public abstract class Structure implements Identifiable {

    private String id, designation;

    public Structure()   {   }
    public Structure(String designation) {

        this.designation = designation;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }


    @Override
    public String toString()    {

        return this.designation;
    }
}
