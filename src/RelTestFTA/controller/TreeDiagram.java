/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.controller;
import RelTestFTA.config.Configurations;
import RelTestFTA.model.ConditionModel;
import RelTestFTA.model.TestCase;
import RelTestFTA.model.TreeModel;

import java.util.ArrayList;
/**
 * Class Name  : TreeDiagram 
 * Parameter   : input  - list of testcases
 * Description : generate Success Tree diagram and Fault Tree Diagram from input
 *               which is testcases
 *               
 * Output      : SuccessTreeDiagram and FaultTreeDiagram
 */
public class TreeDiagram {
    private ArrayList <TestCase> testcases = new ArrayList<TestCase> ();
    private TreeModel successTreeDiagram = new TreeModel ();
    private TreeModel faultTreeDiagram = new TreeModel ();
    private String    space = "  "; 
    private int globalIndex = 0;
    public TreeDiagram (ArrayList <TestCase> input){
        testcases = input;
        generateTreeDiagram(true);
        generateTreeDiagram(false);
    }
    /**
     * Method Name : generateTreeDiagram 
     * Parameter   : buildSuccess -  indicate diagram to build, true = successTreeDiagram
     * Description : Populate diagram      
     * Output      : populated diagram   
     */
    public void generateTreeDiagram (boolean buildSuccess){
        TreeModel diagram = new TreeModel();
        int numOfValidPath = 0;
        for (TestCase testcase : testcases){
            if (testcase.isValid())numOfValidPath++;
        }

        diagram.setOperation((buildSuccess && (numOfValidPath == 1)) ? Configurations.AND_OPERATION: Configurations.OR_OPERATION);
        diagram.setName(diagram.getOperation() + globalIndex);
        globalIndex++;
        for (TestCase testcase : testcases){
            TreeModel testCaseTree = new TreeModel ();
            if (testcase.isValid() == buildSuccess){
                for (ConditionModel model: testcase.getConditionModels()){
                    TreeModel tree = new TreeModel ();
                    tree.setName(model.getName() + " " +  model.getConditions().get(0).getName());
                    tree.setOperation(Configurations.NONE_OPERATION);
                    testCaseTree.getTree().add(tree);
                }
                if (testcases.size() > 1) // There are more than one test case
                {
                    testCaseTree.setOperation((buildSuccess && (testcases.size() == 1)) ? Configurations.OR_OPERATION: Configurations.AND_OPERATION);
                    testCaseTree.setName(testCaseTree.getOperation() + globalIndex);
                    globalIndex++;
                    diagram.getTree().add(testCaseTree);
                }
                else {
                    for (TreeModel subTree: testCaseTree.getTree()){
                        diagram.getTree().add(subTree);
                    }

                }
            }
            
            
        }
        if (buildSuccess){
            successTreeDiagram = diagram;
        }
        else{
            faultTreeDiagram = diagram;
        }
        successTreeDiagram = optimizeTree(successTreeDiagram);
        faultTreeDiagram = optimizeTree(faultTreeDiagram);
    }
    /**
     * Method Name : printSuccessTreeDiagram 
     * Parameter   : None
     * Description : print success tree diagram information      
     * Output      : print data  
     */
    public void printSuccessTreeDiagram(){
        System.out.println("STD name = "+ successTreeDiagram.getName());
        System.out.println("STD op = "+ successTreeDiagram.getOperation());
        space = "  ";
        recur_tree(successTreeDiagram.getTree());
        
        System.out.println("---------------------------------------");
    }
    /**
     * Method Name : printFaultTreeDiagram 
     * Parameter   : None
     * Description : print fault tree diagram information      
     * Output      : print data  
     */
    public void printFaultTreeDiagram(){
        System.out.println("FTD name = "+ faultTreeDiagram.getName());
        System.out.println("FTD op = "+ faultTreeDiagram.getOperation());
        space = "  ";
        recur_tree(faultTreeDiagram.getTree());
        
        System.out.println("---------------------------------------");
    }
    /**
     * Method Name : getSuccessTreeDiagram 
     * Parameter   : None
     * Description : get success tree diagram      
     * Output      : success Tree Diagram 
     */
    public TreeModel getSuccessTreeDiagram(){
        return successTreeDiagram;
    }
    /**
     * Method Name : getFaultTreeDiagram 
     * Parameter   : None
     * Description : get fault tree diagram      
     * Output      : fault tree diagram  
     */
    public TreeModel getFaultTreeDiagram(){
        return faultTreeDiagram;
    }
    /**
     * Method Name : recur_tree 
     * Parameter   : Tree
     * Description : recursive method to print out the condition of STD and FTD    
     * Output      : print data  
     */
    private void recur_tree(ArrayList <TreeModel> trees){
        String backup = space;
        space += space;
        for (TreeModel tree : trees){
            System.out.println(space + "Tree name "+ tree.getName());
            System.out.println(space + "Tree op "+ tree.getOperation());
            recur_tree(tree.getTree());
        }
        space = backup;
    }
    /**
     * Method Name : optimizeTree 
     * Parameter   : Tree
     * Description : recursive method to remove redundant tree   
     * Output      : filtered tree  
     */
    private TreeModel optimizeTree(TreeModel inputTree){
        if (inputTree.getTree().size() == 1 && !inputTree.getTree().get(0).getTree().isEmpty()){
            ArrayList <TreeModel> toMoveTree = inputTree.getTree().get(0).getTree();
            inputTree.setTree(toMoveTree);
        }
        for (TreeModel subTree: inputTree.getTree()){
            if (subTree.getTree().size() == 1){
                inputTree.getTree().set(inputTree.getTree().indexOf(subTree), subTree.getTree().get(0));
            }
            else{
                inputTree.getTree().set(inputTree.getTree().indexOf(subTree), optimizeTree(subTree));
            }
            
        }
        return inputTree;
    }
   
}
