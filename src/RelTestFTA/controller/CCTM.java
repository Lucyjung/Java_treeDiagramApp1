/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.controller;

import RelTestFTA.model.AdjacentNode;
import RelTestFTA.model.Condition;
import RelTestFTA.model.ConditionModel;
import RelTestFTA.model.UmlNode;

import java.util.ArrayList;

/**
 * Class Name  : CCTM 
 * Parameter   : UmlNodes - list of UmlNodes from Input class
 * 
 * Description : This class is designed to create CCTM for every decision node
 *               
 * Output      : CCTM - Condition Model for every decision node
 */
public class CCTM {
    ArrayList<UmlNode> UmlNodes;
    ArrayList<ConditionModel> ConditionModels = new ArrayList<ConditionModel> ();
    public CCTM (ArrayList<UmlNode> node){
        UmlNodes = node;
        createCCTM();
    }
    /**
     * Method Name : createCCTM 
     * Parameter   : None
     * Description : walk thru each decision node and create CCTM       
     * Output      : populated ConditionModels 
     */
    private void createCCTM() {
        for (UmlNode node : UmlNodes){
            if (node.isDecisionNode()){
                ConditionModel model = new ConditionModel();
                 // decision node should have only 1 source
                model.setName(node.getSources().get(0).getName());
                model.setId(node.getSources().get(0).getId());
                for (AdjacentNode target : node.getTargets()){
                    Condition condition = new Condition();
                    condition.setName(target.getCondition());
                    condition.setValid(target.isValid());
                    model.getConditions().add(condition);
                }
                ConditionModels.add(model);
                
            }
        }
    }
    /**
     * Method Name : printCCTMs
     * Parameter   : None
     * Description : print out CCTM list info
     * Output      : print data
     */
    public void printCCTMs() {
        for (ConditionModel model : ConditionModels){
            System.out.println("CCTM "+ ConditionModels.indexOf(model)+" name = " + model.getName());
            System.out.println("CCTM "+ ConditionModels.indexOf(model)+" id = " + model.getId());
            for (Condition condition : model.getConditions()){
                System.out.println("condition "+ model.getConditions().indexOf(condition)+" name = " + condition.getName());
                System.out.println("condition "+ model.getConditions().indexOf(condition)+" valid = " + condition.isValid());
            }
            System.out.println("---------------------------------------");
        }
    }
    public ArrayList<ConditionModel> getCCTM(){
        return ConditionModels;
    }
}
