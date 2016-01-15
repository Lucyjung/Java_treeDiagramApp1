/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.model;

import java.util.ArrayList;
/**
 *
 * @author Lucy
 */
public class ConditionModel {
    String name;
    String id;
    ArrayList<Condition> Conditions = new ArrayList<Condition> ();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Condition> getConditions() {
        return Conditions;
    }

    public void setConditions(ArrayList<Condition> conditions) {
        Conditions = conditions;
    }
}
