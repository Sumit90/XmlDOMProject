package com.practise.xmlparse.xmldom;

/**
 * Created by e00959 on 2/4/2015.
 */
public class RootElementPOJO {

    private String elementName; // This parameter will represent the Root element attribute name
    private String elementValue; // This parameter will represent the Root element attribute Value
    private int modeOfComparison; // This parameter will represent mode of comparison
//--------------------------------------------------------------------------------------------------
    public RootElementPOJO(String elementName, String elementValue, int modeOfComparison)
    {
        this.elementName = elementName;
        this.elementValue = elementValue;
        this.modeOfComparison = modeOfComparison;

    }
//--------------------------------------------------------------------------------------------------
    public RootElementPOJO(String elementName)
    {
        this.elementName = elementName;

    }
//--------------------------------------------------------------------------------------------------
    public RootElementPOJO(String elementName, int modeOfComparison)
    {
        this.elementName = elementName;
        this.modeOfComparison = modeOfComparison;

    }
//--------------------------------------------------------------------------------------------------
    public String getElementName() {
        return elementName;
    }


    public String getElementValue() {
        return elementValue;
    }

//--------------------------------------------------------------------------------------------------
    public int getModeOfComparison() {
        return modeOfComparison;
    }
//--------------------------------------------------------------------------------------------------
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }
//--------------------------------------------------------------------------------------------------
    public void setElementValue(String elementValue) {
        this.elementValue = elementValue;
    }
//--------------------------------------------------------------------------------------------------
    public void setModeOfComparison(int modeOfComparison) {
        this.modeOfComparison = modeOfComparison;
    }
//--------------------------------------------------------------------------------------------------
}
