package com.practise.xmlparse.xmldom;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by e00959 on 3/5/2015.
 */
public class XmlMergingHelper {

    private Context mContext;
    private XmlParser mParseFile1;
    private XmlParser mParseFile2;
    private InitialParameterList mInitialParameterList;
    private FileParameterPOJO mFileParameterPOJO;
    private final String TAG="InitiateMergingUtility";
    private WriteXmlFile mWriteFile;
    private Element mRootElementFile1;
    private Element mRootElementFile2;
    private Element mRootElementFinal;
    private String[] mHierarchy;
    private int mIndex=0;
    private Element mNodeToBeAdded;
    private boolean mIsSuccess =false;
    private String mKeyChildName="";
    private String mValueChildName="";
    private int mModeOfComparison=0;
    private String mElementPath="";
    private List<NodeElementPOJO> mNodeListFile1;
    private List<NodeElementPOJO> mNodeListFile2;
    private List<NodeElementPOJO> mNodeListFinal;
    private Element mParentNode=null;
    private Element mParentElement;
    private List<NodeElementPOJO> mListItemsAdded;
    private int mParentIndex=0;
    Element mainParent=null;

    public XmlMergingHelper(Context context)
    {
        mContext=context;
    }


    /**This function is the entry point for parsing. It will check whether both the files exists or not.
     * If both the files exists then only it will proceed further executions.
     *
     * @param file1Name- Name of file present in sdcard
     * @param file1Path  Path of file present in sdcard
     * @param file2Name  Name of file present in assets
     */
     public void start(String file1Name,String file1Path,String file2Name)
    {
        try {

            boolean isFileExists=false;
            InputStream inputStreamFile1=null;
            InputStream inputStreamFile2=null;

            //----------------------Input Stream for Both the files Start-------------------------------

            //If file1 Exists in Sdcard or external storage
            File sdCard = Environment.getExternalStorageDirectory();
            File file1=new File(sdCard+file1Path,file1Name);

            //TODO
            isFileExists=true;
           /* if(file1.exists())
            {
                inputStreamFile1=new FileInputStream(file1);
                isFileExists=true;
            }*/

            //If file2 Exists in assets
            try {
                inputStreamFile1=mContext.getResources().getAssets().open("LogCodes.xml");
                inputStreamFile2=mContext.getResources().getAssets().open("LogCodes1.xml");
                //TODO
                //inputStreamFile2=mContext.getResources().getAssets().open(file2Name);
            }
            catch(FileNotFoundException ex )
            {
                isFileExists=false;
                ex.printStackTrace();
                Log.d(TAG, "inputStreamFile2 FileNotFoundException");
            }
            catch (IOException ex)
            {
                isFileExists=false;
                ex.printStackTrace();
                Log.d(TAG,"inputStreamFile2 FileNotFoundException");
            }
            //----------------------Input Stream for Both the files end -------------------------------

        /*If both the files exists then execute the Init function that returns a boolean indicating
        * that it has been executed properly*/

            if (isFileExists && init(inputStreamFile1,inputStreamFile2)) {

                //get the root elements of the XML files
                mRootElementFile1 = mParseFile1.parseRootElement();
                mRootElementFile2 = mParseFile2.parseRootElement();

                //If no root elements exists in either of the file
                if (mRootElementFile1 != null && mRootElementFile2 != null) {

                    //If the name of root elements is same in both the files
                    if(((mRootElementFile1.getName()).equals(mRootElementFile2.getName())))
                    {
                        mWriteFile = new WriteXmlFile(mFileParameterPOJO);

                        //If the Root element was created successfully for the final file

                        if(mFileParameterPOJO.getFilePriority()==ComparisonConstants.PRIORITY_FILE1)
                        {
                            mRootElementFinal=mRootElementFile1;
                        }
                        else
                        {
                            mRootElementFinal=mRootElementFile2;
                        }

                        if(mWriteFile.addRootElementToDocument(mRootElementFinal))
                        {
                            if(compareAndMergeRootParameters(mRootElementFinal
                                    ,mRootElementFile1,mRootElementFile2))
                            {
                                Log.d(TAG, "---------- check writeNodesToXml()-------------");

                                if (writeNodesToXml())
                                {
                                    mWriteFile.WriteXml();
                                }
                                else
                                {
                                    Log.d(TAG, "Error while adding nodes writeNodesToXml.");
                                }


                            }
                            else
                            {
                                Log.d(TAG, "-------Cannot write Root Attributes-----------------");
                            }
                        }
                        else
                        {
                            Log.d(TAG, "-------Cannot create the root element-----------------");
                        }

                    }
                    else
                    {
                        Log.d(TAG, "---Cannot write the XML File; Root Elements Not same---");

                    }
                }
            }
        }
        catch (NullPointerException ex)
        {
            Log.d(TAG,"inside  NullPointerException");
            ex.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            Log.d(TAG, "inside FileNotFoundException");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.d(TAG, "inside FileNotFoundException");
            e.printStackTrace();
        }
        catch (Exception e) {

            Log.d(TAG, "inside Exception");
            e.printStackTrace();
        }
    }



    /**The init function is the first function that will called. This function does various tasks like
     * 1) Getting the input stream files from user
     *2) Parse the files
     *3) Initialising FileParameterPOJO
     *4) Adding Initial Root Parameters Name
     *5) Use the function addInitialRootParameters to apply rules on root attribute. Pass the attribute
     * name with symbol @ following the attribute name*
     *6) Use the function addInitialNodeParameters to apply rules on inner elements
     *
     * Permissible Rules that can be applied for Root Attributes are:
     *  1)PICK_FROM_FILE1
     *  2)PICK_FROM_FILE2
     *  3)COMPARE_EQUAL
     *  4)COMPARE_GREATER_FILE1
     *  5)COMPARE_GREATER_FILE2
     *  6)COMPARE_GREATER_EQUAL_FILE1
     *  7)COMPARE_GREATER_EQUAL_FILE2
     *  8)NO_COMPARISON
     *
     *

     * @param inputStreamFile1-Input Stream from Sdcard File
     * @param inputStreamFile2-Input Stream from Assets File
     * @return boolean indicating whether initialisation was successful
     */
    private boolean init(InputStream inputStreamFile1,InputStream inputStreamFile2)
    {
        boolean isInitSuccess;

        try {


            mParseFile1 = new XmlParser(inputStreamFile1);
            mParseFile2 =  new XmlParser(inputStreamFile2);

            mNodeListFile1=new ArrayList<NodeElementPOJO>();
            mNodeListFile2=new ArrayList<NodeElementPOJO>();
            mNodeListFinal=new ArrayList<NodeElementPOJO>();
            mListItemsAdded=new ArrayList<NodeElementPOJO>();

            mFileParameterPOJO =new FileParameterPOJO(ComparisonConstants.PRIORITY_FILE1,
                    "LogCodesFinal.xml","/sdcard/FTA/Log/");

            mInitialParameterList =new InitialParameterList();

            addInitialRootParameters("@version",ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2);
            addInitialRootParameters("@name",ComparisonConstants.NO_COMPARISON);

            addInitialNodeParameters("name", "value", "LTE/LTE_LOGCODE",ComparisonConstants.PICK_FROM_FILE1);
           // addInitialNodeParameters("name", "value1", "LTE/LTE_LOGCODE",ComparisonConstants.PICK_FROM_FILE2);
            //addInitialNodeParameters("name", "value", "WCDMA/Wcdma_logcode",ComparisonConstants.PICK_FROM_FILE2);

            //addInitialNodeParameters("name", "value", "LTE/Lte_logcode",ComparisonConstants.PICK_FROM_FILE2);



           /* addInitialNodeParameters("name",  "value",  "WCDMA/Wcdma_logcode",
                    ComparisonConstants.KEY_VALUE, ComparisonConstants.PICK_FROM_FILE1);

            addInitialNodeParameters("name",  "value",  "GSM",
                     ComparisonConstants.NODE, ComparisonConstants.NO_COMPARISON);

            addInitialNodeParameters("name",  "value",  "CDMA",
                    ComparisonConstants.NODE, ComparisonConstants.NO_COMPARISON);

            addInitialNodeParameters("name",  "value",  "EVDO",
                   ComparisonConstants.NODE, ComparisonConstants.NO_COMPARISON);*/


            printRootList(mInitialParameterList.getInitialRootList());
            isInitSuccess=true;
        }

        catch (NullPointerException ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"[init]:inside init NullPointerException");
            ex.printStackTrace();
        }
        catch(FileNotFoundException ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"[init]:inside init FileNotFoundException");
            ex.printStackTrace();

        }
        catch (XmlPullParserException ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"[init]:inside init XmlPullParserException");
            ex.printStackTrace();
        }
        catch(IOException ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"[init]:inside init IOException");
            ex.printStackTrace();
        }
        catch(Exception ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"[init]:inside init Exception");
            ex.printStackTrace();
        }

        return isInitSuccess;
    }

    /**This function is used to add Initial Root attributes provided by the user to be used for
     * comparing and extracting
     *
     * @param attributeName - Name of the attribute to be compared
     * @param mode - Type of comparison Mode
     */
    private void addInitialRootParameters(String attributeName,int mode)
    {
        mInitialParameterList.addInitialRoot(new RootElementPOJO(attributeName, mode));
    }

    //

    /**
     * This function is used to add Initial Node attributes to be used for comparing and extracting
     * @param keyChildName - Key Child To be searched
     * @param valueChildName - Value of Searched Key
     * @param elementPath - Element hierarchy to be searched
     * @param modeOfComparison - Mode of Search
     */
    private void addInitialNodeParameters(String keyChildName,String valueChildName,
                                           String elementPath,int modeOfComparison
    )
    {
        boolean isTobeAdded = false;
        String keyChildValue="";
        String valueChildValue="";
        Element parentReferenceNode=null;
        mInitialParameterList.addInitialNodeAttribute(new NodeElementPOJO(keyChildName, keyChildValue,
                valueChildName, valueChildValue, elementPath, parentReferenceNode,
                modeOfComparison,isTobeAdded,null,null));

    }


    //

    /**
     * This function will compare the root parameters attributes from both the Xml files and then
     * add it to final root element depending upon the type of comparison.
     *
     * @param rootElementFinal - Final Xml File Root Element
     * @param mRootElementFile1 - File 1 Root Element
     * @param mRootElementFile2 -File 2 Root Element
     * @return boolean indicating whether merging and comparison was success
     */
    private boolean compareAndMergeRootParameters(Element rootElementFinal,Element mRootElementFile1,
                                                  Element mRootElementFile2)
    {
        boolean isSuccess=false;

        /* If either of final root element,root element from file1 or root element from file2 does not
         exist return false*/
        if(rootElementFinal==null ||( mRootElementFile1==null && mRootElementFile2==null))
        {
            return false;
        }

        //This list will contain the initial root parameters set by the user
        List<RootElementPOJO> initialRootList=mInitialParameterList.getInitialRootList();

        RootElementPOJO initialRootElement=null;
        String attributeName="";

        String attributeValue1=null;
        String attributeValue2=null;

        int modeComparison;

        /* If no attribute exists in the initial root attribute list return true since user does not
        want to add any of the attributes.
         */
        if(initialRootList==null || initialRootList.size()==0)
        {
            Log.d(TAG,"----[compareAndMergeRootParameters]:initialRootList.size()==0------");
            return true;
        }

        /*Loop till the last element in the initial root attribute list*/
        OUTER_LOOP : for(int counter=0;counter<initialRootList.size();counter++)
        {
            isSuccess=false;
            initialRootElement=initialRootList.get(counter);
            attributeName=initialRootElement.getElementName().trim();

            //attribute names contain @. Replace it with blank before comparing.
            if(attributeName!=null && !attributeName.equals("")) {
                attributeName = attributeName.replace(ComparisonConstants.DELIMINATOR_ATTRIBUTE, "");
            }

            //get the mode of comparison from the element
            modeComparison=initialRootElement.getModeOfComparison();

            /*If the attribute name is not null or blank from the initial list*/
            if(attributeName!=null && !attributeName.equals("")) {

                /*Get the attribute names from both the root elements*/
                attributeValue1 = mRootElementFile1.getAttributeValue("", attributeName);
                attributeValue2 = mRootElementFile2.getAttributeValue("", attributeName);

                /*If attribute name in either of the file is not present break the loop and signal
                * unsuccessful exit*/
                if((attributeValue1==null || attributeValue1.equals("")) &&
                        (attributeValue2==null || attributeValue2.equals("")))
                {
                    Log.d(TAG,"----attributeValue1== null && attributeValue2== null------");
                    isSuccess = false;
                    break OUTER_LOOP;
                }

            /*If mode of comparison are below ones then check both the list from files should be
            * non empty. Otherwise no comparison can be made*/
                if (modeComparison == ComparisonConstants.COMPARE_EQUAL
                        || modeComparison == ComparisonConstants.COMPARE_GREATER_FILE1
                        || modeComparison == ComparisonConstants.COMPARE_GREATER_FILE2
                        || modeComparison == ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1
                        || modeComparison == ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2) {

                    if(attributeValue1==null || attributeValue1.equals(""))
                    {
                        Log.d(TAG,"----missing attribute in attributeValue1------");
                        isSuccess = false;
                        break OUTER_LOOP;
                    }
                    else if(attributeValue2==null || attributeValue2.equals("")){

                        Log.d(TAG,"----missing attribute in attributeValue2------");
                        isSuccess = false;
                        break OUTER_LOOP;
                    }

                }


                switch (modeComparison) {

                    /*If mode of comparison is COMPARE_EQUAL then check the file priority and add
                    * the attribute on the basis of file priority*/
                    case ComparisonConstants.COMPARE_EQUAL:

                        if (compareAttributeValues(attributeValue1, attributeValue2,
                                ComparisonConstants.COMPARE_EQUAL)) {

                        /*If file1 has more priority then add the element in final list from file1 list*/
                            if (mFileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE1) {
                                setAttribute(attributeName, attributeValue1, rootElementFinal);
                            }
                            /*If file2 has more priority then add the element in final list from file2 list*/
                            else if (mFileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE2) {
                                setAttribute(attributeName, attributeValue2, rootElementFinal);
                            }
                            isSuccess = true;

                        }
                        else
                        {
                            Log.d(TAG,"----COMPARE_EQUAL break------+"+attributeName);
                            isSuccess = false;
                            break OUTER_LOOP;
                        }

                        break;

                    /*If attribute value is greater in file 1 then add it to final list else break the
                    * loop*/
                    case ComparisonConstants.COMPARE_GREATER_FILE1:

                        if (compareAttributeValues(attributeValue1, attributeValue2,
                                ComparisonConstants.COMPARE_GREATER_FILE1)) {

                            setAttribute(attributeName, attributeValue1, rootElementFinal);
                            isSuccess = true;
                        }
                        else
                        {
                            Log.d(TAG,"----COMPARE_GREATER_FILE1 break------"+attributeName);
                            isSuccess = false;
                            break OUTER_LOOP;
                        }

                        break;

                     /*If attribute value is greater in file 2 then add it to final list else break the
                    * loop*/
                    case ComparisonConstants.COMPARE_GREATER_FILE2:

                        if (compareAttributeValues(attributeValue1, attributeValue2,
                                ComparisonConstants.COMPARE_GREATER_FILE2)) {

                            setAttribute(attributeName, attributeValue2, rootElementFinal);
                            isSuccess = true;
                        }
                        else
                        {
                            Log.d(TAG,"----COMPARE_GREATER_FILE2 break------"+attributeName);
                            isSuccess = false;
                            break OUTER_LOOP;
                        }

                        break;

                     /*If attribute value is greater or equal in file 1 then add it to final list
                      else break the loop*/
                    case ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1:

                        if (compareAttributeValues(attributeValue1, attributeValue2,
                                ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1)) {

                            setAttribute(attributeName, attributeValue1, rootElementFinal);
                            isSuccess = true;
                        }
                        else
                        {
                            Log.d(TAG,"----COMPARE_GREATER_EQUAL_FILE1 break------"+attributeName);
                            isSuccess = false;
                            break OUTER_LOOP;
                        }

                        break;

                    /*If attribute value is greater or equal in file 2 then add it to final list
                      else break the loop*/
                    case ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2:

                        if (compareAttributeValues(attributeValue1, attributeValue2,
                                ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2)) {

                            setAttribute(attributeName, attributeValue2, rootElementFinal);
                            isSuccess = true;
                        }
                        else
                        {
                            Log.d(TAG,"----COMPARE_GREATER_EQUAL_FILE2 break------"+attributeName);
                            isSuccess = false;
                            break OUTER_LOOP;
                        }

                        break;

                    /*If attribute value exists in file 1 add it to the final list else break the
                    * loop*/

                    case ComparisonConstants.PICK_FROM_FILE1:


                        if (attributeValue1!=null && !attributeValue1.equals("")) {

                            setAttribute(attributeName, attributeValue1, rootElementFinal);
                            isSuccess = true;
                        }
                        else
                        {
                            Log.d(TAG,"----PICK_FROM_FILE1 break------"+attributeName);
                            isSuccess = false;
                            break OUTER_LOOP;
                        }

                        break;

                    /*If attribute value exists in file 2 add it to the final list else break the
                    * loop*/
                    case ComparisonConstants.PICK_FROM_FILE2:

                        if (attributeValue2!=null && !attributeValue2.equals("")) {

                            setAttribute(attributeName, attributeValue2, rootElementFinal);
                            isSuccess = true;
                        }
                        else
                        {
                            Log.d(TAG,"----PICK_FROM_FILE2 break------"+attributeName);
                            isSuccess = false;
                            break OUTER_LOOP;
                        }

                        break;

                    /*If the user has specified the NO_COMPARISON then add the attribute on the basis
                     * file priority */

                    case ComparisonConstants.NO_COMPARISON:

                        if((attributeValue1!=null && !attributeValue1.equals(""))&&
                                (attributeValue2!=null && !attributeValue2.equals("")))
                        {
                            if(mFileParameterPOJO.getFilePriority()==ComparisonConstants.PRIORITY_FILE1)
                            {
                                setAttribute(attributeName, attributeValue1, rootElementFinal);
                                isSuccess = true;

                            }
                            else if(mFileParameterPOJO.getFilePriority()==ComparisonConstants.PRIORITY_FILE2)
                            {
                                setAttribute(attributeName, attributeValue2, rootElementFinal);
                                isSuccess = true;
                            }
                        }
                        else if((attributeValue1!=null && !attributeValue1.equals("")))
                        {
                            setAttribute(attributeName, attributeValue1, rootElementFinal);
                            isSuccess = true;
                        }
                        else if((attributeValue2!=null && !attributeValue2.equals("")))
                        {
                            setAttribute(attributeName, attributeValue2, rootElementFinal);
                            isSuccess = true;
                        }
                        else
                        {
                            Log.d(TAG,"----NO_COMPARISON break both attributes null------"+attributeName);
                            isSuccess = false;
                        }

                        break;

                    default:
                        Log.d(TAG,"----Wrong Comparison mode for Root Attribute------"+attributeName);
                        isSuccess = false;
                        break OUTER_LOOP;
                }
            }
            else
            {
                Log.d(TAG,"----[compareAndMergeRootParameters]: attributeName==null && attributeName.equals");
                isSuccess=false;
                break OUTER_LOOP;
            }
        }

        return isSuccess;

    }



    /**This function will create an attribute and will append it to the element passed
     *
     * @param attributeName - Name of the attribute to be add
     * @param attributeValue-Value of the attribute to be add
     * @param element -Element on which attribute has to be set
     */
    private void setAttribute(String attributeName, String attributeValue, Element element)
    {
        element.setAttribute("",attributeName,attributeValue);

    }


    /**This function will write the Nodes of both xml in final xml file on the basis of the rules specified
     * by the user. It will call other functions to get the required nodes from both the xml files,
     * compare the attributes to create a final list of nodes and then adding the nodes to the final xml
     * file.
     *
     *
     * @return boolean variable indicating whether comparing and merging was successful or not
     */
     private boolean writeNodesToXml()
    {
        boolean isWriteSuccess=false;

        //Get all initial Node attributes set by the user
        List<NodeElementPOJO> initialNodes = mInitialParameterList.getInitialNodeAttributeList();

        /*If user has not specified any set of rules return the function with false so as not to write
        * the final xml file.*/
        if(initialNodes==null || initialNodes.size()==0)
        {
            return false;
        }

        NodeElementPOJO nodeElementPOJO=null;


        /*Iterate the loop over the initial node list consisting of rules and node hierarchy*/
        OUTER_LOOP:for (int count=0;count<initialNodes.size();count++) {

            nodeElementPOJO = initialNodes.get(count);

            /*This represents the name of key to be used*/
            mKeyChildName=nodeElementPOJO.getKeyChildName();

            /*This represents the value of key to be used*/
            mValueChildName=nodeElementPOJO.getValueChildName();

            /*This represents the complete element hierarchy not including the root*/
            mElementPath=nodeElementPOJO.getElementPath();

            /*This represents the rule to be applied on elements*/
            mModeOfComparison=nodeElementPOJO.getModeOfComparison();


            mIsSuccess =false;
            mIndex=0;
            mNodeToBeAdded=null;
            mParentNode=null;
            mHierarchy=null;

           /*Element path consists of hierarchy separated by the deliminator. split and store
            * it in this variable*/
            mHierarchy=mElementPath.split(ComparisonConstants.ABSOLUTE_PATH);

            //search and add the elements from root element 1
            searchAndAddKeyValueNode(mNodeListFile1, mRootElementFile1);

            //If element exists in root element 1, search elements in root element 2.

            if(mIsSuccess && mNodeListFile1!=null && mNodeListFile1.size()>0)
            {
                mIsSuccess =false;
                mNodeToBeAdded=null;
                mIndex=0;
                mParentNode=null;

                searchAndAddKeyValueNode(mNodeListFile2, mRootElementFile2);

                /* If element exists in both the files then compare and merge the list to get
                    a final list*/


                if(mIsSuccess && mNodeListFile2!=null && mNodeListFile2.size()>0)
                {
                    mIsSuccess =false;
                    mNodeToBeAdded=null;
                    mIndex=0;
                    mParentNode=null;

                    compareAndMergeKeyNode(mNodeListFile1,mNodeListFile2,mNodeListFinal);

                    /*After comparison if final list consists of elements then add the
                    * elements to the final Root element*/
                    if(mIsSuccess && mNodeListFinal!=null)
                    {
                        mIsSuccess =false;
                        mNodeToBeAdded=null;
                        mIndex=0;
                        mParentNode=null;

                        if(mNodeListFinal.size()>0)
                             addNodeElementsToFinalFile(mNodeListFinal);

                        else
                            mIsSuccess=true;

                        /*If any of the element was added then write the file*/
                        if(mIsSuccess)
                            isWriteSuccess=true;
                    }
                    else {

                        isWriteSuccess=false;
                        Log.d(TAG, "writeNodesToXml:[KEY_VALUE]:mIsSuccess && mNodeListFinal!=null && mNodeListFinal.size()>0-----");
                        break OUTER_LOOP;
                    }
                }
                else
                {
                    isWriteSuccess=false;
                    Log.d(TAG, "writeNodesToXml:[KEY_VALUE]:mIsSuccess && mNodeListFile2!=null && mNodeListFile2.size()>0-----");
                    break OUTER_LOOP;
                }
            }
            else
            {
                isWriteSuccess=false;
                Log.d(TAG, "writeNodesToXml:[KEY_VALUE]:mIsSuccess && mNodeListFile1!=null && mNodeListFile1.size()>0-----");
                break OUTER_LOOP;

            }


            mHierarchy=null;
            mIndex=0;
            mParentNode=null;
            mParentElement=null;
            mIsSuccess=false;
            mNodeListFile1.clear();
            mNodeListFile2.clear();
            mNodeListFinal.clear();

        }
        return isWriteSuccess;


    }



    /**
     * This function will take the final node element list and final root element. It will iterate over the
     * List and will add the elements to the final root element.
     *
      * @param nodeElementPOJOList - Final List of node elements
     */
     private void addNodeElementsToFinalFile(List<NodeElementPOJO> nodeElementPOJOList)
    {
        String elementName=nodeElementPOJOList.get(0).getElementPath();
        NodeElementPOJO nodeElement=null;

        if(elementName!=null && elementName.length()>0)
        {
            //Get the element path and split it.
            String []splitPath=elementName.split(ComparisonConstants.ABSOLUTE_PATH);
              mHierarchy=splitPath;
             getFinalKeyValueElement(nodeElementPOJOList, mRootElementFinal);

        }

    }

    /* */

    /**
     *This function is used for appending key value pair elements to the final element at appropriate
     * hierarchy
     *
     * @param nodeElementPOJOList - List containing key value pair elements to be added
     * @param parentNodeElement - Parent node in which elements will be searched from the list and
     *                          added accordingly
     */
    private void getFinalKeyValueElement(List<NodeElementPOJO> nodeElementPOJOList, Element parentNodeElement)
    {
        for (int i=0;i<parentNodeElement.getChildCount();i++)
        {
            Element childElement=parentNodeElement.getElement(i);

            if( childElement!=null && (mHierarchy[mIndex].equals(childElement.getName())))
            {
                if(mIndex==0)
                {
                    mParentElement=childElement;
                }
                if(mIndex==mHierarchy.length-1)
                {
                    NodeElementPOJO nodeElementPOJO=null;
                    boolean isElementFound=false;

                    INNER_LOOP:for(int j=0;j<nodeElementPOJOList.size();j++) {

                         nodeElementPOJO = nodeElementPOJOList.get(j);

                        if (nodeElementPOJO.isToBeAdded()) {
                            if ((childElement.getElement("", nodeElementPOJO.getKeyChildName()) != null) &&
                                    (childElement.getElement("", nodeElementPOJO.getValueChildName()) != null)) {
                                mainParent=parentNodeElement;
                                String keyValue = childElement.getElement("", nodeElementPOJO.getKeyChildName()).getText(0);
                                String valueValue = childElement.getElement("", nodeElementPOJO.getValueChildName()).getText(0);

                                if ((keyValue != null && keyValue.length() > 0) && (valueValue != null && valueValue.length() > 0)) {
                                    if (keyValue.equals(nodeElementPOJO.getKeyChildValue())) {

                                        if(valueValue!=(nodeElementPOJO.getValueChildValue()))
                                        {

                                            parentNodeElement.removeChild(i);
                                            //parentNodeElement.addChild(Node.ELEMENT,nodeElementPOJO.getNodeItself());
                                            parentNodeElement.addChild(i,Node.ELEMENT,nodeElementPOJO.getNodeItself());
                                            nodeElementPOJO.setToBeAdded(false);
                                            isElementFound=true;
                                            mIsSuccess = true;
                                            break INNER_LOOP;

                                        }
                                        else
                                        {
                                            nodeElementPOJO.setToBeAdded(false);
                                            isElementFound=true;
                                            mIsSuccess = true;
                                            break INNER_LOOP;
                                        }
                                    }
                                }

                            }
                        }
                    }
                    if(isElementFound)
                    nodeElementPOJOList.remove(nodeElementPOJO);

                }
                else
                {
                    mIndex++;
                    getFinalKeyValueElement(nodeElementPOJOList, childElement);
                }
            }

        }

        if(nodeElementPOJOList.size()>0)
        {
            if(mainParent!=null)
            {
               for(NodeElementPOJO node:nodeElementPOJOList)
               {
                   mainParent.addChild(Node.ELEMENT,node.getNodeItself());
                   mIsSuccess = true;
               }
            }
            mainParent=null;
            nodeElementPOJOList.clear();
        }
    }




    /**This function will search for a key value pair hierarchy in given root element. If one of
     * element is found then add it to the final list and set the global variable mIsSuccess true
     *
     * @param nodeElementList - The List that will contain the searched elements
     * @param rootElement  - The root element where search has to be performed
     */
     private void searchAndAddKeyValueNode(List<NodeElementPOJO> nodeElementList, Element rootElement)
    {
        /*If there is no element in the hierarchy then set the variable to signal no element
         was found */
        if(mHierarchy.length==0)
        {
            mIsSuccess =false;
        }
        /*If the length of hierarchy is equal to one then search for the element directly in root
        * element. If found then add it to the node element list*/
        else if(mHierarchy.length==1)
        {
            for(int i=0;i<rootElement.getChildCount();i++)
            {
                Element childElement=rootElement.getElement(i);

                /*If the name of the child element is same as that in mHierarchy */
                if( childElement!=null && (mHierarchy[0].equals(childElement.getName())))
                {
                    /*Get the key and values from the element. If found then get the values of both.
                    * */

                     if((childElement.getElement("",mKeyChildName)!=null)&&
                            (childElement.getElement("",mValueChildName)!=null))
                    {
                        String keyValue=childElement.getElement("",mKeyChildName).getText(0);
                        String valueValue=childElement.getElement("",mValueChildName).getText(0);

                        /*If values are present then add it to the final list. change the value of
                        * mIsSuccess to true to signal at least one search is found*/
                        if((keyValue!=null && keyValue.length()>0) && (valueValue!=null && valueValue.length()>0))
                        {
                            mIsSuccess =true;
                            nodeElementList.add(new NodeElementPOJO(mKeyChildName,keyValue,mValueChildName,valueValue,
                                    mElementPath,childElement,mModeOfComparison,true,null,childElement));
                        }
                    }
                }
            }

        }
        /*If the length of element hierarchy is greater than 1*/
        else {

            //Iterate the loop over the root element child
            for(int j=0;j<rootElement.getChildCount();j++)
            {
                Element childElement=rootElement.getElement(j);

                //If the child element exists and its name is same as that of name from hierarchy array
                if( childElement!=null && (mHierarchy[mIndex].equals(childElement.getName())))
                {
                    /*If the child element found is the first element then it will be the parent node.
                    * Store the element in a variable*/
                    if(mIndex==0 )
                    {
                        mParentNode=childElement;
                    }
                    /*If the element is second last element then store element as reference node*/
                    if(mIndex==mHierarchy.length-2)
                    {
                        mNodeToBeAdded=childElement;
                    }
                    /*If we have reached to the last element then check key and values. If they exists
                    * add them to the list.*/
                    if(mIndex==mHierarchy.length-1)
                    {
                        if((childElement.getElement("",mKeyChildName)!=null)&&
                                (childElement.getElement("",mValueChildName)!=null))
                        {
                            String keyValue=childElement.getElement("",mKeyChildName).getText(0);
                            String valueValue=childElement.getElement("",mValueChildName).getText(0);

                            if((keyValue!=null && keyValue.length()>0) && (valueValue!=null && valueValue.length()>0))
                            {
                                mIsSuccess =true;
                                nodeElementList.add(new NodeElementPOJO(mKeyChildName,keyValue,mValueChildName,valueValue,
                                        mElementPath,mNodeToBeAdded,mModeOfComparison,true,mParentNode,childElement));
                            }
                        }

                    }
                    /*If we have not reached to the last element iterate the function*/
                    else
                    {
                        mIndex++;
                        searchAndAddKeyValueNode(nodeElementList, childElement);
                    }
                }

            }

        }

    }

    /**
     * This function will be used for comparing the node list from both the files on the basis of mode
     * of comparison and then adding the final element to the final list of elements
     *
      * @param nodeElementList1 - List containing elements from File 1
     * @param nodeElementList2  - List containing elements from File 2
     * @param nodeElementListFinal - List that will hold final compared elements
     * @return boolean whether any of the element was compared and added
     */
     private boolean compareAndMergeKeyNode(List<NodeElementPOJO> nodeElementList1,
                                           List<NodeElementPOJO> nodeElementList2,
                                           List<NodeElementPOJO> nodeElementListFinal
    )

    {
        mIsSuccess=false;

        List<NodeElementPOJO> auxillaryList1 = new ArrayList<NodeElementPOJO>();
        NodeElementPOJO auxInnerNode = null;
        int mode=0;

        /*If the both the list files are empty return the function*/
        if(nodeElementList1.size()==0 || nodeElementList2.size()==0)
        {
            mIsSuccess=false;
            return mIsSuccess;
        }

        /*Iterate over both of the lists*/
        OUTER_LOOP:for(NodeElementPOJO nodeListElement1 : nodeElementList1)
        {

            boolean elementFound = false;
            INNER_LOOP:for (NodeElementPOJO nodeListElement2 : nodeElementList2)
            {

                /*If the element path,key child name, key child value,mode of comparison,value child
                * name are same then we have found the element in same hierarchy having same key name
                * and value. Apply rules on the basis of mode.*/
                if(((nodeListElement1.getElementPath()).equals(nodeListElement2.getElementPath()))
                        &&((nodeListElement1.getKeyChildName()).equalsIgnoreCase(nodeListElement2.getKeyChildName()))
                        &&((nodeListElement1.getKeyChildValue()).equalsIgnoreCase(nodeListElement2.getKeyChildValue()))
                        &&((nodeListElement1.getModeOfComparison())==(nodeListElement2.getModeOfComparison()))
                        &&((nodeListElement1.getValueChildName()).equalsIgnoreCase(nodeListElement2.getValueChildName()))
                        )
                {

                    mode=nodeListElement1.getModeOfComparison();

                    /*If the element values are also same then don't add it the final list. The elements
                    * are already added in the final xml that were to be added on the basis of file priority*/
                    if(nodeListElement1.getValueChildValue().equals(nodeListElement2.getValueChildValue()))
                    {
                        mIsSuccess = true;
                    }
                    /*If the values are different then check the file priority. Then select the element
                    * from other file if the mode selected by the user is different from file mode*/
                    else
                    {
                        switch (mFileParameterPOJO.getFilePriority())
                        {
                            case ComparisonConstants.PRIORITY_FILE1:

                                if(mode == ComparisonConstants.PICK_FROM_FILE2)
                                {
                                    nodeElementListFinal.add(nodeListElement2);
                                }

                                mIsSuccess=true;
                                break;

                            case ComparisonConstants.PRIORITY_FILE2:

                                if(mode == ComparisonConstants.PICK_FROM_FILE1)
                                {
                                    nodeElementListFinal.add(nodeListElement1);
                                }

                                mIsSuccess=true;
                                break;

                            default:

                                mIsSuccess=false;
                                Log.d(TAG,"[compareAndMergeKeyNode]:default case.");
                                return mIsSuccess;
                        }
                    }

                    elementFound = true;
                    auxInnerNode = nodeListElement2;
                    break INNER_LOOP;
                }
            }

            if(elementFound) {
                auxillaryList1.add(nodeListElement1);
                nodeElementList2.remove(auxInnerNode);
            }


        }
       /*This piece of code will add the uncommon elements from both the lists to the final list*/
        nodeElementList1.removeAll(auxillaryList1);
        nodeElementListFinal.addAll(nodeElementList1);
        nodeElementListFinal.addAll(nodeElementList2);
        mIsSuccess=true;


        return mIsSuccess;

    }



    /**
     * *This function will compare the two attribute values on the basis of comparison type and
    * will the return the boolean result
    *
      * @param attrValue1 - Attribute Value 1
     * @param attrValue2 - Attribute Value 2
     * @param comparisonType - Comparison Type
     * @return - Boolean telling whether comparison was successful pr not
     */
    private boolean compareAttributeValues(String attrValue1,String attrValue2,int comparisonType)
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

                if(((attrValue1.trim()).compareTo(attrValue2.trim()))>0)
                {
                    result=true;
                }

                break;

            /*Compare attribute Values for value greater in File2 */
            case ComparisonConstants.COMPARE_GREATER_FILE2:

                if(((attrValue2.trim()).compareTo(attrValue1.trim()))>0)
                {
                    result=true;
                }

                break;

            /*Compare attribute Values for value greater or equal in File1 */
            case ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1:

                if(((attrValue1.trim()).compareTo(attrValue2.trim()))>=0)
                {
                    result=true;
                }

                break;

            /*Compare attribute Values for value greater or equal in File2 */
            case ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2:

                if(((attrValue2.trim()).compareTo(attrValue1.trim()))>=0)
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


}
