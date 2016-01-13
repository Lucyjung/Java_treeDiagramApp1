/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA;
import RelTestFTA.config.ConstantsConfig;
import RelTestFTA.config.ErrorReporting;
import RelTestFTA.controller.GoalProcessor;
import RelTestFTA.controller.Methodology;
import RelTestFTA.controller.TestInput;
import RelTestFTA.model.ConditionModel;
import RelTestFTA.model.TestCase;
import RelTestFTA.model.TreeModel;
import RelTestFTA.model.umlNode;
import RelTestFTA.view.Input;
import RelTestFTA.view.Output;
import sun.reflect.generics.tree.Tree;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

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
        int  panelWidth = ConstantsConfig.PANEL_WIDTH;
        int  panelHeight = ConstantsConfig.PANEL_HEIGHT;
        boolean testMode = ConstantsConfig.TEST_MODE;
        // *******************************************************************
        // Step 1. load input

        Input input;
        if (testMode) {
            input = new TestInput(ConstantsConfig.TEST_FILE);
        }
        else{
            input = new Input();
            if (ConstantsConfig.PRINT_DEBUG_INFO) input.printUmlNodesArray();
            if (ConstantsConfig.PRINT_DEBUG_INFO) System.out.println(input.getFilePath());
        }

        if (!input.isSuccess()) {
            JOptionPane.showMessageDialog(null,
                    ErrorReporting.NORMAL_EXIT,
                    ErrorReporting.SUCCESS_TYPE,
                    JOptionPane.ERROR_MESSAGE);
        }
        else{

            try {
                String invalidGoal;
                String validGoal;

                if (testMode){
                    invalidGoal = ConstantsConfig.TEST_INVALID_GOAL;
                    validGoal = ConstantsConfig.TEST_GOAL;
                }
                else{
                    // TODO: UI get goals and get validGoal from UI
                    ArrayList<String> goals = input.getGoalList();
                    invalidGoal = goals.get(1);
                    validGoal = goals.get(0);
                }

                GoalProcessor processingGoal = new GoalProcessor(input, validGoal);

                ArrayList<umlNode> nodes= processingGoal.getUmlNodes();
                if (ConstantsConfig.PRINT_DEBUG_INFO) input.printUmlNodesArray();

                // *******************************************************************
                // Step 2. Execute Methodology
                Methodology methodology = new Methodology(nodes,input.getFinalNode());
                methodology.Execute();
                ArrayList<TestCase> testCases = methodology.getTestCases();
                ArrayList<ConditionModel> ConditionModels = methodology.getConditionModels();
                TreeModel STD = methodology.getSTD();
                TreeModel FTD = methodology.getFTD();

                // *******************************************************************
                // Step 3. Display
                Output cctmFrame = new Output(ConstantsConfig.HEADER_CCTM_FRAME,panelWidth, panelHeight);
                cctmFrame.drawCCTM(testCases, ConditionModels);
                cctmFrame.setVisible(true);

                Output ftdFrame = new Output(ConstantsConfig.HEADER_FTD_FRAME,panelWidth, panelHeight);
                ftdFrame.drawTreeDiagram(FTD, invalidGoal);
                ftdFrame.setVisible(true);

                Output stdFrame = new Output(ConstantsConfig.HEADER_STD_FRAME,panelWidth, panelHeight);
                stdFrame.drawTreeDiagram(STD, validGoal);
                stdFrame.setVisible(true);
            }catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        e.getMessage(),
                        ErrorReporting.FILE_ERROR_TYPE,
                        JOptionPane.ERROR_MESSAGE);
                throw new IOException(e.getMessage());
            }
        }
    }

}
