/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.view;

import RelTestFTA.config.Configurations;
import RelTestFTA.model.Condition;
import RelTestFTA.model.ConditionModel;
import RelTestFTA.model.TestCase;
import RelTestFTA.model.TreeModel;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.model.mxCell;
import com.mxgraph.shape.mxStencil;
import com.mxgraph.shape.mxStencilRegistry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 * @author Lucy
 */
public class Output extends JFrame {
    mxGraph graph = new mxGraph();
    Object parent = graph.getDefaultParent();
    private static HashMap m = new HashMap();
    private static HashMap xPos = new HashMap();
    private static HashMap yPos = new HashMap();
    private static HashMap xPosLine = new HashMap();
    private static HashMap yPosTestCase = new HashMap();

    private final String topEventGateName = Configurations.TOP_EVENT_GATE_NAME;
    int  panelWidth;
    int  panelHeight;
    private int  processedCondition = 0;
    private int  depth = 0;

    // CCTM configuration
    final int CCTMWidth = Configurations.CCTM_WIDTH;
    final int CCTMHeight = Configurations.CCTM_HEIGHT;
    final int CCTMOriginX = Configurations.CCTM_ORIGIN_X;
    final int CCTMOriginY = Configurations.CCTM_ORIGIN_Y;
    final int CCTMGapY = Configurations.CCTM_GAP_Y;
    final int CCTMGapX = Configurations.CCTM_GAP_X;

    // TestCase configuration
    int TestCaseWidth = Configurations.TESTCASE_WIDTH;
    final int TestCaseHeight = Configurations.TESTCASE_HEIGHT;
    final int TestCaseOriginX = Configurations.TESTCASE_ORIGIN_X;
    final int TestCaseOriginY = Configurations.TESTCASE_ORIGIN_Y;
    final int TestCaseGapY = Configurations.TESTCASE_GAP_Y;
    final int TestCaseGapX = Configurations.TESTCASE_GAP_X;

    // Condition Line configuration
    final int ConditionLineWidth = Configurations.CONDITION_LINE_WIDTH; // min 10
    int ConditionLineHeight = Configurations.CONDITION_LINE_HEIGHT;

    // Diagram configuration
    int DiagramWidth = Configurations.DIAGRAM_WIDTH;
    int DiagramHeight = Configurations.DIAGRAM_HEIGHT;
    int DiagramOriginX = Configurations.DIAGRAM_ORIGIN_X;
    int DiagramOriginY = Configurations.DIAGRAM_ORIGIN_Y;
    int DiagramGapY = Configurations.DIAGRAM_GAP_Y;
    int DiagramGapX = Configurations.DIAGRAM_GAP_X;
    int MaxWidth = Configurations.MAX_WIDTH;

    boolean ready = false;
    public Output (String name,int w,int h ){
        super(name);
        initGui(w,h);
        this.setSize(w,h);
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(Configurations.IMAGE_ICON)));
    }

    public void initGui(int w,int h){
        try
        {
            panelWidth = w;
            panelHeight = h;

            graph = new mxGraph();
            parent = graph.getDefaultParent();
            m = new HashMap();
            xPos = new HashMap();
            yPos = new HashMap();
            xPosLine = new HashMap();
            yPosTestCase = new HashMap();
            processedCondition = 0;
            depth = 0;
            // create rounded for dot connection
            mxStylesheet stylesheet = graph.getStylesheet();
            Hashtable<String, Object> style = new Hashtable<String, Object>();
            style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
            style.put(mxConstants.STYLE_OPACITY, 100);
            style.put(mxConstants.STYLE_FILLCOLOR, Configurations.DOT_COLOR);
            style.put(mxConstants.STYLE_FONTCOLOR, Configurations.DOT_FONT_COLOR);
            style.put(mxConstants.STYLE_EDITABLE, false);
            stylesheet.putCellStyle(Configurations.DOT_NAME, style);
            
         

            String filename = Output.class.getResource(
                            Configurations.SHAPE_FILE_PATH).getPath();
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
        treeModel.setOperation(Configurations.NONE_OPERATION);
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
            addLine(treeModel.getName(),linkName, Configurations.EDGE_NO_ARROW);
        }


        mxGraphComponent graphComponent = new mxGraphComponent(graph)
        {
            // Sets global image base path
            public mxInteractiveCanvas createCanvas()
            {
                mxInteractiveCanvas canvas = super.createCanvas();
                canvas.setImageBasePath(Configurations.IMAGE_PATH);

                return canvas;
            }
        };
        getContentPane().add(graphComponent);
        setReady(true);
    }

    private ArrayList<String> recur_tree(TreeModel trees, int lvl){
        ArrayList<String> childNames = new ArrayList<String>();
        for (TreeModel tree : trees.getTree()){

            ArrayList<String> toLinkNames = recur_tree(tree, lvl+1);
            childNames.add(tree.getName());

            if (toLinkNames.size() == 0){
                // bottom
                drawDiagram(tree.getName(), Configurations.NONE_OPERATION, lvl, 0,null);
                if (lvl != depth){
                    getXPos(depth, TreeModel.class);
                }
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
                    addLine(tree.getName(),linkName, Configurations.EDGE_NO_ARROW);
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

    /** draw the diagram (STD and FTD)
     * @param name name of vertex
     * @param operation AND_OPERATION, OR_OPERATION, NONE_OPERATION
     * @param lvl row to be drawn the vertex
     * @param childCount child of this vertex
     * @param forceXPos x position to be drawn
     * @return position in x of this vertex
     */
    private int drawDiagram(String name, String operation, int lvl, int childCount, Integer forceXPos){
        Integer xPosition = 0;
        graph.getModel().beginUpdate();
        try
        {
            String shape;
            String label = null;
            if (operation.equals(Configurations.AND_OPERATION)){
                shape = Configurations.SHAPE_AND;
            }
            else if (operation.equals(Configurations.OR_OPERATION)){
                shape = Configurations.SHAPE_OR;
            }
            else{
                shape = Configurations.SHAPE_BLOCK;
                label = wrapString(name,"\n",Configurations.DIAGRAM_WORDWRAP_LIMIT);
            }
            shape = Configurations.DIAGRAM_FONT_SIZE_STYLE + ";" + shape;
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

    /** add line between to 2 vertex
     * @param parentName origin vertex name
     * @param childName destination vertex name
     * @param arrowStyle style , EDGE_NO_ARROW or EDGE_DIAMOND_ARROW
     */
    private void addLine(String parentName ,String childName, String arrowStyle){

        String style = "";
        if (arrowStyle == null){
            arrowStyle = "";
        }
        if (arrowStyle.equals(Configurations.EDGE_DIAMOND_ARROW)){
            style = Configurations.ARROW_DIAMOND;
        }else if (arrowStyle.equals(Configurations.EDGE_NO_ARROW)){
            style = Configurations.ARROW_NONE;
        }
        Object parentObj = getM().get(parentName);
        Object childObj = getM().get(childName);
        graph.insertEdge(parent, null, null, childObj,parentObj, style);

    }

    /** Secret routine for drawing
     * @param lvl
     * @param c
     * @return
     */
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

    /** Secret routine for drawing
     * @param lvl secret
     * @param c secret
     * @return Y position
     */
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

    /**
     * @param lvl number of row
     * @return Y position
     */
    private int getYPosition(int lvl)
    {
        return lvl*(DiagramHeight+DiagramGapY) + DiagramOriginY;
    }

    /**
     * @param TestCases List of Test Case to be drawn
     * @param ConditionModels List of Condition Model to be drawn
     */
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
                canvas.setImageBasePath(Configurations.IMAGE_PATH);

                return canvas;
            }
        };
        graphComponent.setEnabled(false);
        getContentPane().add(graphComponent);
        setReady(true);

    }

    /**
     * @param model model to be drawn
     */
    private void drawConditionModel (ConditionModel model){
        graph.getModel().beginUpdate();
        try
        {
            int Offset0 = (((CCTMWidth*model.getConditions().size()) + CCTMGapX*(model.getConditions().size()-1))/2) - (CCTMWidth/2);

            String shape = Configurations.CCTM_FONT_SIZE_STYLE + ";" + Configurations.SHAPE_BLOCK ;
            String label = wrapString(model.getName(),"\n",Configurations.CCTM_WORDWRAP_LIMIT);
            Object v = graph.insertVertex(parent, null,label ,
                    getXPos(0, ConditionModel.class) + Offset0,
                    CCTMOriginY,
                    CCTMWidth,
                    CCTMHeight,
                    shape);
            getM().put(model.getName(), v);
            for (Condition condition : model.getConditions()){
                int xPosition =  getXPos(1, ConditionModel.class);
                int yPosition = CCTMOriginY + CCTMHeight + CCTMGapY;
                Object v1 = graph.insertVertex(parent, null, condition.getName(),
                        xPosition,
                        yPosition,
                        CCTMWidth,
                        CCTMHeight,
                        shape);
                getM().put(condition.getName(), v1);
                addLine(model.getName(),condition.getName(), Configurations.EDGE_NO_ARROW);
                Object v2 = graph.insertVertex(parent, null, null,
                        xPosition + CCTMWidth/2,
                        yPosition + CCTMHeight,
                        ConditionLineWidth,
                        ConditionLineHeight,
                        Configurations.SHAPE_VERTICAL_LINE);
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

    /**
     * @param index index of test case
     * @param valid validity of test case
     */
    private void drawTestCase(int index, boolean valid){
        graph.getModel().beginUpdate();
        try
        {
            String label = (Configurations.TESTCASE_PREFIX_NAME + (index+1));
            String name = Configurations.TESTCASE_PREFIX_NAME + index;
            String shape;
            if (valid) {
                shape = Configurations.SHAPE_HORIZONTAL_GREEN_LINE;
            }else{
                shape = Configurations.SHAPE_HORIZONTAL_RED_LINE;
            }
            int yPosistion = TestCaseOriginY + (index * (TestCaseGapY + TestCaseHeight));
            Object v = graph.insertVertex(parent, null,label ,
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

    /**
     * @param testCase test case to be drawn
     * @param index index
     */
    private void drawTestCaseLink(TestCase testCase, int index){
        ArrayList <ConditionModel> ConditionModels = testCase.getConditionModels();
        String name = Configurations.TESTCASE_PREFIX_NAME + index;
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
                            Configurations.DOT_NAME);
                }
            }
        }
        finally
        {
            graph.getModel().endUpdate();
        }
    }
    private static String wrapString(String s, String deliminator, int length) {
        String result = "";
        int lastdelimPos = 0;
        for (String token : s.split(" ", -1)) {
            if (result.length() - lastdelimPos + token.length() > length) {
                result = result + deliminator + token;
                lastdelimPos = result.length() + 1;
            }
            else {
                result += (result.isEmpty() ? "" : " ") + token;
            }
        }
        return result;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }
}
