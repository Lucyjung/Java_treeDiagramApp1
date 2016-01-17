/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.controller;

import RelTestFTA.model.*;

import java.util.ArrayList;
import java.util.Collections;
/**
 * Class Name  : TestCaseTable 
 * Parameter   : UmlNodes - list of UmlNodes from Input class
 *             : finalNode - final UmlNode
 * Description : create test case table for each condition
 *               
 * Output      : list of testcases
 */
public class TestCaseTable {
    private ArrayList<TestCase> testcases = new ArrayList<TestCase>();
    private ArrayList<UmlNode> UmlNodes = new ArrayList<UmlNode>();
    private TestCase temp = new TestCase();
    boolean workToDo = false;
    private int processedSource;

    public TestCaseTable (ArrayList<UmlNode> nodes, UmlNode finalNode){
        UmlNodes = nodes;
        generateTestCaseTable(nodes,finalNode);
    }
    /**
     * Method Name : getTestCaseTable 
     * Parameter   : None
     * Description : return populated testcases      
     * Output      : populated testcases   
     */
    public ArrayList<TestCase> getTestCaseTable(){
        return testcases;
    }
    /**
     * Method Name : generateTestCaseTable 
     * Parameter   : UmlNodes - list of UmlNodes from Input class
     *             : finalNode - final UmlNode
     * Description : recursive walk thru every possible paths.
     *               Also, create CCTM and connect to the matched condition
     * Output      : populated testcases   
     */
    private void generateTestCaseTable(ArrayList<UmlNode> nodes, UmlNode finalNode){
        
        processedSource = 0;
        for (AdjacentNode goal : finalNode.getSources()){
            UmlNode startNode = nodes.get(goal.getIndex());
            do {
                workToDo = false;
                getDraftTestCase_recursive(startNode);
                temp.setValid(startNode.isValidPath());
                Collections.reverse(temp.getConditionModels());
                testcases.add(temp);
                temp = new TestCase();
            }while (workToDo);
        }
    }
    /**
     * Method Name : getDraftTestCase_recursive 
     * Parameter   : UmlNode - start node to find the path to initial node
     *     
     * Description : Given node, this function will recursively walk until initial node 
     *               and return back to the caller with WorkToDo flag
     * Output      : workToDo - if set, mean caller is required to call one more time
     *             : multisource - flag to indicate multi source for given node
     */
    private boolean getDraftTestCase_recursive(UmlNode node){
        
        boolean mutlisource = false;
        int skipped = 0;

        for (AdjacentNode source :node.getSources()){
            if (processedSource > 0 ){
                processedSource--;
                skipped++;
                continue;
            }
            if (mutlisource){
                workToDo = true;
                processedSource++;
                processedSource += skipped;
                return mutlisource;
            }
            UmlNode next  = UmlNodes.get(source.getIndex());

            connectCondition(next,source);
            mutlisource = getDraftTestCase_recursive(next);
        }
        return true;
    }
    /**
     * Method Name : printTestCases
     * Parameter   : None
     *     
     * Description : print out testcases information
     * Output      : print data
     */
    public void printTestCases() {
        for (TestCase testcase : testcases){
            System.out.println("Tesecase "+ testcases.indexOf(testcase));
            System.out.println("Tesecase valid = "+ testcase.isValid());
            for (ConditionModel model : testcase.getConditionModels()){
               System.out.println("CCTM "+ testcase.getConditionModels().indexOf(model)+" name = " + model.getName());
               System.out.println("CCTM "+ testcase.getConditionModels().indexOf(model)+" id = " + model.getId());
               for (Condition condition : model.getConditions()){
                   System.out.println("condition "+ model.getConditions().indexOf(condition)+" name = " + condition.getName());
                   //System.out.println("condition "+ model.Conditions.indexOf(condition)+" valid = " + condition.valid);
               }
            }
            System.out.println("---------------------------------------");
        }

    }
    public int countValidTestCases() {
        int count = 0;
        for (TestCase testcase : testcases){
            if (testcase.isValid()){
                count++;
            }
        }
        return count;
    }
    public int countInvalidTestCases() {
        int count = 0;
        for (TestCase testcase : testcases){
            if (!testcase.isValid()){
                count++;
            }
        }
        return count;
    }
    /**
     * Method Name : connectCondition
     * Parameter   : node - node to get the source name and id
     *             : source - source information for populate the condition
     *     
     * Description : for decision node, need to populate CCTM with connected condition
     *             : 
     * Output      : temp - testCase to be added in global array, testcases
     */
    private void connectCondition (UmlNode node,AdjacentNode source){
        if (node.isDecisionNode()){
            ConditionModel model = new ConditionModel();
            model.setName(node.getSources().get(0).getName());
            model.setId(node.getSources().get(0).getId());
            Condition condition = new Condition();
            condition.setName(source.getCondition());
            model.getConditions().add(condition);
            temp.getConditionModels().add(model);
        }
    }
}
