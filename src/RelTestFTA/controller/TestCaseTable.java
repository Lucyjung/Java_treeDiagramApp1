/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.controller;

import RelTestFTA.model.*;

import java.util.*;

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

        for (AdjacentNode goal : finalNode.getSources()){
            UmlNode startNode = nodes.get(goal.getIndex());
            temp.setValid(startNode.isValidPath());
            getDraftTestCase_recursive(startNode);

        }
        removeDup();
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
    private void getDraftTestCase_recursive(UmlNode node){

        for (AdjacentNode source :node.getSources()){


            UmlNode next  = UmlNodes.get(source.getIndex());
            ConditionModel model = connectCondition(next,source);
            getDraftTestCase_recursive(next);
            disconnectCondition(next,model);
        }

        if (node.isFirstNode())
        {

            TestCase toBeSaved = cloneTestCase(temp);
            Collections.reverse(toBeSaved.getConditionModels());
            testcases.add(toBeSaved);
        }
    }

    private TestCase cloneTestCase(TestCase src)
    {
        TestCase tc = new TestCase();

        tc.setConditionModels((ArrayList<ConditionModel>) src.getConditionModels().clone());
        tc.setValid(src.isValid());
        return tc;
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
    private ConditionModel connectCondition (UmlNode node,AdjacentNode source){
        if (node.isDecisionNode()){
            ConditionModel model = new ConditionModel();
            model.setName(node.getSources().get(0).getName());
            model.setId(node.getSources().get(0).getId());
            Condition condition = new Condition();
            condition.setName(source.getCondition());
            model.getConditions().add(condition);
            temp.getConditionModels().add(model);
            return model;
        }
        return null;
    }

    private void disconnectCondition (UmlNode node, ConditionModel model){
        if (node.isDecisionNode()){

            temp.getConditionModels().remove(model);
        }
    }
    private void removeDup(){
        ArrayList l2 = new ArrayList();

        Iterator iterator = testcases.iterator();

        while (iterator.hasNext())
        {
            TestCase o = (TestCase) iterator.next();
            if(!contain(l2,o)) l2.add(o);
        }
        testcases = l2;
    }
    private boolean contain(ArrayList<TestCase> tcs, TestCase tc){
        for (TestCase testcase : tcs){
            if (compareTestCase(tc, testcase)){
                return true;
            }
        }
        return false;
    }
    private boolean compareTestCase(TestCase tc1, TestCase tc2){
        if (tc1.getConditionModels().size() == tc2.getConditionModels().size()){
            for (int i = 0; i < tc1.getConditionModels().size(); i++)
            {
                if (tc1.getConditionModels().get(i).getId() != tc2.getConditionModels().get(i).getId())
                {
                    return false; // id mismatch
                }
                else{
                    // id matched need to check condition
                    ArrayList<Condition> con1 = tc1.getConditionModels().get(i).getConditions();
                    ArrayList<Condition> con2 = tc2.getConditionModels().get(i).getConditions();
                    if (con1.size() != con2.size())
                    {
                        return false; // condition size mismatch
                    }
                    else{
                        // size matched
                        for (int j = 0; j < con1.size(); j++)
                        {
                            if (con1.get(j).getName() != con2.get(j).getName())
                            {
                                return false; // id mismatch
                            }
                        }
                    }
                }
            }
        }
        else{
            return false;
        }

        return true;
    }
}
