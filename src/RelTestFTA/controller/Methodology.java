package RelTestFTA.controller;

import RelTestFTA.config.ConstantsConfig;
import RelTestFTA.model.ConditionModel;
import RelTestFTA.model.TestCase;
import RelTestFTA.model.TreeModel;
import RelTestFTA.model.umlNode;

import java.util.ArrayList;

/**
 * Created by Z510 on 14/1/2559.
 */
public class Methodology {
    private ArrayList<ConditionModel> ConditionModels;
    private ArrayList<TestCase> testCases;
    private TreeModel STD;
    private TreeModel FTD;

    private ArrayList<umlNode> umlNodes;
    private umlNode finalNode;
    public Methodology(ArrayList<umlNode> nodes, umlNode finalNode){
        this.umlNodes = nodes;
        this.finalNode = finalNode;
    }
    public void Execute(){
        // *******************************************************************
        // Step 2.1. Create CCTM for every decision point
        CCTM cctm = new CCTM(umlNodes);
        if (ConstantsConfig.PRINT_DEBUG_INFO) cctm.printCCTMs();
        ConditionModels = cctm.getCCTM();


        // *******************************************************************
        // Step 2.2. create test case table
        TestCaseTable testCase = new TestCaseTable(umlNodes,finalNode);
        testCases = testCase.getTestCaseTable();
        if (ConstantsConfig.PRINT_DEBUG_INFO) testCase.printTestCases();


        // *******************************************************************
        // Step 2.3. create tree diagram
        TreeDiagram tree = new TreeDiagram(testCases);

        if (ConstantsConfig.PRINT_DEBUG_INFO) tree.printSuccessTreeDiagram();
        if (ConstantsConfig.PRINT_DEBUG_INFO) tree.printFaultTreeDiagram();

        STD = tree.getSuccessTreeDiagram();
        FTD = tree.getFaultTreeDiagram();
    }

    public ArrayList<ConditionModel> getConditionModels() {
        return ConditionModels;
    }

    public void setConditionModels(ArrayList<ConditionModel> conditionModels) {
        ConditionModels = conditionModels;
    }

    public ArrayList<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(ArrayList<TestCase> testCases) {
        this.testCases = testCases;
    }

    public TreeModel getSTD() {
        return STD;
    }

    public void setSTD(TreeModel STD) {
        this.STD = STD;
    }

    public TreeModel getFTD() {
        return FTD;
    }

    public void setFTD(TreeModel FTD) {
        this.FTD = FTD;
    }
}
