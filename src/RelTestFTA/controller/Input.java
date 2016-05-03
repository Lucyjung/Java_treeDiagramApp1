/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.controller;

import RelTestFTA.RelTestFTA;
import RelTestFTA.config.Configurations;
import RelTestFTA.model.AdjacentNode;
import RelTestFTA.model.UmlNode;
import RelTestFTA.model.XmiNode;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Class Name  : Input 
 * Parameter   : filename - file name of xmi file 
 *             : goal     - name for valid goal
 * Description : This class is designed to parse xmi file and determine valid path
 *               
 * Output      : UmlNodes - List of UmlNode
 */
public class Input {
    ArrayList<UmlNode> UmlNodes = new ArrayList<UmlNode>  ();
    ArrayList<String> goalList = new ArrayList<String>();
    String filePath;
    boolean sorted = false;
    boolean success = false;
    boolean populated = false;

    protected  ArrayList<XmiNode> xmiNodes = new ArrayList<XmiNode> ();
    private  ArrayList<String>  temp_condition = new ArrayList<String> ();

    private boolean validateSourceNode = false;
    private boolean validateTargetNode = false;


    public void reInit(){
        UmlNodes = new ArrayList<UmlNode>  ();
        goalList = new ArrayList<String>();
        filePath = "";
        xmiNodes = new ArrayList<XmiNode> ();
        temp_condition = new ArrayList<String> ();
        validateSourceNode = false;
        validateTargetNode = false;
        sorted = false;
        success = false;
        populated = false;
    }
    public void processFile (File chosenFile) throws IOException, SAXException {
        setFilePath(chosenFile.getPath());
        try {
            openXml(chosenFile);
            populateSourceAndTargetNode();
            sortAndIndexUmlNode();
            setSuccess(true);
        }catch (Exception e) {
            throw e;
        }

        return;
    }
    /**
     * Method Name : printUmlNodesArray 
     * Parameter   : None
     * Description : print out UmlNodes information for debugging purpose
     * Output      : print data 
     */
    public void printUmlNodesArray(){
        for (UmlNode node_info : UmlNodes) {
            System.out.println("Node "+ UmlNodes.indexOf(node_info)+" name = " + node_info.getName());
            System.out.println("Node "+ UmlNodes.indexOf(node_info)+" valid = " + node_info.isValidPath());
            System.out.println("Node "+ UmlNodes.indexOf(node_info)+" decision node = " + node_info.isDecisionNode());
            int i =1;
            for (AdjacentNode source : node_info.getSources()){
                System.out.println("Source " + i + " index = " + source.getIndex());
                System.out.println("Source " + i + " name = " + source.getName());
                System.out.println("Source " + i + " condtion = " + source.getCondition());
                System.out.println("Source " + i + " valid = " + source.isValid());
                i++;
            }
            i =1;
            for (AdjacentNode target : node_info.getTargets()){
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
     * Description : return UmlNodes
     * Output      : populated UmlNodes
     */
    public ArrayList<UmlNode> getUmlNodes(){
        return UmlNodes;
    }
    public void setUmlNodes(ArrayList<UmlNode> UmlNodes) {
        this.UmlNodes = UmlNodes;
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
        UmlNode finalNode = getFinalNode();
        for (AdjacentNode source : finalNode.getSources()){
            goalList.add(source.getName());
            if (Configurations.PRINT_DEBUG_INFO) System.out.println("goal : "+source.getName());
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
    protected void openXml(File file) throws IOException, SAXException {


        DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
        
        //Get the DOM Builder
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            //Logger.getLogger(RelTestFTA.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }

        //Load and Parse the XML document
        //document contains the complete XML as a Tree.
        Document document = null;
        try {
            assert builder != null;
            document = builder.parse(file);
        } catch (SAXException ex) {
            throw new IOException(ex);
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
     * Output      : UmlNodes
     */
    protected void populateSourceAndTargetNode(){

        for (XmiNode node : xmiNodes){
            if (node.isNode()){
                
                ArrayList<XmiNode> targets_nodes = searchTargetNodes(node);
                
                UmlNode temp_umlInfo = new UmlNode();
                temp_umlInfo.setName(node.getName());
                temp_umlInfo.setId(node.getId());
                temp_umlInfo.setDecisionNode(node.isDecisionNode());
                temp_umlInfo.setFirstNode(node.isInitialNode());
                temp_umlInfo.setLastNode(node.isFinalNode());

                for (XmiNode target_node : targets_nodes){
                    AdjacentNode target = new AdjacentNode();
                    target.setName(target_node.getName());
                    target.setCondition(popCondition());
                    target.setId(target_node.getId());
                    temp_umlInfo.getTargets().add(target);
                }
                ArrayList<XmiNode> sources_nodes = searchSourceNodes(node);
                for (XmiNode source_node : sources_nodes){
                    AdjacentNode source = new AdjacentNode();
                    source.setName(source_node.getName());
                    source.setCondition(popCondition());
                    source.setId(source_node.getId());
                    temp_umlInfo.getSources().add(source);
                }
                UmlNodes.add(temp_umlInfo);
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
     * Output      : sorted and indexed UmlNodes
     */
    protected void sortAndIndexUmlNode(){
        ArrayList<UmlNode> temp = UmlNodes;
        
        for (UmlNode node_info : UmlNodes){
            if (node_info.isFirstNode()){
                temp.remove(node_info);
                temp.add(0,node_info);
                break;
            }  
        }
        for (UmlNode node_info : UmlNodes){
            
            for (AdjacentNode target: node_info.getTargets())
            {
                target.setIndex(temp.indexOf(searchUmlNodeByIdOrName(target.getId())));
                node_info.getTargets().set(node_info.getTargets().indexOf(target), target);
                temp.set(temp.indexOf(node_info),node_info);
            }
            for (AdjacentNode source: node_info.getSources())
             {
                 source.setIndex(temp.indexOf(searchUmlNodeByIdOrName(source.getId())));
                 node_info.getSources().set(node_info.getSources().indexOf(source), source);
                 temp.set(temp.indexOf(node_info),node_info);
             } 
                      
        }
        UmlNodes = temp;
        setSorted(true);
    }
    /**
     * Method Name : searchUmlNodeByIdOrName
     * Parameter   : id or name
     * Description : walk thru UmlNodes and return node if matched id or name
     * Output      : matched UmlNode
     */
    public UmlNode searchUmlNodeByIdOrName(String id){
        for (UmlNode node_info : UmlNodes){
            if (id.equals(node_info.getId()) || id.equals(node_info.getName())){
                return node_info;
            }
        }
        return null;
    }
    /**
     * Method Name : getFinalNode 
     * Parameter   : None
     * Description : return a final UmlNode
     * Output      : final UmlNode
     */
    public UmlNode getFinalNode(){
        for (UmlNode node_info : UmlNodes){
            if (node_info.isLastNode()){
                return node_info;
            }
        }
        return null;
    }
    /**
     * Method Name : getFirstNode 
     * Parameter   : None
     * Description : return a first UmlNode
     * Output      : first UmlNode
     */
    public UmlNode getFirstNode(){
        for (UmlNode node_info : UmlNodes){
            if (node_info.isFirstNode()){
                return node_info;
            }
        }
        return null;
    }
    /**
     * Method Name : getNodeByIndex 
     * Parameter   : index
     * Description : Given index, return a matched UmlNode
     * Output      : matched UmlNode
     */
    public UmlNode getNodeByIndex(int index){
        
        return UmlNodes.get(index);
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }



}
