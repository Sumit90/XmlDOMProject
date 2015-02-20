package com.practise.xmlparse.xmldom;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends ActionBarActivity {

    private Button parseBtn;

    private InputStream inputStreamFile1;
    private InputStream inputStreamFile2;

    private XmlParser parseFile1;
    private XmlParser parseFile2;

    private InitialParameterList initialParameterList;

    private ParametersList ParameterListFile1;
    private ParametersList ParameterListFile2;
    private ParametersList ParameterListFinal;

    private FileParameterPOJO fileParameterPOJO;

    private final String TAG="MYLOGS";

    private boolean statusMerging=false;

    private WriteXmlFile writeFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parseBtn=(Button)findViewById(R.id.parse_button);


        parseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(init())
                    {
                        //get the root elements of the XML files
                        Element rootElementFile1 = parseFile1.parseRootElement();
                        Element rootElementFile2 = parseFile2.parseRootElement();

                        //add root parameters to list after comparing with initial root elements
                        addRootParameters(ParameterListFile1,rootElementFile1);
                        addRootParameters(ParameterListFile2,rootElementFile2);

                        printRootList(ParameterListFile1.getRootParameterList());
                        printRootList(ParameterListFile2.getRootParameterList());

                        //---print node element list---------------
                        printRootList(ParameterListFile1.getNodeElementList());

                        printRootList(ParameterListFile2.getNodeElementList());
                        //---print node element list---------------



                        List<RootElementPOJO> finalRootElementList =getMergedRootList
                                                        (ParameterListFile1.getRootParameterList(),
                                                            ParameterListFile2.getRootParameterList(),
                                                             initialParameterList.getRootParameterList());


                        if(finalRootElementList!=null && statusMerging)
                        {
                            Log.d(TAG,"---------- print final list start-------------");
                            printRootList(finalRootElementList);
                            Log.d(TAG,"---------- print final list end-------------");
                            ParameterListFinal.setRootParameterList(finalRootElementList);
                            Log.d(TAG,"---------- Writing XML start-------------");
                            writeFile = new WriteXmlFile(ParameterListFinal);
                            //writeFile.writeXml();
                            writeFile.writeRootElement();
                            addNodeAttributes(rootElementFile1,rootElementFile2);
                            writeFile.writeFinalXml();
                            Log.d(TAG,"---------- Writing Xml end-------------");
                        }
                        else
                        {
                            Log.d(TAG,"-------Cannot write the XML File-----------------");
                        }


                    }

                }

                /*catch (ParserConfigurationException e)
                {
                    Log.d(TAG,"inside ParserConfigurationException");
                    e.printStackTrace();
                }*/

                catch (Exception e) {

                    Log.d(TAG,"inside Exception");
                    e.printStackTrace();
                }

            }
        });
    }


//--------------------------------------------------------------------------------------------------
/*The init function is the first function that will called. This function does various tasks like
* 1) Getting the input stream files from user
 *2) Parse the files
 *3) Initialising FileParameterPOJO
 *4) Adding Root Tag name and required attributes to the initial Root Parameter List */
//--------------------------------------------------------------------------------------------------
    public boolean init()
    {
        boolean isInitSuccess;

        try {

          //----------------get the input streams of file here from user----------------------------
            inputStreamFile1 = getResources().getAssets().open("LogCodes.xml");
            inputStreamFile2 = getResources().getAssets().open("LogCodes1.xml");
          //----------------------------------------------------------------------------------------


            parseFile1 = new XmlParser(inputStreamFile1);
            parseFile2 =  new XmlParser(inputStreamFile2);

            fileParameterPOJO=new FileParameterPOJO(inputStreamFile1,inputStreamFile2
                    ,ComparisonConstants.PRIORITY_FILE1,"LogCodes.xml","",true);

            initialParameterList=new InitialParameterList();

            ParameterListFile1 =new ParametersList();
            ParameterListFile2 =new ParametersList();
            ParameterListFinal=new ParametersList();

            addInitialRootParameters("fastLogcodes",ComparisonConstants.COMPARE_EQUAL);
            addInitialRootParameters("fastLogcodes;A",ComparisonConstants.COMPARE_EQUAL);
            addInitialNodeAttributes("Lte;A;B;N", ComparisonConstants.PICK_FROM_FILE1);
            //addInitialNodeAttributes("Lte1;N", ComparisonConstants.PICK_FROM_FILE1);


            printRootList(initialParameterList.getRootParameterList());

            isInitSuccess=true;
        }

        catch(FileNotFoundException ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"inside init FileNotFoundException");
            ex.printStackTrace();

        }
        catch(IOException ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"inside init IOException");
            ex.printStackTrace();

        }
        catch (ParserConfigurationException e)
        {
            isInitSuccess=false;
            Log.d(TAG,"inside init ParserConfigurationException");
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            isInitSuccess=false;
            Log.d(TAG,"inside init SAXException");
            e.printStackTrace();
        }
        catch(Exception ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"inside init Exception");
            ex.printStackTrace();
        }

        return isInitSuccess;
    }


//--------------------------------------------------------------------------------------------------
//This function is used to add Initial Root attributes to be used for comparing and extracting
        void addInitialRootParameters(String attributeName,int mode)
        {
            initialParameterList.addInitialRootAttributes(new RootElementPOJO(attributeName, mode));
        }
//--------------------------------------------------------------------------------------------------
 //This function is used to add Initial Node attributes to be used for comparing and extracting
    void addInitialNodeAttributes(String attributeName,int mode)
    {
        initialParameterList.addInitialNodeAttribute(new RootElementPOJO(attributeName, mode));
    }

//--------------------------------------------------------------------------------------------------
    boolean addNodeAttributes(Element rootElementFile1,Element rootElementFile2)
    {
        boolean isSuccess=false;

        //This will give the Tag name of Root Element in file 1
        String rootTagNameFile1=rootElementFile1.getTagName();

        //This will give the Tag name of Root Element in file 1
        String rootTagNameFile2=rootElementFile2.getTagName();

        if(!(rootTagNameFile1.equals(rootTagNameFile2)))
        {
            return isSuccess;
        }

        //Get all initial root attributes set by the user`
        List<RootElementPOJO> initialNodes = initialParameterList.getNodeAttributeList();

        RootElementPOJO nodeElement=null;
        int mode=0;

        LOOP_OUTER:for(int count=0;count<initialNodes.size();count++)
        {
            nodeElement =  initialNodes.get(count);
            mode = nodeElement.getModeOfComparison();

            //This string contains Root name,Element Name and Type of data it contains separated by
            // deliminator
            String nodeElementName=nodeElement.getElementName();
            String []tagAttribute=nodeElementName.split(ComparisonConstants.DELIMINATOR);
            String dataType=tagAttribute[tagAttribute.length-1];

            if(dataType.equals(ComparisonConstants.NODE)) {



                switch (mode) {

                    case ComparisonConstants.PICK_FROM_FILE1:

                        if(writeOnlyNodeCaseElements(rootElementFile1,tagAttribute))
                        {
                            isSuccess=true;
                        }
                        else {
                            isSuccess=false;
                            break LOOP_OUTER;
                        }

                        break;

                    case ComparisonConstants.PICK_FROM_FILE2:

                        if(writeOnlyNodeCaseElements(rootElementFile2,tagAttribute))
                        {
                            isSuccess=true;
                        }
                        else {
                            isSuccess=false;
                            break LOOP_OUTER;
                        }


                        break;
                }
            }

        }

        return isSuccess;
    }

    private boolean writeOnlyNodeCaseElements(Element rootElement,String [] tagAttribute)
    {
        NodeList nodeList=null;
        Element nodeElement1=null;
        Element nodeElement2=null;
        boolean isSuccess=false;

        nodeList=rootElement.getElementsByTagName(tagAttribute[0]);

        if(nodeList!=null)
        {
            for (int nodeCount=0;nodeCount<nodeList.getLength();nodeCount++)
            {
                nodeElement1 = (Element)nodeList.item(nodeCount);
                nodeElement1=writeFile.writeNodeToRoot(nodeElement1.getTagName());
                if(nodeElement1!=null)
                {
                    for(int i=1;i<=tagAttribute.length-2;i++)
                    {
                        Element ele=writeFile.writeElementToNode(nodeElement1,tagAttribute[i]);
                        if(ele!=null)
                        {
                            nodeElement1=ele;
                        }
                    }
                    isSuccess=true;
                }
                else {

                    isSuccess=false;
                }

            }

        }

        return isSuccess;

    }


//--------------------------------------------------------------------------------------------------
//This function will add root attributes to root parameter list after comparing it with initial list
        void addRootParameters(ParametersList paramList,Element rootElement)
        {
           //This will give the Tag name of Root Element
            String rootTagName=rootElement.getTagName();
            //Get all initial root attributes set by the user
            List<RootElementPOJO> initialRootParams = initialParameterList.getRootParameterList();

            RootElementPOJO rootElementPOJO=null;

            /*Iterate the list to get all Initial Root Attributes set by user. Using those values
               get the attributes from rootElement and create a new list in ParametersList for root
               elements .
             */
            for (int count=0;count<initialRootParams.size();count++)
            {
                rootElementPOJO = initialRootParams.get(count);
                //This string contains tag name and attribute name of root element separated by
                // deliminator
                String rootElementName=rootElementPOJO.getElementName();

                //0th index contains tag name and 1st contains actual attribute name
                String []tagAttribute=rootElementName.split(ComparisonConstants.DELIMINATOR);

                // If tag name for root in initial list and root element matches
                if(tagAttribute[0].equals(rootTagName))
                {

                    if(tagAttribute.length>1)
                    {
                      /*Get the attribute value for attribute name given by initial root element list
                      after splitting (tagAttribute[1]) . tagAttribute[0] gives root tag name
                      */
                       String attributeValue=rootElement.getAttribute(tagAttribute[1]);

                    /* If the attribute exists in Root element create a RootPOJO object and add
                    * it to the list*/
                        if(!attributeValue.isEmpty())
                        {

                                paramList.addRootParameter(new
                                        RootElementPOJO(rootElementName, attributeValue,
                                        rootElementPOJO.getModeOfComparison()));
                        }
                    }

                    //If user has passed only Root tag name add it to the list.
                    else
                    {
                            paramList.addRootParameter(new
                                    RootElementPOJO(tagAttribute[0], "", rootElementPOJO.getModeOfComparison()));


                    }
                }
            }
        }


//--------------------------------------------------------------------------------------------------
    private List<RootElementPOJO> getMergedRootList(List<RootElementPOJO> listRootFile1,
                               List<RootElementPOJO> listRootFile2,List<RootElementPOJO> listRootInitial) {

        // This is the list that will contain merged elements.
        List<RootElementPOJO> finalRootList=null;

        // If the user has not provided any initial Root attribute list
        if(listRootInitial==null )
        {
            Log.d(TAG,"-----getMergedRootList(): listRootInitial==null");
        }
        // If no user specified attribute exists in any of the list
        else if((listRootFile1==null || listRootFile1.size()==0) && (listRootFile2==null || listRootFile2.size()==0))
        {
            Log.d(TAG,"-----getMergedRootList(): listRootFile1==null && listRootFile2==null");
        }
        else
        {
            finalRootList=new ArrayList<RootElementPOJO>();
            RootElementPOJO rootElement=null;
            RootElementPOJO rootElementFile1=null;
            RootElementPOJO rootElementFile2=null;

            int modeComparison=0;
            OUTERLOOP:for(int counter=0;counter<listRootInitial.size();counter++) {
                rootElement = listRootInitial.get(counter);

                //get the mode how element has to be pushed in final XML
                modeComparison = initialParameterList.getModeComparison(rootElement.getElementName());


                if (modeComparison == ComparisonConstants.COMPARE_EQUAL
                        || modeComparison == ComparisonConstants.COMPARE_GREATER_FILE1
                        || modeComparison == ComparisonConstants.COMPARE_GREATER_FILE2
                        || modeComparison == ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1
                        || modeComparison == ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2) {
                    //If file1 does not contain any of the elements exit the loop
                    if (listRootFile1 == null || listRootFile1.size() == 0) {
                        Log.d(TAG, "-----getMergedRootList(): listRootFile1==null");
                        statusMerging = false;
                        break OUTERLOOP;
                    }
                    //If file2 does not contain any of the elements exit the loop
                    else if (listRootFile2 == null || listRootFile2.size() == 0) {
                        Log.d(TAG, "-----getMergedRootList(): listRootFile2==null");
                        statusMerging = false;
                        break OUTERLOOP;
                    }


                // Check if element exists in File1 Root list
                rootElementFile1 = getElement(listRootFile1, rootElement);

                   /*If the required element is found in File1 then check for the element in File2*/
                if (rootElementFile1 != null) {
                    rootElementFile2 = getElement(listRootFile2, rootElement);

                }
                   /*If the required element not found in File1 then break the loop*/
                else {
                    statusMerging = false;
                    Log.d(TAG, "-----getMergedRootList(): " + rootElement.getElementName() + " Not Found in File1");
                    break OUTERLOOP;
                }

                    /* If the required element is found in File1 and File 2 then add the element
                    * in final list after checking which file has more priority*/

                 if (rootElementFile1 != null && rootElementFile2 != null) {
                    // If mode of comparison is compare for equal check for equality of element values
//--------------------------------------------------------------------------------------------------
                    if (modeComparison == ComparisonConstants.COMPARE_EQUAL) {
                     /*Compare the attribute values of File1 and File2. If they are equal add it to
                        final list else exit the outer loop*/
                        if (compareAttributeValues(rootElementFile1.getElementValue(), rootElementFile2.getElementValue(),
                                ComparisonConstants.COMPARE_EQUAL)) {
                           /*If file1 has more priority then add the element in final list from file1 list*/
                            if (fileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE1) {
                                finalRootList.add(rootElementFile1);
                            }
                            /*If file2 has more priority then add the element in final list from file2 list*/
                            else if (fileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE2) {
                                finalRootList.add(rootElementFile2);
                            }
                            statusMerging = true;
                        }
                        // If the attribute value if File1 and File2 are not equal
                        else {
                            Log.d(TAG, "-----getMergedRootList():COMPARE_EQUAL " + rootElementFile1.getElementValue() + " != " + rootElementFile2.getElementValue());
                            statusMerging = false;
                            break OUTERLOOP;
                        }

                    }
//--------------------------------------------------------------------------------------------------
                     /*Check if the attribute value is greater in File1. If true add the attribute
                       from File1 */
                    else if (modeComparison == ComparisonConstants.COMPARE_GREATER_FILE1) {
                        if (compareAttributeValues(rootElementFile1.getElementValue(), rootElementFile2.getElementValue(),
                                ComparisonConstants.COMPARE_GREATER_FILE1)) {

                            finalRootList.add(rootElementFile1);
                            statusMerging = true;
                        } else {
                            Log.d(TAG, "-----getMergedRootList():COMPARE_GREATER_FILE1 " + rootElementFile1.getElementValue() + " < " + rootElementFile2.getElementValue());
                            statusMerging = false;
                            break OUTERLOOP;
                        }
                    }
//--------------------------------------------------------------------------------------------------
                    /*Check if the attribute value is greater in File2. If true add the attribute
                     from File2 */
                    else if (modeComparison == ComparisonConstants.COMPARE_GREATER_FILE2) {
                        if (compareAttributeValues(rootElementFile1.getElementValue(), rootElementFile2.getElementValue(),
                                ComparisonConstants.COMPARE_GREATER_FILE2)) {
                            finalRootList.add(rootElementFile2);
                            statusMerging = true;
                        } else {
                            Log.d(TAG, "-----getMergedRootList():COMPARE_GREATER_FILE2 " + rootElementFile2.getElementValue() + " < " + rootElementFile1.getElementValue());
                            statusMerging = false;
                            break OUTERLOOP;
                        }
                    }
//--------------------------------------------------------------------------------------------------
                    /*Check if the attribute value is greater or equal in File1. If true add the attribute
                      from File1 */
                    else if (modeComparison == ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1) {
                        if (compareAttributeValues(rootElementFile1.getElementValue(), rootElementFile2.getElementValue(),
                                ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1)) {
                            finalRootList.add(rootElementFile1);
                            statusMerging = true;
                        } else {
                            Log.d(TAG, "-----getMergedRootList():COMPARE_GREATER_FILE2 " + rootElementFile1.getElementValue() + " < !=" + rootElementFile2.getElementValue());
                            statusMerging = false;
                            break OUTERLOOP;
                        }
                    }
//--------------------------------------------------------------------------------------------------

                    /*Check if the attribute value is greater or equal in File2. If true add the attribute
                      from File2 */
                    else if (modeComparison == ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2) {
                        if (compareAttributeValues(rootElementFile1.getElementValue(), rootElementFile2.getElementValue(),
                                ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2)) {
                            finalRootList.add(rootElementFile2);
                            statusMerging = true;
                        } else {
                            Log.d(TAG, "-----getMergedRootList():COMPARE_GREATER_FILE2 " + rootElementFile2.getElementValue() + " < 1=" + rootElementFile1.getElementValue());
                            statusMerging = false;
                            break OUTERLOOP;
                        }
                    }
//--------------------------------------------------------------------------------------------------

                 }
                     /*If the required element not found in File2 then break the loop*/
                else {
                    if (rootElementFile2 == null) {
                        Log.d(TAG, "-----getMergedRootList(): " + rootElement.getElementName() + " Not Found File2");
                        statusMerging = false;
                    }

                    break OUTERLOOP;
                  }
            }
             /*Always Pick the element from the File1. If element exists it will be added in final
             * list otherwise loop will get break.*/
            else if(modeComparison==ComparisonConstants.PICK_FROM_FILE1)
            {
                //If file1 does not contain any of the elements exit the loop
                if (listRootFile1 == null || listRootFile1.size() == 0) {
                    Log.d(TAG, "-----getMergedRootList(): listRootFile1==null");
                    statusMerging = false;
                    break OUTERLOOP;
                }
                else {
                    // Check if element exists in File1 Root list
                    rootElementFile1 = getElement(listRootFile1, rootElement);

                   /*If the required element is found in File1 then add it the final list*/
                    if (rootElementFile1 != null) {
                        finalRootList.add(rootElementFile2);
                        statusMerging = true;
                    }
                   /*If the required element not found in File1 then break the loop*/
                    else {
                        statusMerging = false;
                        Log.d(TAG, "-----getMergedRootList():PICK_FROM_FILE1 " + rootElement.getElementName() + " Not Found in File1");
                        break OUTERLOOP;
                    }

                }
            }
             /*Always Pick the element from the File2. If element exists it will be added in final
             * list otherwise loop will get break.*/
                else if(modeComparison==ComparisonConstants.PICK_FROM_FILE2)
            {
                //If file2 does not contain any of the elements exit the loop
                if (listRootFile2 == null || listRootFile2.size() == 0) {
                    Log.d(TAG, "-----getMergedRootList(): listRootFile2==null");
                    statusMerging = false;
                    break OUTERLOOP;
                }
                else {
                    // Check if element exists in File2 Root list
                    rootElementFile2 = getElement(listRootFile2, rootElement);

                   /*If the required element is found in File1 then add it the final list*/
                    if (rootElementFile2 != null) {
                        finalRootList.add(rootElementFile2);
                        statusMerging = true;
                    }
                   /*If the required element not found in File2 then break the loop*/
                    else {
                        statusMerging = false;
                        Log.d(TAG, "-----getMergedRootList():PICK_FROM_FILE2 " + rootElement.getElementName() + " Not Found in File2");
                        break OUTERLOOP;
                    }

                }

            }
             /*If user has specified the mode of comparison to be NO_COMPARISON*/
            else if(modeComparison==ComparisonConstants.NO_COMPARISON)
            {
              /* If both the file list are non empty then check if the element exists in both the lists.
              */
              if((listRootFile1!=null&&listRootFile1.size()>0)&&(listRootFile2!=null&&listRootFile2.size()>0))
              {
                  rootElementFile1=getElement(listRootFile1, rootElement);
                  rootElementFile2=getElement(listRootFile2, rootElement);

               /*If the element does not exists in both of the lists then break the loop.*/
                  if(rootElementFile1==null && rootElementFile2==null)
                  {
                      statusMerging = false;
                      Log.d(TAG, "-----getMergedRootList():NO_COMPARISON "+" rootElementFile1==null && rootElementFile2==null");
                      break OUTERLOOP;
                  }
                  /*If the element exists in File1 list add it to the final list*/
                  else if(rootElementFile1!=null && rootElementFile2==null)
                  {
                      finalRootList.add(rootElementFile1);
                      statusMerging = true;
                  }
                   /*If the element exists in File2 list add it to the final list*/
                  else if(rootElementFile1==null && rootElementFile2!=null)
                  {
                      finalRootList.add(rootElementFile2);
                      statusMerging = true;
                  }
                  /*If the element exists in both the lists then check the file priority.*/
                  else if(rootElementFile1!=null && rootElementFile2!=null)
                  {
                      /*If the File 1 has more priority then add the element form File1 list*/
                      if(fileParameterPOJO.getFilePriority()==ComparisonConstants.PRIORITY_FILE1)
                      {
                          finalRootList.add(rootElementFile1);
                          statusMerging = true;
                      }
                      /*If the File 2 has more priority then add the element form File2 list*/
                      else  if(fileParameterPOJO.getFilePriority()==ComparisonConstants.PRIORITY_FILE2)
                      {
                          finalRootList.add(rootElementFile2);
                          statusMerging = true;
                      }
                  }
              }
              /*If File 1 list does not contain any element but File 2 list contains elements*/
              else if ((listRootFile1==null || listRootFile1.size()==0)&&(listRootFile2!=null&&listRootFile2.size()>0))
              {
                  rootElementFile2=getElement(listRootFile2, rootElement);
                  /*If element exists in File 2 list add it to final list*/
                  if(rootElementFile2!=null)
                  {
                      finalRootList.add(rootElementFile2);
                      statusMerging = true;
                  }
                  /*If element does not exists in File 2 list break the loop as element was not found
                  * in either of the lists*/
                  else
                  {
                      statusMerging = false;
                      Log.d(TAG, "-----getMergedRootList():NO_COMPARISON "+" (listRootFile1==null || listRootFile1.size()==0)&&(listRootFile2!=null&&listRootFile2.size()>0)");
                      break OUTERLOOP;
                  }


              }
              /*If File 2 list does not contain any element but File 1 list contains elements*/
              else if ((listRootFile1!=null&&listRootFile1.size()>0)&&(listRootFile2==null&&listRootFile2.size()==0))
              {
                  rootElementFile1=getElement(listRootFile1,rootElement);
                  /*If element exists in File 1 list add it to final list*/
                  if(rootElementFile1!=null)
                  {
                      finalRootList.add(rootElementFile1);
                      statusMerging = true;
                  }
                  /*If element does not exists in File 1 list break the loop as element was not found
                  * in either of the lists*/
                  else
                  {
                      statusMerging = false;
                      Log.d(TAG, "-----getMergedRootList():NO_COMPARISON "+" (listRootFile1!=null&&listRootFile1.size()>0)&&(listRootFile2==null&&listRootFile2.size()==0)");
                      break OUTERLOOP;
                  }
              }
            }
          }
       }


        return finalRootList;


    }
//--------------------------------------------------------------------------------------------------
    /*This function searches for a element searchElement in list listElements and returns the
      searched element from the list. If no element found it will return null object*/
    private RootElementPOJO getElement(List<RootElementPOJO> listElements,RootElementPOJO searchElement)
    {
        int l_i=0;
        RootElementPOJO searchedElement=null;
        for ( l_i=0;l_i<listElements.size();l_i++)
        {
            if(listElements.get(l_i).getElementName().equals(searchElement.getElementName()))
            {
                searchedElement=listElements.get(l_i);
                break;
            }
        }
        return searchedElement;
    }

//--------------------------------------------------------------------------------------------------

    /*This function will compare the two attribute values on the basis of comparison type and
    * will the return the boolean result*/
    public boolean compareAttributeValues(String attrValue1,String attrValue2,int comparisonType)
    {
        boolean result=false;

        switch (comparisonType)
        {
            /*Compare attribute Values for equality */
            case ComparisonConstants.COMPARE_EQUAL:
                if((attrValue1.trim()).equals(attrValue2.trim()))
                {
                    result=true;
                }
                break;

            /*Compare attribute Values for value greater in File1 */
            case ComparisonConstants.COMPARE_GREATER_FILE1:

                if(((attrValue1.trim()).compareTo(attrValue2))>0)
                {
                    result=true;
                }

                break;

            /*Compare attribute Values for value greater in File2 */
            case ComparisonConstants.COMPARE_GREATER_FILE2:

                if(((attrValue2.trim()).compareTo(attrValue1))>0)
                {
                    result=true;
                }

                break;

            /*Compare attribute Values for value greater or equal in File1 */
            case ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1:

                if(((attrValue1.trim()).compareTo(attrValue2))>=0)
                {
                    result=true;
                }

                break;

            /*Compare attribute Values for value greater or equal in File2 */
            case ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2:

                if(((attrValue2.trim()).compareTo(attrValue1))>=0)
                {
                    result=true;
                }

                break;

            default:

                break;

        }

        return result;
    }


//--------------------------------------------------------------------------------------------------

// This function has been created for debugging purpose for printing the list of elements
   private void printRootList(List<RootElementPOJO> list)
   {

       Log.d(TAG,"--------inside printRootList Start-----------");
       for (int i=0;i<list.size();i++)
       {
           RootElementPOJO elementPOJO=list.get(i);
           Log.d(TAG,"Root Element Name :"+elementPOJO.getElementName());
           Log.d(TAG,"Root Element Value :"+elementPOJO.getElementValue());
           Log.d(TAG,"Root Element Mode :"+elementPOJO.getModeOfComparison());
       }

       Log.d(TAG,"--------inside printRootList End-----------");
   }

//--------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
