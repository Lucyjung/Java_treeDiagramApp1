/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
    private final ArrayList<XmiNode> xmiNodes = new ArrayList<XmiNode> ();
    private final ArrayList<String>  temp_condition = new ArrayList<String> ();
    ArrayList<umlNode> umlNodes = new ArrayList<umlNode>  ();
    ArrayList<umlNode> validUmlNode = new ArrayList<umlNode>  ();
    private boolean populated = false;
    private boolean sorted = false;
    private boolean validateSourceNode = false;
    private boolean validatetargetNode = false;
    public Input(String filename, String goal) throws IOException{
        openXml(filename);
        populateSourceAndTargetNode();
        sortAndIndexUmlNode();
        try {
            buildValidUmlNodesArrayByNameOrId(goal);
        } catch (Exception ex) {
            Logger.getLogger(Input.class.getName()).log(Level.SEVERE, null, ex);
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
            System.out.println("Node "+ umlNodes.indexOf(node_info)+" name = " + node_info.name);
            System.out.println("Node "+ umlNodes.indexOf(node_info)+" valid = " + node_info.validPath);
            int i =1;
            for (adjacentNode source : node_info.sources){
                System.out.println("Source " + i + " index = " + source.index);
                System.out.println("Source " + i + " name = " + source.name);
                System.out.println("Source " + i + " condtion = " + source.condition);
                System.out.println("Source " + i + " valid = " + source.valid);
                i++;
            }
            i =1;
            for (adjacentNode target : node_info.targets){
                System.out.println("Target " + i + " index = " + target.index);
                System.out.println("Target " + i + " name = " + target.name);
                System.out.println("Target " + i + " condtion = " + target.condition);
                System.out.println("Target " + i + " valid = " + target.valid);
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
    /**
     * Method Name : openXml 
     * Parameter   : filename
     * Description : parse xmi file to variable xmiNode      
     * Output      : xmiNodes 
     */
    private void openXml(String filename) throws IOException {

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
            document = builder.parse(ClassLoader.getSystemResourceAsStream(filename));
        } catch (SAXException ex) {
            Logger.getLogger(RelTestFTA.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Iterating through the nodes and extracting the data.
        
        NodeList nodeList = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);
            if (node instanceof Element) {
                NodeList childNodes = node.getChildNodes();
                //printXmiElement(node);

                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node cNode = childNodes.item(j);
                    //Identifying the child tag of employee encountered.
                    if (cNode instanceof Element) {
                        XmiNode temp_node = new XmiNode(cNode);

                        boolean add = xmiNodes.add(temp_node);
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
            if (target_id.equals(node.id)){
                if (node.isNode){
                    // found it
                    targetNode = node;
                }
                else{
                    // if not node, it's flow. there is only one target
                    pushCondition(node.name);
                    targetNode = searchTargetNode(node.targets.get(0));
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
        for (String target_id : inputNode.targets){
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
    private void populateSourceAndTargetNode(){
        XmiNode firstNode = xmiNodes.get(0);
      
        for (XmiNode node : xmiNodes){
            if (node.isNode){
                
                ArrayList<XmiNode> targets_nodes = searchTargetNodes(node);
                
                umlNode temp_umlInfo = new umlNode();
                temp_umlInfo.name = node.name;
                temp_umlInfo.id   = node.id;
                temp_umlInfo.decisionNode = node.decisionNode;
                temp_umlInfo.firstNode = node.initialNode;
                temp_umlInfo.lastNode = node.finalNode;

                for (XmiNode target_node : targets_nodes){
                    adjacentNode target = new adjacentNode();
                    target.name = target_node.name;
                    target.condition = popCondition();
                    target.id = target_node.id;
                    temp_umlInfo.targets.add(target);
                }
                ArrayList<XmiNode> sources_nodes = searchSourceNodes(node);
                for (XmiNode source_node : sources_nodes){
                    adjacentNode source = new adjacentNode();
                    source.name = source_node.name;
                    source.condition = popCondition();
                    source.id = source_node.id;
                    temp_umlInfo.sources.add(source);
                }
                umlNodes.add(temp_umlInfo);
            }
        }
        populated = true;
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
            if (target_id.equals(node.id)){
                if (node.isNode){
                    // found it
                    targetNode = node;
                }
                else{
                    // if not node, it's flow. there is only one target
                    pushCondition(node.name);
                    targetNode = searchTargetNode(node.sources.get(0));
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

        for (String target_id : inputNode.sources){
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
    private void sortAndIndexUmlNode(){
        ArrayList<umlNode> temp = umlNodes;
        
        for (umlNode node_info : umlNodes){
            if (node_info.firstNode){
                temp.remove(node_info);
                temp.add(0,node_info);
                break;
            }  
        }
        for (umlNode node_info : umlNodes){
            
            for (adjacentNode target: node_info.targets)
            {
                target.index = temp.indexOf(searchUmlNodebyIdOrName(target.id));
                node_info.targets.set(node_info.targets.indexOf(target),target);
                temp.set(temp.indexOf(node_info),node_info);
            }
            for (adjacentNode source: node_info.sources)
             {
                 source.index = temp.indexOf(searchUmlNodebyIdOrName(source.id));
                 node_info.sources.set(node_info.sources.indexOf(source),source);
                 temp.set(temp.indexOf(node_info),node_info);
             } 
                      
        }
        umlNodes = temp;
        sorted = true;
    }
    /**
     * Method Name : searchUmlNodebyIdOrName 
     * Parameter   : id or name
     * Description : walk thru umlNodes and return node if matched id or name  
     * Output      : matched umlNode
     */
    public umlNode searchUmlNodebyIdOrName(String id){
        for (umlNode node_info : umlNodes){
            if (id.equals(node_info.id) || id.equals(node_info.name)){
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
            if (node_info.lastNode){
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
            if (node_info.firstNode){
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
    /**
     * Method Name : buildValidUmlNodesArrayByNameOrId 
     * Parameter   : goal - require to be valid goal string
     * Description : build valid flag for each node 
     * Output      : flagged umlNodes 
     */
    private void buildValidUmlNodesArrayByNameOrId(String id) throws Exception{
        if (sorted == true && populated == true){
            
            umlNode node = null;
            umlNode finalNode = getFinalNode();
            for (adjacentNode source : finalNode.sources){
                if (source.name.equals(id)){
                    node = getNodeByIndex(source.index);
                }
            }
            if (node == null){
                throw new Exception();
            }
            recur_validateSourceAndSelfNode(node);
            node = umlNodes.get(0);
            recur_validateTargetNode(node);
        }
    }
    /**
     * Method Name : recur_validateSourceAndSelfNode 
     * Parameter   : node
     * Description : recursive walk each node and populate valid flag
     * Output      : none 
     */
    private void recur_validateSourceAndSelfNode(umlNode node){
        node.validPath = true;
        umlNodes.set(umlNodes.indexOf(node),node);  
        for(adjacentNode source : node.sources){
            umlNode next = umlNodes.get(source.index);
            source.valid = true;
            node.sources.set(node.sources.indexOf(source),source);
            umlNodes.set(umlNodes.indexOf(node),node);
            if (!node.firstNode){
                recur_validateSourceAndSelfNode(next);
            }
        
        }
    }
    /**
     * Method Name : recur_validateTargetNode 
     * Parameter   : node
     * Description : recursive walk each node and populate valid flag
     * Output      : none 
     */
    private void recur_validateTargetNode(umlNode node){  
        for(adjacentNode target : node.targets){
            umlNode next = umlNodes.get(target.index);
            if (next.validPath){
                target.valid = true;
                node.targets.set(node.targets.indexOf(target),target);
                umlNodes.set(umlNodes.indexOf(node),node);
            }
            if (!node.lastNode){
                recur_validateTargetNode(next);
            }
        
        }
    }
    
}
