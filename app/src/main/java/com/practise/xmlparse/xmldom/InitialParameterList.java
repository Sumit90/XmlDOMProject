package com.practise.xmlparse.xmldom;

import com.practise.xmlparse.xmldom.NodeElementPOJO;
import com.practise.xmlparse.xmldom.RootElementPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e00959 on 2/6/2015.
 */

//The class will contains the Lists for initial elements provided for Root element and other elements
public class InitialParameterList {

    //This will store the initial root attributes to be included
    private List<RootElementPOJO> initialRootList;

    private List<NodeElementPOJO> initialNodeList;


    //--------------------------------------------------------------------------------------------------
    public InitialParameterList()
    {
        initialRootList =new ArrayList<RootElementPOJO>() ;
        initialNodeList =new ArrayList<NodeElementPOJO>();

    }
    //--------------------------------------------------------------------------------------------------
    public void addInitialRoot(RootElementPOJO rootElement)
    {
        initialRootList.add(rootElement);
    }
    //--------------------------------------------------------------------------------------------------
    public List<RootElementPOJO> getInitialRootList()
    {
        return initialRootList;
    }

    //--------------------------------------------------------------------------------------------------
    public void addInitialNodeAttribute(NodeElementPOJO nodeElement)
    {
        initialNodeList.add(nodeElement);
    }

    //--------------------------------------------------------------------------------------------------
    public List<NodeElementPOJO> getInitialNodeAttributeList()
    {
        return initialNodeList;
    }

    //--------------------------------------------------------------------------------------------------
    public int getModeComparison(String attributeName)
    {
        int mode=0;
        for(int count=0;count< initialRootList.size();count++)
        {
            RootElementPOJO rootElementPOJO= initialRootList.get(count);
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
