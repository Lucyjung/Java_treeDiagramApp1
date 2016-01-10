/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA;
import java.io.IOException; 
import java.util.ArrayList;
import javax.swing.JFrame;
/**
 * Class Name  : RelTestFTA 
 * Parameter   : None
 *
 * Description : This tool is part of Master's Thesis Project called,  
 *               Reliability Tests for Process Flow with Fault Tree Analysis
 *               this tool is designed for parsing xmi 
 *               and generate Tree Diagram using FTA technique 
 *               
 * Output      : CCTM/ Test Case Table / Success Tree Diagram / Fault Tree Diagram
 */
public class RelTestFTA {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        int  panelWidth = 800;
        int  panelHeight = 600;
        // *******************************************************************
        //Step 1. Load input
        String invalidGoal = "Reject transaction";
        String validGoal = "Accept transaction";
        Input input = new Input("xml/testxmi2.xml",validGoal);
        //String validGoal = "Accept Transection";
        //Input input = new Input("xml/firstuml.xml",validGoal);
        
        ArrayList<umlNode> nodes= input.getUmlNodes();
        //input.printUmlNodesArray();
        
        // *******************************************************************
        // Step 2. Create CCTM for every decision point 
        CCTM cctm = new CCTM(nodes);
        //cctm.printCCTMs();
        ArrayList<ConditionModel> ConditionModels = cctm.getCCTM();
        

        // *******************************************************************
        // Step 3. create test case table
        TestCaseTable testCase = new TestCaseTable(nodes,input.getFinalNode());
        ArrayList<TestCase> testcases = testCase.getTestCaseTable();
        testCase.printTestCases();
        

        // *******************************************************************
        // Step 4. create tree diagram 
        TreeDiagram tree = new TreeDiagram(testcases);

        //tree.printSuccessTreeDiagram();
        tree.printFaultTreeDiagram();
        
        TreeModel STD = tree.getSuccessTreeDiagram();
        TreeModel FTD = tree.getFaultTreeDiagram();
        // *******************************************************************
        // Step 5. Display 
        Output output = new Output();
        output.initGui(panelWidth,panelHeight);
        //output.drawTreeDiagram(FTD,invalidGoal);
        //output.drawTreeDiagram(STD,validGoal);
        output.drawCCTM(testcases,ConditionModels);
        output.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        output.setSize(panelWidth, panelHeight);
        output.setVisible(true);

    }
}
