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
public class TestCase {
    ArrayList <ConditionModel> ConditionModels = new ArrayList <ConditionModel>();
    boolean valid;

    public ArrayList<ConditionModel> getConditionModels() {
        return ConditionModels;
    }

    public void setConditionModels(ArrayList<ConditionModel> conditionModels) {
        ConditionModels = conditionModels;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
