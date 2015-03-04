package com.practise.xmlparse.xmldom;

import org.kxml2.kdom.Element;

/**
 * Created by e00959 on 2/25/2015.
 */
public class NodeElementPOJO {

    private String keyChildName;
    private String keyChildValue;
    private String valueChildName;
    private String valueChildValue;
    private String elementPath;
    private Element referenceNode;
    private String elementType;
    private int modeOfComparison;
    private boolean isToBeAdded;
    private Element parentNode;

    NodeElementPOJO(String keyChildName, String keyChildValue, String valueChildName, String valueChildValue,
                    String parentReferenceAddress, Element referenceNode, String elementType,
                    int modeOfComparison,boolean isToBeAdded,Element parentNode)
    {
        this.keyChildName=keyChildName;
        this.keyChildValue=keyChildValue;
        this.valueChildName=valueChildName;
        this.valueChildValue=valueChildValue;
        this.elementPath =parentReferenceAddress;
        this.referenceNode =referenceNode;
        this.elementType =elementType;
        this.modeOfComparison=modeOfComparison;
        this.isToBeAdded=isToBeAdded;
        this.parentNode=parentNode;
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

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
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
}
