package com.practise.xmlparse.xmldom;

import org.kxml2.kdom.Element;

/**
 * This class will contain the attributes related to a particular node element
 */
public class NodeElementPOJO {

    private String keyChildName; // This will store the key name
    private String keyChildValue;// This will store the key value
    private String valueChildName; // This will store the value name
    private String valueChildValue; // This will store the value value
    private String elementPath; //This will store the complete element hierarchy not including root
    private Element referenceNode; //This will contain the reference node
    private int modeOfComparison; // This will contain the mode of comparison
    private boolean isToBeAdded;  //This boolean will tell whether the key value element has to be removed
    private Element parentNode;  // This will store the parent node
    private Element nodeItself;  // This will store the node itself

    /**
     * Constructor to create NodeElement
     * @param keyChildName - This will store the key name
     * @param keyChildValue - This will store the key value
     * @param valueChildName - This will store the value name
     * @param valueChildValue - This will store the value value
     * @param parentReferenceAddress -This will store the complete element hierarchy not including root
     * @param referenceNode -  This will contain the reference node
     * @param modeOfComparison - This will contain the mode of comparison
     * @param isToBeAdded -This boolean will tell whether the key value element has been added
     * @param parentNode - This will store the parent node
     * @param nodeItself - This will store the node itself
     */
    NodeElementPOJO(String keyChildName, String keyChildValue, String valueChildName, String valueChildValue,
                    String parentReferenceAddress, Element referenceNode,
                    int modeOfComparison,boolean isToBeAdded,Element parentNode,Element nodeItself)
    {
        this.keyChildName=keyChildName;
        this.keyChildValue=keyChildValue;
        this.valueChildName=valueChildName;
        this.valueChildValue=valueChildValue;
        this.elementPath =parentReferenceAddress;
        this.referenceNode =referenceNode;
        this.modeOfComparison=modeOfComparison;
        this.isToBeAdded=isToBeAdded;
        this.parentNode=parentNode;
        this.nodeItself=nodeItself;
    }

    public String getKeyChildName() {
        return keyChildName;
    }

    public void setKeyChildName(String keyChildName) {
        this.keyChildName = keyChildName;
    }

    public String getKeyChildValue() {
        return keyChildValue;
    }

    public void setKeyChildValue(String keyChildValue) {
        this.keyChildValue = keyChildValue;
    }

    public String getValueChildName() {
        return valueChildName;
    }

    public void setValueChildName(String valueChildName) {
        this.valueChildName = valueChildName;
    }

    public String getValueChildValue() {
        return valueChildValue;
    }

    public void setValueChildValue(String valueChildValue) {
        this.valueChildValue = valueChildValue;
    }

    public String getElementPath() {
        return elementPath;
    }

    public void setElementPath(String elementPath) {
        this.elementPath = elementPath;
    }

    public Element getReferenceNode() {
        return referenceNode;
    }

    public void setReferenceNode(Element referenceNode) {
        this.referenceNode = referenceNode;
    }

    public int getModeOfComparison() {
        return modeOfComparison;
    }

    public void setModeOfComparison(int modeOfComparison) {
        this.modeOfComparison = modeOfComparison;
    }

    public boolean isToBeAdded() {
        return isToBeAdded;
    }

    public void setToBeAdded(boolean isToBeAdded) {
        this.isToBeAdded = isToBeAdded;
    }

    public Element getParentNode() {
        return parentNode;
    }

    public void setParentNode(Element parentNode) {
        this.parentNode = parentNode;
    }

    public Element getNodeItself() {
        return nodeItself;
    }

    public void setNodeItself(Element nodeItself) {
        this.nodeItself = nodeItself;
    }
}
