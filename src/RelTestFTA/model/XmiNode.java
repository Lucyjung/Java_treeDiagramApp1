/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.model;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Z510
 */
public class XmiNode{
    private Node   refNode;
    String id;
    String name;
    String type;
    String xmiType;
    ArrayList<String> targets;
    ArrayList<String> sources;
    boolean decisionNode;
    boolean finalNode;
    boolean initialNode;
    boolean isNode;
    public XmiNode(Node node){
        cleanUpException();

        refNode = node;
        id = getId();
        type = getType();
        
        xmiType = getXmiType();
        isNode = (type.equals("node"));
        decisionNode = (xmiType.equals("uml:DecisionNode"));
        initialNode = (xmiType.equals("uml:InitialNode"));
        finalNode = (xmiType.equals("uml:ActivityFinalNode"));
        targets   = getTargets();
        sources    = getSources();
        name = getName();
    }
    public String getXmiType(){

        return refNode.getAttributes().getNamedItem("xmi:type").getNodeValue();
    }
    public String getType(){
        String type = refNode.getNodeName();
        return type;
    }
    public String getName(){
        String name;
        
        try {
            name = refNode.getAttributes().getNamedItem("name").getNodeValue();
        }catch (Exception e){
            name = ""; // not found;
        }
        return name;
    }
    public String getId(){

        return refNode.getAttributes().getNamedItem("xmi:id").getNodeValue();
    }
    public ArrayList<String>  getTargets(){
        ArrayList<String> targets = new ArrayList<String>();
        String type = getType();
        String raw = "";
        try {
            if (type.equals("edge")){
                raw = refNode.getAttributes().getNamedItem("target").getNodeValue();
            }
            else if (type.equals("node")){
                raw = refNode.getAttributes().getNamedItem("outgoing").getNodeValue();
            }
            String[] spilted_array = raw.split(" ");
            Collections.addAll(targets, spilted_array);
            
        }catch (Exception e){
            name = ""; // probably final node
        }
        return targets;
    }
    public String getTarget(int index){
        return getTargets().get(index);
    }
    public int getTargetCount(){     
        return getTargets().size();
    }
    public ArrayList<String> getSources(){
        ArrayList<String> targets = new ArrayList<String>();
        String type = getType();
        String raw = "";
        try {
            if (type.equals("edge")){
                raw = refNode.getAttributes().getNamedItem("source").getNodeValue();
            }
            else if (type.equals("node")){
                raw = refNode.getAttributes().getNamedItem("incoming").getNodeValue();
            }
            String[] spilted_array = raw.split(" ");
            Collections.addAll(targets, spilted_array);
            
        }catch (Exception ignored){
          
        }
        return targets;
    }
    public String getSource(int index){
        return getSources().get(index);
    }
    public int getSourceCount(){
        return getSources().size();
    }
    private void cleanUpException(){
        try {}catch (Exception ignored){} // clean up exception
    }

    public Node getRefNode() {
        return refNode;
    }

    public void setRefNode(Node refNode) {
        this.refNode = refNode;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setXmiType(String xmiType) {
        this.xmiType = xmiType;
    }

    public void setTargets(ArrayList<String> targets) {
        this.targets = targets;
    }

    public void setSources(ArrayList<String> sources) {
        this.sources = sources;
    }

    public boolean isDecisionNode() {
        return decisionNode;
    }

    public void setDecisionNode(boolean decisionNode) {
        this.decisionNode = decisionNode;
    }

    public boolean isFinalNode() {
        return finalNode;
    }

    public void setFinalNode(boolean finalNode) {
        this.finalNode = finalNode;
    }

    public boolean isInitialNode() {
        return initialNode;
    }

    public void setInitialNode(boolean initialNode) {
        this.initialNode = initialNode;
    }

    public boolean isNode() {
        return isNode;
    }

    public void setNode(boolean isNode) {
        this.isNode = isNode;
    }
}
