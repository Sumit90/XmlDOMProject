package com.practise.xmlparse.xmldom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e00959 on 2/4/2015.
 */

//This class will provide the Lists that will contain Root elements and other elements of XML
public class ParametersList {

    // This will contain the mandatory attribute list at root element
    private List<RootElementPOJO> rootAttributes;

    // This will contain node elements
    private List<RootElementPOJO> nodeElementList;


//--------------------------------------------------------------------------------------------------
    public ParametersList()
    {
        rootAttributes =new ArrayList<RootElementPOJO>() ;
        nodeElementList =new ArrayList<RootElementPOJO>() ;

    }
//--------------------------------------------------------------------------------------------------
   //Method used to add Root element to rootAttributes List
    public void addRootParameter(RootElementPOJO rootElement)
    {
        rootAttributes.add(rootElement);
    }
//--------------------------------------------------------------------------------------------------
//Method used to get rootAttributes List containing root elements
    public List<RootElementPOJO> getRootParameterList()
    {
        return rootAttributes;
    }
//--------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------
    //Method used to add node  element to the List
    public void addNodeElement(RootElementPOJO nodeElement)
    {
        nodeElementList.add(nodeElement);
    }
    //--------------------------------------------------------------------------------------------------
//Method used to get rootAttributes List containing root elements
    public List<RootElementPOJO> getNodeElementList()
    {
        return nodeElementList;
    }
    //--------------------------------------------------------------------------------------------------
    public void setRootParameterList(List<RootElementPOJO> rootParameterList)
    {
        rootAttributes=rootParameterList;
    }
//--------------------------------------------------------------------------------------------------

}
