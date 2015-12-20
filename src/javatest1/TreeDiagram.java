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
public class TreeDiagram {
    private ArrayList <TestCase> testcases = new ArrayList<TestCase> ();
    private TreeModel successTreeDiagram = new TreeModel ();
    private TreeModel faultTreeDiagram = new TreeModel ();
    private String    space = "  "; 
    public TreeDiagram (ArrayList <TestCase> input){
        testcases = input;
    }
    public void generateSuccessTreeDiagram (){
        successTreeDiagram.operation = "AND";
        for (TestCase testcase : testcases){
            if (testcase.valid){
                for (ConditionModel model: testcase.ConditionModels){
                    TreeModel tree = new TreeModel ();
                    tree.name = model.name + " " +  model.Conditions.get(0).name;
                    tree.operation = "NONE";
                    successTreeDiagram.tree.add(tree);
                }
                
            }
            
        }
    }
    public void generateTreeDiagram (boolean buildSuccess){
        TreeModel diagram = new TreeModel();
        diagram.operation = buildSuccess ? "AND":"OR";
        for (TestCase testcase : testcases){
            TreeModel testCaseTree = new TreeModel ();
            if (testcase.valid == buildSuccess){
                for (ConditionModel model: testcase.ConditionModels){
                    TreeModel tree = new TreeModel ();
                    tree.name = model.name + " " +  model.Conditions.get(0).name;
                    tree.operation = "NONE";
                    testCaseTree.tree.add(tree);
                }
                if (testcases.size() > 1) // There are more than one test case
                {
                    testCaseTree.operation = buildSuccess? "OR": "AND";
                    diagram.tree.add(testCaseTree);
                }
                else {
                    for (TreeModel subTree: testCaseTree.tree){
                        diagram.tree.add(subTree);
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
    public void printSuccessTreeDiagram(){
        System.out.println("STD name = "+ successTreeDiagram.name);
        System.out.println("STD op = "+ successTreeDiagram.operation);
        space = "  ";
        recur_tree(successTreeDiagram.tree);
        
        System.out.println("---------------------------------------");
    }
    public void printFaultTreeDiagram(){
        System.out.println("FTD name = "+ faultTreeDiagram.name);
        System.out.println("FTD op = "+ faultTreeDiagram.operation);
        space = "  ";
        recur_tree(faultTreeDiagram.tree);
        
        System.out.println("---------------------------------------");
    }
    private void recur_tree(ArrayList <TreeModel> trees){
        String backup = space;
        space += space;
        for (TreeModel tree : trees){
            System.out.println(space + "Tree name "+ tree.name);
            System.out.println(space + "Tree op "+ tree.operation);
            recur_tree(tree.tree);
        }
        space = backup;
    }
    private TreeModel optimizeTree(TreeModel inputTree){
        if (inputTree.tree.size() == 1 && inputTree.tree.get(0).tree.isEmpty() == false){
            ArrayList <TreeModel> toMoveTree = inputTree.tree.get(0).tree;
            inputTree.tree = toMoveTree;
        }
        for (TreeModel subTree: inputTree.tree){
            if (subTree.tree.size() == 1){
                inputTree.tree.set(inputTree.tree.indexOf(subTree),subTree.tree.get(0));
            }
            else{
                inputTree.tree.set(inputTree.tree.indexOf(subTree),optimizeTree(subTree));
            }
            
        }
        return inputTree;
    }
   
}
