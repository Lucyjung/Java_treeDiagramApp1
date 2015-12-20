/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javatest1;

import java.util.ArrayList;

/**
 *
 * @author Z510
 */
public class TestCaseTable {
    private ArrayList<TestCase> testcases = new ArrayList<TestCase>();
    private ArrayList<umlNode> umlNodes = new ArrayList<umlNode>();
    private TestCase temp = new TestCase();
    boolean workToDo = false;
    private int processedSource;
    public void generateTestCaseTable(ArrayList<umlNode> nodes, umlNode finalNode){
        umlNodes = nodes;
        processedSource = 0;
        for (adjacentNode goal : finalNode.sources){
            umlNode startNode = nodes.get(goal.index);
            do {
                workToDo = false;
                recur_node(startNode,startNode.validPath);
                temp.valid = startNode.validPath;
                testcases.add(temp);
                temp = new TestCase();
            }while (workToDo);
        }
    }
    private boolean recur_node(umlNode node, boolean flag){
        
        boolean mutlisource = false;
        for (adjacentNode source :node.sources){
            if (processedSource > 0){
                processedSource--;
                continue;
            }
            if (mutlisource){
                workToDo = true;
                processedSource++;
                return mutlisource;
            }
            umlNode next  = umlNodes.get(source.index);
            mutlisource = recur_node(next,flag);
            
        }
        if (node.decisionNode){
            ArrayList<ConditionModel> ConditionModel = new ArrayList<ConditionModel> ();
            ConditionModel model = new ConditionModel();
                 // decision node should have only 1 source
                model.name = node.sources.get(0).name;
                model.id = node.sources.get(0).id;
                for (adjacentNode target : node.targets){
                    if (target.valid == flag){
                        Condition condition = new Condition();
                        condition.name = target.condition;
                        condition.valid = target.valid;
                        model.Conditions.add(condition);
                    }
                }
                temp.ConditionModels.add(model);
                
        }
        return true;
    }
    public void printTestCases() {
        for (TestCase testcase : testcases){
            System.out.println("Tesecase "+ testcases.indexOf(testcase));
            System.out.println("Tesecase valid = "+ testcase.valid);
            for (ConditionModel model : testcase.ConditionModels){
               System.out.println("CCTM "+ testcase.ConditionModels.indexOf(model)+" name = " + model.name);
               System.out.println("CCTM "+ testcase.ConditionModels.indexOf(model)+" id = " + model.id);
               for (Condition condition : model.Conditions){
                   System.out.println("condition "+ model.Conditions.indexOf(condition)+" name = " + condition.name);
                   System.out.println("condition "+ model.Conditions.indexOf(condition)+" valid = " + condition.valid);
               }
            }
            System.out.println("---------------------------------------");
        }

    }
}
