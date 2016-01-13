/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.view;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import RelTestFTA.config.ConstantsConfig;
import RelTestFTA.RelTestFTA;
import RelTestFTA.model.XmiNode;
import RelTestFTA.model.adjacentNode;
import RelTestFTA.model.umlNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.ArrayList;
/**
 * Class Name  : Input 
 * Parameter   : filename - file name of xmi file 
 *             : goal     - name for valid goal
 * Description : This class is designed to parse xmi file and determine valid path
 *               
 * Output      : umlNodes - List of umlNode 
 */
public class Input {
    ArrayList<umlNode> umlNodes = new ArrayList<umlNode>  ();
    ArrayList<String> goalList = new ArrayList<String>();
    String filePath;
    boolean sorted = false;
    boolean success = false;
    boolean populated = false;

    protected final ArrayList<XmiNode> xmiNodes = new ArrayList<XmiNode> ();
    private final ArrayList<String>  temp_condition = new ArrayList<String> ();

    private boolean validateSourceNode = false;
    private boolean validateTargetNode = false;



    public Input() throws IOException{
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File chosenFile = chooser.getSelectedFile();
            setFilePath(chosenFile.getPath());
            openXml(chosenFile);
            populateSourceAndTargetNode();
            sortAndIndexUmlNode();
            setSuccess(true);

        }
    }
    /**
     * Method Name : printUmlNodesArray 
     * Parameter   : None
     * Description : print out umlNodes information for debugging purpose       
     * Output      : print data 
     */
    public void printUmlNodesArray(){
        for (umlNode node_info : umlNodes) {
            System.out.println("Node "+ umlNodes.indexOf(node_info)+" name = " + node_info.getName());
            System.out.println("Node "+ umlNodes.indexOf(node_info)+" valid = " + node_info.isValidPath());
            int i =1;
            for (adjacentNode source : node_info.getSources()){
                System.out.println("Source " + i + " index = " + source.getIndex());
                System.out.println("Source " + i + " name = " + source.getName());
                System.out.println("Source " + i + " condtion = " + source.getCondition());
                System.out.println("Source " + i + " valid = " + source.isValid());
                i++;
            }
            i =1;
            for (adjacentNode target : node_info.getTargets()){
                System.out.println("Target " + i + " index = " + target.getIndex());
                System.out.println("Target " + i + " name = " + target.getName());
                System.out.println("Target " + i + " condtion = " + target.getCondition());
                System.out.println("Target " + i + " valid = " + target.isValid());
                i++;
            }
            System.out.println("---------------------------------------");
        }
    }
    /**
     * Method Name : getUmlNodes 
     * Parameter   : None
     * Description : return umlNodes       
     * Output      : populated umlNodes 
     */
    public ArrayList<umlNode> getUmlNodes(){
        return umlNodes;
    }
    public void setUmlNodes(ArrayList<umlNode> umlNodes) {
        this.umlNodes = umlNodes;
    }
    public boolean isPopulated() {
        return populated;
    }

    public void setPopulated(boolean populated) {
        this.populated = populated;
    }



    public boolean isSorted() {
        return sorted;
    }

    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<String> getGoalList() {
        setGoalList(new ArrayList<String>());
        umlNode finalNode = getFinalNode();
        for (adjacentNode source : finalNode.getSources()){
            goalList.add(source.getName());
            if (ConstantsConfig.PRINT_DEBUG_INFO) System.out.println("goal : "+source.getName());
        }
        return goalList;
    }

    public void setGoalList(ArrayList<String> goalList) {
        this.goalList = goalList;
    }

    /**
     * Method Name : openXml 
     * Parameter   : filename
     * Description : parse xmi file to variable xmiNode      
     * Output      : xmiNodes 
     */
    protected void openXml(File file) throws IOException {


        DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
        
        //Get the DOM Builder
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(RelTestFTA.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Load and Parse the XML document
        //document contains the complete XML as a Tree.
        Document document = null;
        try {
            assert builder != null;
            document = builder.parse(file);
        } catch (SAXException ex) {
            Logger.getLogger(RelTestFTA.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Iterating through the nodes and extracting the data.

        assert document != null;
        NodeList nodeList = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);
            if (node instanceof Element) {
                NodeList childNodes = node.getChildNodes();
                //printXmiElement(node);

                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node cNode = childNodes.item(j);

                    if (cNode instanceof Element) {
                        XmiNode temp_node = new XmiNode(cNode);
                        xmiNodes.add(temp_node);
                    }
                }
            }
        }        
    }
    /**
     * Method Name : printXmiElement 
     * Parameter   : Node
     * Description : for debug, print out xmi info      
     * Output      : print data 
     */
    private static void printXmiElement (Node node){
        NamedNodeMap attr = node.getAttributes();
        for (int j = 0; j < attr.getLength(); j++) {
            System.out.println("Node name = " + attr.item(j).getNodeName());
            System.out.println("Node Value= " + attr.item(j).getNodeValue());
        }
    }
    /**
     * Method Name : searchTargetNode 
     * Parameter   : target_id - node target id
     * Description : recursive walk thru xmiNodes to find the target id     
     * Output      : xmiNode of target id
     */
    private XmiNode searchTargetNode(String target_id){
        XmiNode targetNode = null;
        for (XmiNode node : xmiNodes){
            if (target_id.equals(node.getId())){
                if (node.isNode()){
                    // found it
                    targetNode = node;
                }
                else{
                    // if not node, it's flow. there is only one target
                    pushCondition(node.getName());
                    targetNode = searchTargetNode(node.getTargets().get(0));
                }
                break;
            }
        }
        return targetNode;
    }
    /**
     * Method Name : searchTargetNodes 
     * Parameter   : XmiNode - input Node to find target node info 
     * Description : Given xmi node, return list of target xmi node    
     * Output      : target xmi node
     */
    private ArrayList<XmiNode> searchTargetNodes(XmiNode inputNode){
        ArrayList<XmiNode>  targetsNode = new ArrayList<XmiNode> ();
        for (String target_id : inputNode.getTargets()){
            XmiNode result = searchTargetNode(target_id);
            if (result != null)
            {
                targetsNode.add(result);
            }
            
        }
        return targetsNode;
    }
    /**
     * Method Name : populateSourceAndTargetNode 
     * Parameter   : None
     * Description : populate source and target node for all node type    
     * Output      : umlNodes
     */
    protected void populateSourceAndTargetNode(){

        for (XmiNode node : xmiNodes){
            if (node.isNode()){
                
                ArrayList<XmiNode> targets_nodes = searchTargetNodes(node);
                
                umlNode temp_umlInfo = new umlNode();
                temp_umlInfo.setName(node.getName());
                temp_umlInfo.setId(node.getId());
                temp_umlInfo.setDecisionNode(node.isDecisionNode());
                temp_umlInfo.setFirstNode(node.isInitialNode());
                temp_umlInfo.setLastNode(node.isFinalNode());

                for (XmiNode target_node : targets_nodes){
                    adjacentNode target = new adjacentNode();
                    target.setName(target_node.getName());
                    target.setCondition(popCondition());
                    target.setId(target_node.getId());
                    temp_umlInfo.getTargets().add(target);
                }
                ArrayList<XmiNode> sources_nodes = searchSourceNodes(node);
                for (XmiNode source_node : sources_nodes){
                    adjacentNode source = new adjacentNode();
                    source.setName(source_node.getName());
                    source.setCondition(popCondition());
                    source.setId(source_node.getId());
                    temp_umlInfo.getSources().add(source);
                }
                umlNodes.add(temp_umlInfo);
            }
        }
        setPopulated(true);
    }
     /**
     * Method Name : searchSourceNode 
     * Parameter   : target_id - node target id
     * Description : recursive walk thru xmiNodes to find the target id     
     * Output      : xmiNode of target id
     */
    private XmiNode searchSourceNode(String target_id){
        XmiNode targetNode = null;
        for (XmiNode node : xmiNodes){
            if (target_id.equals(node.getId())){
                if (node.isNode()){
                    // found it
                    targetNode = node;
                }
                else{
                    // if not node, it's flow. there is only one target
                    pushCondition(node.getName());
                    targetNode = searchTargetNode(node.getSources().get(0));
                }
                break;
            }
        }
        return targetNode;
    }
    /**
     * Method Name : searchSourceNodes 
     * Parameter   : XmiNode - input Node to find source node info 
     * Description : Given xmi node, return list of source xmi node    
     * Output      : source xmi node
     */
    private ArrayList<XmiNode> searchSourceNodes(XmiNode inputNode){
        ArrayList<XmiNode>  targetsNode = new ArrayList<XmiNode> ();

        for (String target_id : inputNode.getSources()){
            XmiNode result = searchSourceNode(target_id);
            if (result != null)
            {
                targetsNode.add(result);
            }
            
        }
        return targetsNode;
    }
    private void pushCondition(String name){
        temp_condition.add(name);
    }
    private String popCondition(){
        if (temp_condition.isEmpty()){
            return null;
        }
        return temp_condition.remove(0);
    }
    /**
     * Method Name : sortAndIndexUmlNode 
     * Parameter   : None
     * Description : sort the initial node and link node with node index   
     * Output      : sorted and indexed umlNodes
     */
    protected void sortAndIndexUmlNode(){
        ArrayList<umlNode> temp = umlNodes;
        
        for (umlNode node_info : umlNodes){
            if (node_info.isFirstNode()){
                temp.remove(node_info);
                temp.add(0,node_info);
                break;
            }  
        }
        for (umlNode node_info : umlNodes){
            
            for (adjacentNode target: node_info.getTargets())
            {
                target.setIndex(temp.indexOf(searchUmlNodeByIdOrName(target.getId())));
                node_info.getTargets().set(node_info.getTargets().indexOf(target), target);
                temp.set(temp.indexOf(node_info),node_info);
            }
            for (adjacentNode source: node_info.getSources())
             {
                 source.setIndex(temp.indexOf(searchUmlNodeByIdOrName(source.getId())));
                 node_info.getSources().set(node_info.getSources().indexOf(source), source);
                 temp.set(temp.indexOf(node_info),node_info);
             } 
                      
        }
        umlNodes = temp;
        setSorted(true);
    }
    /**
     * Method Name : searchUmlNodeByIdOrName
     * Parameter   : id or name
     * Description : walk thru umlNodes and return node if matched id or name  
     * Output      : matched umlNode
     */
    public umlNode searchUmlNodeByIdOrName(String id){
        for (umlNode node_info : umlNodes){
            if (id.equals(node_info.getId()) || id.equals(node_info.getName())){
                return node_info;
            }
        }
        return null;
    }
    /**
     * Method Name : getFinalNode 
     * Parameter   : None
     * Description : return a final umlNode  
     * Output      : final umlNode 
     */
    public umlNode getFinalNode(){
        for (umlNode node_info : umlNodes){
            if (node_info.isLastNode()){
                return node_info;
            }
        }
        return null;
    }
    /**
     * Method Name : getFirstNode 
     * Parameter   : None
     * Description : return a first umlNode  
     * Output      : first umlNode 
     */
    public umlNode getFirstNode(){
        for (umlNode node_info : umlNodes){
            if (node_info.isFirstNode()){
                return node_info;
            }
        }
        return null;
    }
    /**
     * Method Name : getNodeByIndex 
     * Parameter   : index
     * Description : Given index, return a matched umlNode
     * Output      : matched umlNode 
     */
    public umlNode getNodeByIndex(int index){
        
        return umlNodes.get(index);
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }



}