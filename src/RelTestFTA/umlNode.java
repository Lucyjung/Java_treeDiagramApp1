/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA;
import java.util.ArrayList;
/**
 *
 * @author Z510
 */
public class umlNode {
    String name;
    String id;
    boolean decisionNode;
    boolean firstNode;
    boolean lastNode;
    boolean validPath;
    ArrayList<adjacentNode> targets = new ArrayList<adjacentNode>() ;
    ArrayList<adjacentNode> sources = new ArrayList<adjacentNode>() ;
}
