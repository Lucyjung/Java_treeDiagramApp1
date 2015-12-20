/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javatest1;
import org.w3c.dom.Node;
import java.util.ArrayList;
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
        isNode = (type.equals("node"))?true:false;
        decisionNode = (xmiType.equals("uml:DecisionNode"))?true:false;
        initialNode = (xmiType.equals("uml:InitialNode"))?true:false;
        finalNode = (xmiType.equals("uml:ActivityFinalNode"))?true:false;
        targets   = getTargets();
        sources    = getSources();
        name = getName();
    }
    public String getXmiType(){
        String type = refNode.getAttributes().getNamedItem("xmi:type").getNodeValue();
        
        return type;
    }
    public String getType(){
        String type = refNode.getNodeName();
        return type;
    }
    public String getName(){
        String name = "";
        
        try {
            name = refNode.getAttributes().getNamedItem("name").getNodeValue();
        }catch (Exception e){
            name = ""; // not found;
        }
        return name;
    }
    public String getId(){
        String id = refNode.getAttributes().getNamedItem("xmi:id").getNodeValue();
        
        return id;
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
            for(String target : spilted_array ) {
                targets.add(target);
            }
            
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
            for(String target : spilted_array ) {
                targets.add(target);
            }
            
        }catch (Exception e){
          
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
        try {}catch (Exception e){} // clean up exception
    }
}
