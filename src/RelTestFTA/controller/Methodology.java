package RelTestFTA.controller;

import RelTestFTA.config.Configurations;
import RelTestFTA.model.ConditionModel;
import RelTestFTA.model.TestCase;
import RelTestFTA.model.TreeModel;
import RelTestFTA.model.UmlNode;

import java.util.ArrayList;

/**
 * Created by Lucy on 14/1/2559.
 */
public class Methodology {
    private ArrayList<ConditionModel> ConditionModels;
    private ArrayList<TestCase> testCases;
    private TreeModel STD;
    private TreeModel FTD;
    private int validTestCaseCount = 0;
    private int invalidTestCaseCount = 0;

    private ArrayList<UmlNode> UmlNodes;
    private UmlNode finalNode;
    public Methodology(ArrayList<UmlNode> nodes, UmlNode finalNode){
        this.UmlNodes = nodes;
        this.finalNode = finalNode;
    }
    public void Execute(){
        // *******************************************************************
        // Step 2.1. Create CCTM for every decision point
        CCTM cctm = new CCTM(UmlNodes);
        if (Configurations.PRINT_DEBUG_INFO) cctm.printCCTMs();
        ConditionModels = cctm.getCCTM();


        // *******************************************************************
        // Step 2.2. create test case table
        TestCaseTable testCase = new TestCaseTable(UmlNodes,finalNode);
        testCases = testCase.getTestCaseTable();
        if (Configurations.PRINT_DEBUG_INFO) testCase.printTestCases();
        validTestCaseCount = testCase.countValidTestCases();
        invalidTestCaseCount = testCase.countInvalidTestCases();

        // *******************************************************************
        // Step 2.3. create tree diagram
        TreeDiagram tree = new TreeDiagram(testCases);

        if (Configurations.PRINT_DEBUG_INFO) tree.printSuccessTreeDiagram();
        if (Configurations.PRINT_DEBUG_INFO) tree.printFaultTreeDiagram();

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

    public int getValidTestCaseCount() {
        return validTestCaseCount;
    }

    public void setValidTestCaseCount(int validTestCaseCount) {
        this.validTestCaseCount = validTestCaseCount;
    }

    public int getInvalidTestCaseCount() {
        return invalidTestCaseCount;
    }

    public void setInvalidTestCaseCount(int invalidTestCaseCount) {
        this.invalidTestCaseCount = invalidTestCaseCount;
    }
}
