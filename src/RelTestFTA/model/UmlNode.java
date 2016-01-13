/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.model;

import java.util.ArrayList;
/**
 *
 * @author Z510
 */
public class UmlNode {
    String name;
    String id;
    boolean decisionNode;
    boolean firstNode;
    boolean lastNode;
    boolean validPath;
    ArrayList<AdjacentNode> targets ;
    ArrayList<AdjacentNode> sources ;

    public UmlNode() {
        this.name = "";
        this.id = "";
        this.decisionNode = false;
        this.firstNode = false;
        this.lastNode = false;
        this.validPath = false;
        this.targets = new ArrayList<AdjacentNode>();
        this.sources = new ArrayList<AdjacentNode>() ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDecisionNode() {
        return decisionNode;
    }

    public void setDecisionNode(boolean decisionNode) {
        this.decisionNode = decisionNode;
    }

    public boolean isFirstNode() {
        return firstNode;
    }

    public void setFirstNode(boolean firstNode) {
        this.firstNode = firstNode;
    }

    public boolean isLastNode() {
        return lastNode;
    }

    public void setLastNode(boolean lastNode) {
        this.lastNode = lastNode;
    }

    public boolean isValidPath() {
        return validPath;
    }

    public void setValidPath(boolean validPath) {
        this.validPath = validPath;
    }

    public ArrayList<AdjacentNode> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<AdjacentNode> targets) {
        this.targets = targets;
    }

    public ArrayList<AdjacentNode> getSources() {
        return sources;
    }

    public void setSources(ArrayList<AdjacentNode> sources) {
        this.sources = sources;
    }
}
