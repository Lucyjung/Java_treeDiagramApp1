/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA;

import java.awt.Image;
import javax.swing.JFrame;
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
    private final String undefinedName = "noname";
    int  panelWidth;
    int panelHeight;
    int  processedCondition = 0;

    // CCTM configuration
    final int CCTMWidth = 40;
    final int CCTMHeight = 50;
    final int CCTMOriginX = 60;
    final int CCTMOriginY = 10;
    final int CCTMGapY = 20;
    final int CCTMGapX = 20;

    // TestCase configuration
    final int TestCaseWidth = 600;
    final int TestCaseHight = 50;
    final int TestCaseOriginX = 60;
    final int TestCaseOriginY = 200;
    final int TestCaseGapY = 20;
    final int TestCaseGapX = 20;

    public Output (){
        super("Tree Diagram");   
    }

    public void initGui(int w,int h){
        try
        {
            panelWidth = w;
            panelHeight = h;
            String filename = Output.class.getResource(
                            "/resource/shapes.xml").getPath();
            Document doc = mxXmlUtils.parseXml(mxUtils.readFile(filename));

            Element shapes = (Element) doc.getDocumentElement();
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
    public void drawDiagram(TreeModel diagram){
  
        addDiagram(diagram.name, diagram.operation, 0, 1);
        recur_tree(diagram.tree,diagram.name,1);
        mxGraphComponent graphComponent = new mxGraphComponent(graph)
        {
            // Sets global image base path
            public mxInteractiveCanvas createCanvas()
            {
                mxInteractiveCanvas canvas = super.createCanvas();
                canvas.setImageBasePath("/resource/");

                return canvas;
            }
        };
        getContentPane().add(graphComponent);
    }

    private void recur_tree(ArrayList <TreeModel> trees, String parent_name, int lvl){
        int i = 0;
        if (parent_name == null) parent_name = undefinedName + (lvl-1);
        for (TreeModel tree : trees){
            String childName = tree.name != null ? tree.name: undefinedName + lvl;
            addDiagram(childName, tree.operation, lvl, i);
            addLine(parent_name,childName,"none");
            recur_tree(tree.tree, childName, lvl + 1);
            i++;
        }
    }
    public static HashMap getM() {
        return m;
    }
    private void addDiagram(String name, String operation, int lvl, int order){
        graph.getModel().beginUpdate();
        try
        {
            Object v;
            if (operation == "AND"){
                v = graph
                    .insertVertex(parent, null, name, getXPosition(order, TreeModel.class), getYPosition(lvl,TreeModel.class), 80, 100,
                    "shape=and_h");
            }
            else if (operation == "OR"){
                v = graph
                    .insertVertex(parent, null, name, getXPosition(order,TreeModel.class), getYPosition(lvl,TreeModel.class), 80, 100,
                    "shape=or_h");
            }
            else{
                v = graph
                    .insertVertex(parent, null, name, getXPosition(order,TreeModel.class), getYPosition(lvl,TreeModel.class), 80, 100);
            }
            this.getM().put(name, v);
//            Object v1 = graph
//                    .insertVertex(parent, null, "World", 130, 20, 80, 100,
//                    "shape=or_h");
//            Object v2 = graph.insertVertex(parent, null, "hello!", 240,
//                            150, 80, 100, "shape=and_h");
//            Object v3 = graph.insertVertex(parent, null, "Hi!", 20,
//                            150, 80, 100, "shape=and_h");
//            graph.insertEdge(parent, null, null, v2, v1);
//            graph.insertEdge(parent, null, null, v3, v1);
        }
        finally
        {
            graph.getModel().endUpdate();
        }
    }
    private void addLine(String parentName ,String childName, String arrowStyle){

        String style = "";
        if (arrowStyle == null){
            arrowStyle = "";
        }
        if (arrowStyle.equals("diamond")){
            style = "startArrow=none;endArrow=diamond";
        }else if (arrowStyle.equals("none")){
            style = "startArrow=none;endArrow=none;";
        }
        Object parentObj = getM().get(parentName);
        Object childObj = getM().get(childName);
        graph.insertEdge(parent, null, null, childObj,parentObj, style);

    }

    private int getXPosition(int order, Class c)
    {

        if (c.getSimpleName().equals("ConditionModel")){
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
            if (c.getSimpleName().equals("ConditionModel")) {
                newXpos = CCTMOriginX;
            }
        }else{
            if (c.getSimpleName().equals("ConditionModel")) {
                lastXPos = Integer.parseInt((String) obj);
                newXpos = lastXPos + CCTMWidth + CCTMGapX;
            }
        }

        xPos.put(name,newXpos);
        if (lvl > 0) {
            String name_prevLvl = c.getSimpleName() + (lvl - 1);
            obj = xPos.get(name_prevLvl).toString();
            int prev_xPos = Integer.parseInt((String) obj);
            if (prev_xPos < newXpos ){
                xPos.put(name_prevLvl,newXpos);
            }
        }
        return newXpos;
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
            if (c.getSimpleName().equals("TestCase")) {
                newYPos = TestCaseOriginY;
            }
        }else{
            if (c.getSimpleName().equals("TestCase")) {
                lastYPos = Integer.parseInt((String) obj);
                newYPos = lastYPos + TestCaseHight + TestCaseGapY;
            }
        }

        yPos.put(name,newYPos);

        return newYPos;
    }

    private int getYPosition(int lvl,Class c)
    {
        return lvl*120;
    }

    public void drawCCTM(ArrayList<TestCase> TestCases, ArrayList<ConditionModel> ConditionModels){
        // draw ConditionModels
        int order = 0;
        processedCondition = 0;
        for (ConditionModel model : ConditionModels){
            drawConditionModel(model, order++);
        }

        // draw Test Cases
        for (TestCase testCase: TestCases){
            drawTestcases(testCase, TestCases.indexOf(testCase));
        }

        // Link the dots

        mxGraphComponent graphComponent = new mxGraphComponent(graph)
        {
            // Sets global image base path
            public mxInteractiveCanvas createCanvas()
            {
                mxInteractiveCanvas canvas = super.createCanvas();
                canvas.setImageBasePath("/resource/");

                return canvas;
            }
        };
        //graphComponent.setEnabled(false);
        getContentPane().add(graphComponent);

    }
    private void drawConditionModel (ConditionModel model, int order){
        graph.getModel().beginUpdate();
        try
        {
            int Offset0 = (((CCTMWidth*model.Conditions.size()) + CCTMGapX*(model.Conditions.size()-1))/2) - (CCTMWidth/2);

            Object v = graph.insertVertex(parent, null, model.name, getXPos(0,ConditionModel.class) + Offset0, CCTMOriginY, CCTMWidth, CCTMHeight);
            this.getM().put(model.name, v);
            for (Condition condition : model.Conditions){
                Object v1 = graph.insertVertex(parent, null, condition.name, getXPos(1, ConditionModel.class), CCTMOriginY + CCTMHeight + CCTMGapY, CCTMWidth, CCTMHeight);
                this.getM().put(condition.name, v1);
                addLine(model.name,condition.name,"none");
                processedCondition++;
            }
        }
        finally
        {
            graph.getModel().endUpdate();
        }
    }
    private void drawTestcases (TestCase testCase, int index){
        graph.getModel().beginUpdate();
        try
        {
            String name = "testcase " + index;
            Object v = graph.insertVertex(parent, null,null ,
                    TestCaseOriginX,
                    TestCaseOriginY + (index * (TestCaseGapY + TestCaseHight)),
                    TestCaseWidth,
                    TestCaseHight,
                    "shape=hline");
            this.getM().put(name, v);

        }
        finally
        {
            graph.getModel().endUpdate();
        }
    }
    
}
