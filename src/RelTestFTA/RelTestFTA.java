/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA;

import RelTestFTA.config.Configurations;
import RelTestFTA.config.ErrorReporting;
import RelTestFTA.controller.GoalProcessor;
import RelTestFTA.controller.Input;
import RelTestFTA.controller.Methodology;
import RelTestFTA.controller.TestInput;
import RelTestFTA.model.ConditionModel;
import RelTestFTA.model.TestCase;
import RelTestFTA.model.TreeModel;
import RelTestFTA.model.UmlNode;
import RelTestFTA.view.AppForm;
import RelTestFTA.view.ButtonEditor;
import RelTestFTA.view.ButtonRenderer;
import RelTestFTA.view.Output;
import com.alee.laf.WebLookAndFeel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
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
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        final int  panelWidth = Configurations.PANEL_WIDTH;
        final int  panelHeight = Configurations.PANEL_HEIGHT;
        boolean testMode = Configurations.TEST_MODE;
        final Input input = new Input();

        try {
            // Set System L&F
            UIManager.setLookAndFeel(new WebLookAndFeel());
        }
        catch (UnsupportedLookAndFeelException e) {
            // handle exception
        }


        final AppForm form = new AppForm();
        form.setVisible(true);
        String path = RelTestFTA.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.replace("RelTestFTA.jar", "");
        System.out.println(path);
        
        // *******************************************************************
        // Step 1. load input


        if (testMode) {
            Input testInput = new TestInput(Configurations.TEST_FILE);
            tempFunc(testInput,panelWidth, panelHeight);
        }
        else{
            // *******************************************************************
            // Step 1. Process Input File
            form.getBtnButton().addActionListener((ActionEvent e) -> {
                JFileChooser fileopen = new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter(
                        "UML file", "uml");
                fileopen.setFileFilter(filter);
                
                int ret = fileopen.showDialog(null, "Choose UML file");
                
                if (ret == JFileChooser.APPROVE_OPTION) {
                    
                    // Read Text file
                    File file = fileopen.getSelectedFile();
                    
                    try {
                        input.reInit();
                        input.processFile(file);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(form,
                                ErrorReporting.FILE_ERROR_TYPE,
                                ErrorReporting.FILE_ERROR_TITLE,
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (Configurations.PRINT_DEBUG_INFO) input.printUmlNodesArray();
                    if (Configurations.PRINT_DEBUG_INFO) System.out.println(input.getFilePath());
                    
                    form.getTxtFile().setText(fileopen.getSelectedFile().toString());
                    try {
                        form.getGoalList().removeAllItems();
                        input.getGoalList().stream().forEach((goal) -> {
                            form.getGoalList().addItem(goal);
                        });
                        form.getBtnGoal().setEnabled(true);
                    }catch (Exception ignored){
                        
                    }
                    
                    
                    form.reInitTable();
                }
            });
            form.getBtnGoal().addActionListener((ActionEvent e) -> {
                final String validGoal = form.getGoalList().getSelectedItem().toString();
                
                try {
                    
                    GoalProcessor processingGoal = new GoalProcessor(input, validGoal);
                    ArrayList<UmlNode> nodes= processingGoal.getUmlNodes();
                    if (Configurations.PRINT_DEBUG_INFO) input.printUmlNodesArray();
                    
                    // *******************************************************************
                    // Step 2. Execute Methodology
                    final Methodology methodology = new Methodology(nodes,input.getFinalNode());
                    methodology.Execute();
                    ArrayList<TestCase> testCases = methodology.getTestCases();
                    
                    
                    form.getTable().setValueAt(testCases.size(),0,Configurations.TESTCASE_COLUMN);
                    form.getTable().setValueAt(methodology.getValidTestCaseCount(),1,Configurations.TESTCASE_COLUMN);
                    form.getTable().setValueAt(methodology.getInvalidTestCaseCount(),2,Configurations.TESTCASE_COLUMN);
                    
                    form.getTable().setValueAt(Configurations.CCTM_BUTTON_NAME,0,Configurations.BUTTON_COLUMN);
                    form.getTable().setValueAt(Configurations.STD_BUTTON_NAME,1,Configurations.BUTTON_COLUMN);
                    form.getTable().setValueAt(Configurations.FTD_BUTTON_NAME,2,Configurations.BUTTON_COLUMN);
                    
                    // *******************************************************************
                    // Step 3. Draw diagram on click Event
                    form.getTable().getColumn(Configurations.BUTTON_COLUMN_NAME).setCellRenderer(new ButtonRenderer(true));
                    form.getTable().getColumn(Configurations.BUTTON_COLUMN_NAME).setCellEditor(
                            new ButtonEditor(new JCheckBox()){
                                @Override
                                public Object getCellEditorValue() {
                                    if (this.isPushed()) {

                                        Output cctmFrame = new Output(Configurations.HEADER_CCTM_FRAME,panelWidth, panelHeight);
                                        ArrayList<TestCase> testCases = methodology.getTestCases();
                                        ArrayList<ConditionModel> ConditionModels = methodology.getConditionModels();
                                        if (this.getLabel().equals(Configurations.CCTM_BUTTON_NAME)) {

                                            cctmFrame.initGui(panelWidth, panelHeight);
                                            cctmFrame.drawCCTM(testCases, ConditionModels);
                                            cctmFrame.setVisible(true);
                                        }
                                        else if (this.getLabel().equals(Configurations.STD_BUTTON_NAME)){
                                            TreeModel STD = methodology.getSTD();
                                            final Output stdFrame = new Output(Configurations.HEADER_STD_FRAME,panelWidth, panelHeight);
                                            stdFrame.initGui(panelWidth, panelHeight);
                                            stdFrame.drawTreeDiagram(STD, validGoal);
                                            stdFrame.setVisible(true);
                                        }else if (this.getLabel().equals(Configurations.FTD_BUTTON_NAME)){
                                            TreeModel FTD = methodology.getFTD();
                                            final Output ftdFrame = new Output(Configurations.HEADER_FTD_FRAME,panelWidth, panelHeight);
                                            String invalidGoal = null;
                                            for (String goal : input.getGoalList()){
                                                if (!validGoal.equals(goal)) invalidGoal = goal;
                                            }
                                            ftdFrame.initGui(panelWidth, panelHeight);
                                            ftdFrame.drawTreeDiagram(FTD, invalidGoal);
                                            ftdFrame.setVisible(true);
                                        }
                                    }
                                    setPushed(false);
                                    return this.getLabel();
                                }
                            });
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(form,
                            ErrorReporting.FILE_ERROR_DECODE,
                            ErrorReporting.FILE_ERROR_TITLE,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            });
        }
    }
    private static void tempFunc(Input input, int panelWidth, int panelHeight){
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

                invalidGoal = Configurations.TEST_INVALID_GOAL;
                validGoal = Configurations.TEST_GOAL;

                GoalProcessor processingGoal = new GoalProcessor(input, validGoal);

                ArrayList<UmlNode> nodes= processingGoal.getUmlNodes();
                if (Configurations.PRINT_DEBUG_INFO) input.printUmlNodesArray();

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
                Output cctmFrame = new Output(Configurations.HEADER_CCTM_FRAME,panelWidth, panelHeight);
                cctmFrame.drawCCTM(testCases, ConditionModels);
                cctmFrame.setVisible(true);

                Output ftdFrame = new Output(Configurations.HEADER_FTD_FRAME,panelWidth, panelHeight);
                ftdFrame.drawTreeDiagram(FTD, invalidGoal);
                ftdFrame.setVisible(true);

                Output stdFrame = new Output(Configurations.HEADER_STD_FRAME,panelWidth, panelHeight);
                stdFrame.drawTreeDiagram(STD, validGoal);
                stdFrame.setVisible(true);
            }catch (Exception e){
                JOptionPane.showMessageDialog(null,
                        e.getMessage(),
                        ErrorReporting.FILE_ERROR_TYPE,
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
