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
 * @author jiewdue
 */
public class JavaTest1 {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws IOException {
        //Step 1. Load input 
        
        String validGoal = "Accept Transection";
        Input input = new Input("xml/firstuml.xml",validGoal);
        
        ArrayList<umlNode> nodes= input.getUmlNodes();
        //input.printUmlNodesArray();
        // Step 2. Create CCTM for every decision point 
        CCTM cctm = new CCTM(nodes);
        //cctm.printCCTMs();
        
        // Step 3. create test case table
        TestCaseTable testCase = new TestCaseTable(nodes,input.getFinalNode());
        ArrayList <TestCase> testcases = testCase.getTestCaseTable();
        //testCase.printTestCases();
        // Step 4. create tree diagram 
        TreeDiagram tree = new TreeDiagram(testcases);
        tree.generateTreeDiagram(true);
        tree.generateTreeDiagram(false);
        tree.printSuccessTreeDiagram();
        tree.printFaultTreeDiagram();
        
        // Step 5. Display 
        Output output = new Output();
        output.display("TODO");

    }
}
