package com.practise.xmlparse.xmldom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e00959 on 2/6/2015.
 */

//The class will contains the Lists for initial elements provided for Root element and other elements
public class InitialParameterList {

    //This will store the initial root attributes to be included
    private List<RootElementPOJO> initialRootAttributes;

    private List<RootElementPOJO> initialNodeAttributes;


//--------------------------------------------------------------------------------------------------
    public InitialParameterList()
    {
        initialRootAttributes =new ArrayList<RootElementPOJO>() ;
        initialNodeAttributes=new ArrayList<RootElementPOJO>();

    }
//--------------------------------------------------------------------------------------------------
    public void addInitialRootAttributes(RootElementPOJO rootElement)
    {
        initialRootAttributes.add(rootElement);
    }
//--------------------------------------------------------------------------------------------------
    public List<RootElementPOJO> getRootParameterList()
    {
        return initialRootAttributes;
    }

//--------------------------------------------------------------------------------------------------
    public void addInitialNodeAttribute(RootElementPOJO nodeElement)
    {
        initialNodeAttributes.add(nodeElement);
    }

//--------------------------------------------------------------------------------------------------
    public List<RootElementPOJO> getNodeAttributeList()
{
    return initialNodeAttributes;
}

//--------------------------------------------------------------------------------------------------
    public int getModeComparison(String attributeName)
    {
        int mode=0;
        for(int count=0;count<initialRootAttributes.size();count++)
        {
            RootElementPOJO rootElementPOJO=initialRootAttributes.get(count);
            if(rootElementPOJO.getElementName().equals(attributeName))
            {
                mode=rootElementPOJO.getModeOfComparison();
                break;
            }
        }

        return mode;
    }
//--------------------------------------------------------------------------------------------------
}
