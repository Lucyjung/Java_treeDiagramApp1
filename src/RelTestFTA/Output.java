/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA;

import java.awt.Image;
import java.io.IOException;
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
    private final String undefinedName = "noname";
    int  panelWidth;
    int  panelHight;
    public Output (){
        super("Tree Diagram");   
    }

    public void initGui(int w,int h){
        try
        {
            panelWidth = w;
            panelHight = h;
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
  
        addGraph(diagram.name,diagram.operation,0,1);
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
            addGraph(childName,tree.operation,lvl,i);
            addLine(parent_name,childName);
            recur_tree(tree.tree, childName, lvl + 1);
            i++;
        }
    }
    public static HashMap getM() {
        return m;
    }
    private void addGraph(String name, String operation, int lvl, int order){
        graph.getModel().beginUpdate();
        try
        {
            Object v;
            if (operation == "AND"){
                v = graph
                    .insertVertex(parent, null, name, getXPosition(order), getYPosition(lvl), 80, 100,
                    "shape=and_h");
            }
            else if (operation == "OR"){
                v = graph
                    .insertVertex(parent, null, name, getXPosition(order), getYPosition(lvl), 80, 100,
                    "shape=or_h");
            }
            else{
                v = graph
                    .insertVertex(parent, null, name, getXPosition(order), getYPosition(lvl), 80, 100);
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
    private void addLine(String parentName ,String childName){

        Object parentObj = getM().get(parentName);
        Object childObj = getM().get(childName);
        graph.insertEdge(parent, null, null, childObj,parentObj );

    }
    private int getXPosition(int order){
        return order*100;
    }
    private int getYPosition(int lvl){

        
        return lvl*120;
    }

    
}
