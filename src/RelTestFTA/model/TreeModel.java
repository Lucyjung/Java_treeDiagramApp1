/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.model;

import java.util.ArrayList;

/**
 *
 * @author Lucy
 */
public class TreeModel {
    String name;
    String operation;
    ArrayList <TreeModel> tree = new ArrayList <TreeModel>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public ArrayList<TreeModel> getTree() {
        return tree;
    }

    public void setTree(ArrayList<TreeModel> tree) {
        this.tree = tree;
    }
}
