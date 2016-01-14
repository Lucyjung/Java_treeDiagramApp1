package RelTestFTA.controller;

import RelTestFTA.model.AdjacentNode;
import RelTestFTA.model.UmlNode;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Z510 on 14/1/2559.
 */
public class GoalProcessor{
    private Input input;

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public GoalProcessor(Input in, String goal) throws IOException{
        setInput(in);
        try {
            buildValidUmlNodesArrayByNameOrId(goal);
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }

    }
    public ArrayList<UmlNode> getUmlNodes(){
        return getInput().getUmlNodes();
    }
    /**
     * Method Name : buildValidUmlNodesArrayByNameOrId
     * Parameter   : goal - require to be valid goal string
     * Description : build valid flag for each node
     * Output      : flagged UmlNodes
     */
    private void buildValidUmlNodesArrayByNameOrId(String id) throws Exception{
        if (getInput().isSorted() && (getInput().isPopulated())){

            UmlNode node = null;
            UmlNode finalNode = getInput().getFinalNode();
            for (AdjacentNode source : finalNode.getSources()){
                if (source.getName().equals(id)){
                    node = getInput().getNodeByIndex(source.getIndex());
                }
            }
            if (node == null){
                throw new Exception("Goal is invalid");
            }
            recur_validateSourceAndSelfNode(node);
            node = getInput().getUmlNodes().get(0);
            recur_validateTargetNode(node);
        }
    }
    /**
     * Method Name : recur_validateSourceAndSelfNode
     * Parameter   : node
     * Description : recursive walk each node and populate valid flag
     * Output      : none
     */
    private void recur_validateSourceAndSelfNode(UmlNode node){
        node.setValidPath(true);
        getInput().getUmlNodes().set(getInput().getUmlNodes().indexOf(node),node);
        for(AdjacentNode source : node.getSources()){
            UmlNode next = getInput().getUmlNodes().get(source.getIndex());
            source.setValid(true);
            node.getSources().set(node.getSources().indexOf(source), source);
            getInput().getUmlNodes().set(getInput().getUmlNodes().indexOf(node), node);
            if (!node.isFirstNode()){
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
    private void recur_validateTargetNode(UmlNode node){
        for(AdjacentNode target : node.getTargets()){
            UmlNode next = getInput().getUmlNodes().get(target.getIndex());
            if (next.isValidPath()){
                target.setValid(true);
                node.getTargets().set(node.getTargets().indexOf(target), target);
                getInput().getUmlNodes().set(getInput().getUmlNodes().indexOf(node), node);
            }
            if (!node.isLastNode()){
                recur_validateTargetNode(next);
            }

        }
    }
}
