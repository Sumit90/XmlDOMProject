package com.practise.xmlparse.xmldom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e00959 on 2/4/2015.
 */
public class ParametersList {

    // This will contain the mandatory attribute list at root element
    private List<RootElementPOJO> rootAttributes;


    public ParametersList()
    {
        rootAttributes =new ArrayList<RootElementPOJO>() ;

    }

    public void addRootParameter(RootElementPOJO rootElement)
    {
        rootAttributes.add(rootElement);
    }

    public List<RootElementPOJO> getRootParameterList()
    {
        return rootAttributes;
    }

   /* public RootElementPOJO getRootParameter(String rootElementName)
    {
        RootElementPOJO elementPOJO=null;

        for(int i=0;i<rootAttributes.size();i++)
        {
            elementPOJO=(RootElementPOJO)rootAttributes.get(i);
            String []split=elementPOJO.getElementName().split(";");
            if(elementPOJO.getElementName().equals(rootElementName))
            {
                break;
            }
        }

        return elementPOJO;
    }*/




}
