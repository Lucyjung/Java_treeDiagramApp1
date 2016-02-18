package RelTestFTA.config;

/**
 * Created by Lucy on 14/1/2559.
 */
public enum Configurations {;
    public static boolean PRINT_DEBUG_INFO = true;       // debug info
    public static boolean TEST_MODE = false;              // Test mode will read file from below config
    public static String TEST_FILE = "xml/testxmi2.xml";
    //public static String TEST_FILE = "xml/firstuml.xml";
    public static String TEST_GOAL = "Accept transaction";
    //public static String TEST_GOAL = "Accept Transection";
    public static String TEST_INVALID_GOAL = "Reject transaction";

    // App Lang display
    public static String HEADER_APP = "Reliability Tests for Process Flow with Fault Tree Analysis";
    public static String HEADER_CCTM_FRAME = "Condition-Classification Tree Method";
    public static String HEADER_FTD_FRAME = "Fault Tree Diagram";
    public static String HEADER_STD_FRAME = "Success Tree Diagram";

    public static String CCTM_BUTTON_NAME = "View CCTM";
    public static String STD_BUTTON_NAME = "View STD";
    public static String FTD_BUTTON_NAME = "View FTD";
    public static String DIAGRAM_COLUMN_NAME   = "Diagram Generation";
    public static String TESTCASE_COLUMN_NAME   = "Test Case";
    public static String BUTTON_COLUMN_NAME   = "BUTTON";
    public static String CCTM_DISPLAY_TEXT   = "Condition-Classification Tree Method";
    public static String STD_DISPLAY_TEXT   = "Success Tree Diagram";
    public static String FTD_DISPLAY_TEXT   = "Fault Tree Diagram";
    public static int    CCTM_COLUMN_PREFERRED_WIDTH = 250;
    public static int    BUTTON_COLUMN   = 2;
    public static int    TESTCASE_COLUMN   = 1;
    public static int    NAME_COLUMN   = 0;
    //App size

    public static int APP_WIDTH = 668;
    public static int APP_HEIGHT = 345;
    public static int APP_LOCATION_X = 500;
    public static int APP_LOCATION_Y = 280;

    // Panel size
    public static int PANEL_WIDTH = 800;
    public static int PANEL_HEIGHT = 600;

    public static String AND_OPERATION = "AND";
    public static String OR_OPERATION = "OR";
    public static String NONE_OPERATION = "NONE";

    public static String EDGE_NO_ARROW = "none";
    public static String EDGE_DIAMOND_ARROW = "diamond";

    public static String IMAGE_PATH = "/resource/";
    public static String SHAPE_FILE_PATH = "/resource/shapes.xml";
    public static String TOP_EVENT_GATE_NAME = "Gate";
    public static String IMAGE_ICON = IMAGE_PATH + "rel_icon.png";

    // CCTM configuration
    public static int CCTM_WIDTH = 70; //50
    public static int CCTM_HEIGHT = 50;
    public static int CCTM_ORIGIN_X = 60;
    public static int CCTM_ORIGIN_Y = 10;
    public static int CCTM_GAP_Y = 20;
    public static int CCTM_GAP_X = 20;
    public static int CCTM_FONT_SIZE = 9;
    public static String CCTM_FONT_SIZE_STYLE = "fontSize="+ CCTM_FONT_SIZE;
    public static int CCTM_WORDWRAP_LIMIT = 10;

    // TestCase configuration
    public static int TESTCASE_WIDTH = 800;
    public static int TESTCASE_HEIGHT = 50;
    public static int TESTCASE_ORIGIN_X = 60;
    public static int TESTCASE_ORIGIN_Y = 200;
    public static int TESTCASE_GAP_Y = 20;
    public static int TESTCASE_GAP_X = 20;
    public static String TESTCASE_PREFIX_NAME = "Testcase ";

    // Condition Line configuration
    public static int CONDITION_LINE_WIDTH = 10; // min 10
    public static int CONDITION_LINE_HEIGHT= 550;

    // CCTM configuration
    public static int DIAGRAM_WIDTH = 85; //60
    public static int DIAGRAM_HEIGHT = 60; //80
    public static int DIAGRAM_ORIGIN_X= 60;
    public static int DIAGRAM_ORIGIN_Y = 10;
    public static int DIAGRAM_GAP_Y = 50;
    public static int DIAGRAM_GAP_X = 10;
    public static int MAX_WIDTH = 99999;
    public static int DIAGRAM_FONT_SIZE = 9;
    public static String DIAGRAM_FONT_SIZE_STYLE = "fontSize="+ DIAGRAM_FONT_SIZE;
    public static int DIAGRAM_WORDWRAP_LIMIT = 8;

    //Dot-connect configuration
    public static String DOT_NAME = "ROUNDED";
    public static String DOT_COLOR = "#000000";
    public static String DOT_FONT_COLOR = "#774400";

    public static String SHAPE_BLOCK = "fillColor=white;gradientColor=#ffffff;shadow=1";
    public static String SHAPE_OR = "shape=or_h";
    public static String SHAPE_AND = "shape=and_h";
    public static String SHAPE_VERTICAL_LINE = "shape=vline";
    public static String SHAPE_HORIZONTAL_TEXT_ALIGN = "align=right";
    public static String SHAPE_HORIZONTAL_GREEN_LINE = "shape=hline_green;"+SHAPE_HORIZONTAL_TEXT_ALIGN;
    public static String SHAPE_HORIZONTAL_RED_LINE = "shape=hline_red;"+SHAPE_HORIZONTAL_TEXT_ALIGN;

    public static String ARROW_DIAMOND = "startArrow=none;endArrow=diamond";
    public static String ARROW_NONE = "startArrow=none;endArrow=none;edgeStyle=topToBottomEdgeStyle";

}
