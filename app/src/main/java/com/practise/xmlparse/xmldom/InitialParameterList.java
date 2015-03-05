package com.practise.xmlparse.xmldom;

import com.practise.xmlparse.xmldom.NodeElementPOJO;
import com.practise.xmlparse.xmldom.RootElementPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * The class will contains the Lists for initial elements provided for Root element and node elements
 */


public class InitialParameterList {

    //This will store the initial root attributes to be included
    private List<RootElementPOJO> initialRootList;

    //This will store the initial node elements to be searched and added
    private List<NodeElementPOJO> initialNodeList;



    public InitialParameterList()
    {
        initialRootList =new ArrayList<RootElementPOJO>() ;
        initialNodeList =new ArrayList<NodeElementPOJO>();

    }

    public void addInitialRoot(RootElementPOJO rootElement)
    {
        initialRootList.add(rootElement);
    }

    public List<RootElementPOJO> getInitialRootList()
    {
        return initialRootList;
    }


    public void addInitialNodeAttribute(NodeElementPOJO nodeElement)
    {
        initialNodeList.add(nodeElement);
    }

    //--------------------------------------------------------------------------------------------------
    public List<NodeElementPOJO> getInitialNodeAttributeList()
    {
        return initialNodeList;
    }


}
