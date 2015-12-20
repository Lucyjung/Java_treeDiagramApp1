/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javatest1;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Z510
 */
public class CCTM {
    ArrayList<umlNode> umlNodes;
    ArrayList<ConditionModel> ConditionModels = new ArrayList<ConditionModel> ();
    public CCTM (ArrayList<umlNode> node){
        umlNodes = node;
        createCCTM();
    }
    private void createCCTM() {
        for (umlNode node : umlNodes){
            if (node.decisionNode){
                ConditionModel model = new ConditionModel();
                 // decision node should have only 1 source
                model.name = node.sources.get(0).name;
                model.id = node.sources.get(0).id;
                for (adjacentNode target : node.targets){
                    Condition condition = new Condition();
                    condition.name = target.condition;
                    condition.valid = target.valid;
                    model.Conditions.add(condition);
                }
                ConditionModels.add(model);
                
            }
        }
    }
    public void printCCTMs() {
        for (ConditionModel model : ConditionModels){
            System.out.println("CCTM "+ ConditionModels.indexOf(model)+" name = " + model.name);
            System.out.println("CCTM "+ ConditionModels.indexOf(model)+" id = " + model.id);
            for (Condition condition : model.Conditions){
                System.out.println("condition "+ model.Conditions.indexOf(condition)+" name = " + condition.name);
                System.out.println("condition "+ model.Conditions.indexOf(condition)+" valid = " + condition.valid);
            }
            System.out.println("---------------------------------------");
        }
    }
}
