/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.view;

import java.awt.Image;
import javax.swing.*;

import RelTestFTA.config.ConstantsConfig;
import RelTestFTA.model.Condition;
import RelTestFTA.model.ConditionModel;
import RelTestFTA.model.TestCase;
import RelTestFTA.model.TreeModel;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.shape.mxStencil;
import com.mxgraph.shape.mxStencilRegistry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 * @author Z510
 */
public class Output extends JFrame {
    mxGraph graph = new mxGraph();
    Object parent = graph.getDefaultParent();
    protected static HashMap m = new HashMap();
    protected static HashMap xPos = new HashMap();
    protected static HashMap yPos = new HashMap();
    protected static HashMap xPosLine = new HashMap();
    protected static HashMap yPosTestCase = new HashMap();

    private final String topEventGateName = ConstantsConfig.TOP_EVENT_GATE_NAME;
    int  panelWidth;
    int  panelHeight;
    private int  processedCondition = 0;
    private int  depth = 0;

    // CCTM configuration
    final int CCTMWidth = ConstantsConfig.CCTM_WIDTH;
    final int CCTMHeight = ConstantsConfig.CCTM_HEIGHT;
    final int CCTMOriginX = ConstantsConfig.CCTM_ORIGIN_X;
    final int CCTMOriginY = ConstantsConfig.CCTM_ORIGIN_Y;
    final int CCTMGapY = ConstantsConfig.CCTM_GAP_Y;
    final int CCTMGapX = ConstantsConfig.CCTM_GAP_X;

    // TestCase configuration
    int TestCaseWidth = ConstantsConfig.TESTCASE_WIDTH;
    final int TestCaseHeight = ConstantsConfig.TESTCASE_HEIGHT;
    final int TestCaseOriginX = ConstantsConfig.TESTCASE_ORIGIN_X;
    final int TestCaseOriginY = ConstantsConfig.TESTCASE_ORIGIN_Y;
    final int TestCaseGapY = ConstantsConfig.TESTCASE_GAP_Y;
    final int TestCaseGapX = ConstantsConfig.TESTCASE_GAP_X;

    // Condition Line configuration
    final int ConditionLineWidth = ConstantsConfig.CONDITION_LINE_WIDTH; // min 10
    int ConditionLineHeight = ConstantsConfig.CONDITION_LINE_HEIGHT;

    // CCTM configuration
    final int DiagramWidth = ConstantsConfig.DIAGRAM_WIDTH;
    final int DiagramHeight = ConstantsConfig.DIAGRAM_HEIGHT;
    final int DiagramOriginX = ConstantsConfig.DIAGRAM_ORIGIN_X;
    final int DiagramOriginY = ConstantsConfig.DIAGRAM_ORIGIN_Y;
    final int DiagramGapY = ConstantsConfig.DIAGRAM_GAP_Y;
    final int DiagramGapX = ConstantsConfig.DIAGRAM_GAP_X;
    final int MaxWidth = ConstantsConfig.MAX_WIDTH;
    public Output (String name,int w,int h ){
        super(name);
        initGui(w,h);
        this.setSize(w,h);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    public void initGui(int w,int h){
        try
        {
            panelWidth = w;
            panelHeight = h;

            m = new HashMap();
            xPos = new HashMap();
            yPos = new HashMap();
            xPosLine = new HashMap();
            yPosTestCase = new HashMap();
            // create rounded for dot connection
            mxStylesheet stylesheet = graph.getStylesheet();
            Hashtable<String, Object> style = new Hashtable<String, Object>();
            style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
            style.put(mxConstants.STYLE_OPACITY, 100);
            style.put(mxConstants.STYLE_FILLCOLOR, ConstantsConfig.DOT_COLOR);
            style.put(mxConstants.STYLE_FONTCOLOR, ConstantsConfig.DOT_FONT_COLOR);
            style.put(mxConstants.STYLE_EDITABLE, false);
            stylesheet.putCellStyle(ConstantsConfig.DOT_NAME, style);

            String filename = Output.class.getResource(
                            ConstantsConfig.SHAPE_FILE_PATH).getPath();
            Document doc = mxXmlUtils.parseXml(mxUtils.readFile(filename));

            Element shapes = doc.getDocumentElement();
            NodeList list = shapes.getElementsByTagName("shape");

            for (int i = 0; i < list.getLength(); i++)
            {
                Element shape = (Element) list.item(i);
                mxStencilRegistry.addStencil(shape.getAttribute("name"),
                new mxStencil(shape)
                {
                    protected mxGraphicsCanvas2D createCanvas(
                                    final mxGraphics2DCanvas gc)
                    {
                        // Redirects image loading to graphics canvas
                        return new mxGraphicsCanvas2D(gc.getGraphics())
                        {
                            protected Image loadImage(String src)
                            {
                                // Adds image base path to relative image URLs
                                if (!src.startsWith("/")
                                        && !src.startsWith("http://")
                                        && !src.startsWith("https://")
                                        && !src.startsWith("file:"))
                                {
                                    src = gc.getImageBasePath() + src;
                                }

                                // Call is cached
                                return gc.loadImage(src);
                            }
                        };
                    }
                });
            } 
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void drawTreeDiagram(TreeModel diagram, String goal){

        int lvl = 0;

        getDepthInfo(diagram,1);


        // Top event
        //drawDiagram(diagram.name, null, lvl, 0);
        lvl++;
        depth++; // add one depth lvl


        TreeModel treeModel = new TreeModel();
        treeModel.setName(goal);
        treeModel.setOperation(ConstantsConfig.NONE_OPERATION);
        treeModel.getTree().add(diagram);
        // recursive next level
        ArrayList<String> toLinkNames = recur_tree(treeModel,lvl);
        // cal xpos
        Integer min = MaxWidth,max = 0;
        for(String linkName : toLinkNames){
            mxCell childCell = (mxCell) getM().get(linkName);
            int x = (int)childCell.getGeometry().getPoint().getX();
            int y = (int)childCell.getGeometry().getPoint().getY();
            min = Integer.min(min,x);
            max = Integer.max(max,x);
        }
        drawDiagram(treeModel.getName(), treeModel.getOperation(), 0, toLinkNames.size(), (min+max)/2); // parent

        for(String linkName : toLinkNames){
            addLine(treeModel.getName(),linkName,ConstantsConfig.EDGE_NO_ARROW);
        }


        mxGraphComponent graphComponent = new mxGraphComponent(graph)
        {
            // Sets global image base path
            public mxInteractiveCanvas createCanvas()
            {
                mxInteractiveCanvas canvas = super.createCanvas();
                canvas.setImageBasePath(ConstantsConfig.IMAGE_PATH);

                return canvas;
            }
        };
        getContentPane().add(graphComponent);
    }

    private ArrayList<String> recur_tree(TreeModel trees, int lvl){
        ArrayList<String> childNames = new ArrayList<String>();
        for (TreeModel tree : trees.getTree()){
            ArrayList<String> toLinkNames = recur_tree(tree, lvl+1);
            childNames.add(tree.getName());

            if (toLinkNames.size() == 0){
                // bottom
                drawDiagram(tree.getName(), ConstantsConfig.NONE_OPERATION, depth, 0,null);
            }else{
                if (lvl < (depth-1)) { // not bottom and parent of bottom
                    // cal xpos
                    Integer min = MaxWidth,max = 0;
                    for(String linkName : toLinkNames){
                        mxCell childCell = (mxCell) getM().get(linkName);
                        int x = (int)childCell.getGeometry().getPoint().getX();
                        int y = (int)childCell.getGeometry().getPoint().getY();
                        min = Integer.min(min,x);
                        max = Integer.max(max,x);
                    }
                    drawDiagram(tree.getName(), tree.getOperation(), lvl, toLinkNames.size(), (min+max)/2); // parent
                }else{
                    drawDiagram(tree.getName(), tree.getOperation(), lvl, toLinkNames.size(), null); // parent
                }
                for(String linkName : toLinkNames){
                    addLine(tree.getName(),linkName,ConstantsConfig.EDGE_NO_ARROW);
                }
            }
        }
        return childNames;
    }
    private void getDepthInfo(TreeModel trees, int local_depth){
        depth = depth < local_depth? local_depth:depth;
        for (TreeModel tree : trees.getTree()){
            if (tree.getTree().size() != 0) {
                getDepthInfo(tree, local_depth+1);

            }
        }
    }
    private static HashMap getM() {
        return m;
    }
    private static int getxPosLine(String key) {
        String value = xPosLine.get(key).toString();
        return Integer.parseInt(value);
    }
    private static int getyPosTestCase(String key) {
        String value = yPosTestCase.get(key).toString();
        return Integer.parseInt( value);
    }
    private int drawDiagram(String name, String operation, int lvl, int childCount, Integer forceXPos){
        Integer xPosition = 0;
        graph.getModel().beginUpdate();
        try
        {
            String shape = null;
            String label = null;
            if (operation.equals(ConstantsConfig.AND_OPERATION)){
                shape = ConstantsConfig.SHAPE_AND;
            }
            else if (operation.equals(ConstantsConfig.OR_OPERATION)){
                shape = ConstantsConfig.SHAPE_OR;
            }
            else{
                label = name;
            }
            shape += ConstantsConfig.DIAGRAM_FONT_SIZE_STYLE;
            int xPosStartChild = 0;
            int xPosEndChild = getXPos(lvl, TreeModel.class);
            String id = null;
            if (forceXPos == null)
            {
                if (childCount > 0){
                    xPosStartChild = xPosEndChild - (childCount*(DiagramWidth+DiagramGapX));
                    xPosition = (xPosStartChild+xPosEndChild- DiagramWidth)/2;
                    if (lvl > 0){
                        setXPos(lvl-1,TreeModel.class,xPosition);
                    }
                    id = name;
                }
                else{
                    xPosition =  xPosEndChild ;
                }
            }
            else{
                xPosition = forceXPos;
            }


            int yPosition = getYPos(lvl,TreeModel.class);

            Object v = graph
                    .insertVertex(parent, id, label,
                            xPosition,
                            getYPosition(lvl),
                            DiagramWidth,
                            DiagramHeight,
                            shape);
            getM().put(name, v);

        }
        finally
        {
            graph.getModel().endUpdate();
        }
        return xPosition;
    }
    private void addLine(String parentName ,String childName, String arrowStyle){

        String style = "";
        if (arrowStyle == null){
            arrowStyle = "";
        }
        if (arrowStyle.equals(ConstantsConfig.EDGE_DIAMOND_ARROW)){
            style = ConstantsConfig.ARROW_DIAMOND;
        }else if (arrowStyle.equals(ConstantsConfig.EDGE_NO_ARROW)){
            style = ConstantsConfig.ARROW_NONE;
        }
        Object parentObj = getM().get(parentName);
        Object childObj = getM().get(childName);
        graph.insertEdge(parent, null, null, childObj,parentObj, style);

    }

    private int getXPosition(int order, Class c)
    {

        if (c.getSimpleName().equals(ConditionModel.class.getSimpleName())){
            return ((order + processedCondition)*CCTMWidth)+ CCTMGapX;
        }
        return order*100;
    }
    private int getXPos(Integer lvl, Class c)
    {
        Object obj = null;
        String name = c.getSimpleName() + lvl;
        int newXpos = 0;
        int lastXPos = 0 ;
        try {
            obj = xPos.get(name).toString();
        }catch (Exception e){ }

        if (obj == null ){
            if (c.getSimpleName().equals(ConditionModel.class.getSimpleName())) {
                newXpos = CCTMOriginX;
            }
            else if (c.getSimpleName().equals(TreeModel.class.getSimpleName())) {
                newXpos = DiagramOriginX;
            }
        }else{
            if (c.getSimpleName().equals(ConditionModel.class.getSimpleName())) {
                lastXPos = Integer.parseInt((String) obj);
                newXpos = lastXPos + CCTMWidth + CCTMGapX;
            }
            else if (c.getSimpleName().equals(TreeModel.class.getSimpleName())) {
                lastXPos = Integer.parseInt((String) obj);
                newXpos = lastXPos + DiagramWidth + DiagramGapX;
            }
        }

        xPos.put(name,newXpos);
        if (lvl > 0) {
            String name_prevLvl = c.getSimpleName() + (lvl - 1);
            try {
                obj = xPos.get(name_prevLvl).toString();
            }catch (Exception e){
                obj = "0";
            }
            int prev_xPos = Integer.parseInt((String) obj);
            if (prev_xPos < newXpos ){
                xPos.put(name_prevLvl,newXpos);
            }
        }
        return newXpos;
    }
    private void setXPos(Integer lvl, Class c,int value)
    {
        String name = c.getSimpleName() + lvl;
        xPos.put(name,value);
    }
    private int getYPos(Integer lvl, Class c)
    {
        String name = c.getSimpleName();
        Object obj = null;
        int newYPos = TestCaseOriginY,lastYPos;
        try {
            obj = xPos.get(name).toString();
        }catch (Exception e){ }
        if (obj == null ){
            if (c.getSimpleName().equals(TestCase.class.getSimpleName())) {
                newYPos = TestCaseOriginY;
            }
            else if (c.getSimpleName().equals(TreeModel.class.getSimpleName())) {
                newYPos = DiagramOriginY;
            }
        }else{
            lastYPos = Integer.parseInt((String) obj);
            if (c.getSimpleName().equals(TestCase.class.getSimpleName())) {
                newYPos = lastYPos + TestCaseHeight + TestCaseGapY;
            }
            else if (c.getSimpleName().equals(TreeModel.class.getSimpleName())) {
                newYPos = lastYPos + DiagramHeight + DiagramGapY;
            }
        }

        yPos.put(name,newYPos);

        return newYPos;
    }

    private int getYPosition(int lvl)
    {
        return lvl*(DiagramHeight+DiagramGapY) + DiagramOriginY;
    }

    public void drawCCTM(ArrayList<TestCase> TestCases, ArrayList<ConditionModel> ConditionModels){
        // draw ConditionModels
        int order = 0;
        processedCondition = 0;

        // dynamic line
        int cctmCount = 0;
        for (ConditionModel model : ConditionModels) cctmCount += model.getConditions().size();
        TestCaseWidth = (cctmCount+1)*(CCTMWidth+CCTMGapX);
        ConditionLineHeight = (TestCases.size()+1)*(TestCaseHeight+TestCaseGapY);
        for (ConditionModel model : ConditionModels){
            drawConditionModel(model);
        }

        // draw Test Cases
        for (TestCase testCase: TestCases){
            drawTestCase(TestCases.indexOf(testCase), testCase.isValid());
        }

        // Link the dots
        for (TestCase testCase: TestCases){
            drawTestCaseLink(testCase, TestCases.indexOf(testCase));
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph)
        {
            // Sets global image base path
            public mxInteractiveCanvas createCanvas()
            {
                mxInteractiveCanvas canvas = super.createCanvas();
                canvas.setImageBasePath(ConstantsConfig.IMAGE_PATH);

                return canvas;
            }
        };
        graphComponent.setEnabled(false);
        getContentPane().add(graphComponent);

    }
    private void drawConditionModel (ConditionModel model){
        graph.getModel().beginUpdate();
        try
        {
            int Offset0 = (((CCTMWidth*model.getConditions().size()) + CCTMGapX*(model.getConditions().size()-1))/2) - (CCTMWidth/2);

            Object v = graph.insertVertex(parent, null, model.getName(),
                    getXPos(0,ConditionModel.class) + Offset0,
                    CCTMOriginY,
                    CCTMWidth,
                    CCTMHeight,
                    ConstantsConfig.CCTM_FONT_SIZE_STYLE);
            getM().put(model.getName(), v);
            for (Condition condition : model.getConditions()){
                int xPosition =  getXPos(1, ConditionModel.class);
                int yPosition = CCTMOriginY + CCTMHeight + CCTMGapY;
                Object v1 = graph.insertVertex(parent, null, condition.getName(),
                        xPosition,
                        yPosition,
                        CCTMWidth,
                        CCTMHeight);
                getM().put(condition.getName(), v1);
                addLine(model.getName(),condition.getName(),ConstantsConfig.EDGE_NO_ARROW);
                Object v2 = graph.insertVertex(parent, null, null,
                        xPosition + CCTMWidth/2,
                        yPosition + CCTMHeight,
                        ConditionLineWidth,
                        ConditionLineHeight,
                        ConstantsConfig.SHAPE_VERTICAL_LINE);
                String hashName = model.getName()+ "." + condition.getName();
                xPosLine.put(hashName, xPosition + CCTMWidth / 2);
                processedCondition++;
            }
        }
        finally
        {
            graph.getModel().endUpdate();
        }
    }
    private void drawTestCase(int index, boolean valid){
        graph.getModel().beginUpdate();
        try
        {
            String name = ConstantsConfig.TESTCASE_PREFIX_NAME + index;
            String shape;
            if (valid) {
                shape = ConstantsConfig.SHAPE_HORIZONTAL_GREEN_LINE;
            }else{
                shape = ConstantsConfig.SHAPE_HORIZONTAL_RED_LINE;
            }
            int yPosistion = TestCaseOriginY + (index * (TestCaseGapY + TestCaseHeight));
            Object v = graph.insertVertex(parent, null,name ,
                    TestCaseOriginX,
                    yPosistion,
                    TestCaseWidth,
                    TestCaseHeight,
                    shape);
            getM().put(name, v);
            yPosTestCase.put(name, yPosistion);
        }
        finally
        {
            graph.getModel().endUpdate();
        }
    }
    private void drawTestCaseLink(TestCase testCase, int index){
        ArrayList <ConditionModel> ConditionModels = testCase.getConditionModels();
        String name = ConstantsConfig.TESTCASE_PREFIX_NAME + index;
        graph.getModel().beginUpdate();
        try
        {
            for (ConditionModel conditionModel : ConditionModels){
                for (Condition condition : conditionModel.getConditions()){
                    String hashName = conditionModel.getName()+ "." + condition.getName();
                    int x = getxPosLine(hashName);
                    int y = getyPosTestCase(name);
                    Object v = graph.insertVertex(parent, null,null ,
                            x - 5,
                            y - 5,
                            10,
                            10,
                            ConstantsConfig.DOT_NAME);
                }
            }
        }
        finally
        {
            graph.getModel().endUpdate();
        }
    }
    
}
