package RelTestFTA.controller;

import RelTestFTA.RelTestFTA;
import RelTestFTA.model.XmiNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Z510 on 14/1/2559.
 */
public class TestInput extends Input {
    public TestInput(String name) throws IOException {
        this.openXml(name);
        populateSourceAndTargetNode();
        sortAndIndexUmlNode();
        setSuccess(true);
    }
    /**
     * Method Name : openXml
     * Parameter   : filename
     * Description : parse xmi file to variable xmiNode
     * Output      : xmiNodes
     */
    protected void openXml(String filename) throws IOException {


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
            document = builder.parse(ClassLoader.getSystemResourceAsStream(filename));
        } catch (SAXException ex) {
            Logger.getLogger(RelTestFTA.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Iterating through the nodes and extracting the data.

        NodeList nodeList = document != null ? document.getDocumentElement().getChildNodes() : null;

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
}
