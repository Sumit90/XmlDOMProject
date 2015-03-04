package com.practise.xmlparse.xmldom;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import org.kxml2.kdom.Element;

import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Button mParseBtn;

    private InputStream mInputStreamFile1;
    private InputStream mInputStreamFile2;

    private XmlParser mParseFile1;
    private XmlParser mParseFile2;

    private InitialParameterList mInitialParameterList;

    private FileParameterPOJO mFileParameterPOJO;

    private final String TAG="MYLOGS";
    private boolean mStatusMerging =false;

    private WriteXmlFile mWriteFile;
    private Element mRootElementFile1;
    private Element mRootElementFile2;
    private Element mRootElementFinal;
    private String[] mHierarchy;
    private int mIndex=0;
    private Element mNodeToBeAdded;
    boolean mIsSuccess =false;
    private String mKeyChildName="";
    private String mValueChildName="";
    private String mElementType="";
    private int mModeOfComparison=0;
    private String mElementPath="";
    private List<NodeElementPOJO> mNodeListFile1;
    private List<NodeElementPOJO> mNodeListFile2;
    private List<NodeElementPOJO> mNodeListFinal;
    private Element mParentNode=null;
    private Element mParentElement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParseBtn =(Button)findViewById(R.id.parse_button);


        mParseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (init()) {


                        //get the root elements of the XML files
                        mRootElementFile1 = mParseFile1.parseRootElement();
                        mRootElementFile2 = mParseFile2.parseRootElement();

                        //If no root elements exists in either of the file
                        if (mRootElementFile1 != null && mRootElementFile2 != null) {

                            //If the name of root elements is same in both the files
                            if(((mRootElementFile1.getName()).equals(mRootElementFile2.getName())))
                            {
                                mWriteFile = new WriteXmlFile(mFileParameterPOJO);

                                if(mWriteFile.createDocRootElement(mRootElementFile1.getName()))
                                {
                                    mRootElementFinal = mWriteFile.getDocRootElement();

                                    if(compareAndMergeRootParameters(mRootElementFinal
                                            ,mRootElementFile1,mRootElementFile2))
                                    {
                                        Log.d(TAG, "---------- check writeNodesToXml()-------------");

                                        if (writeNodesToXml(mRootElementFile1, mRootElementFile2, mRootElementFinal))
                                        {
                                            mWriteFile.WriteXml();
                                        } else
                                        {
                                            Log.d(TAG, "Error while adding nodes writeNodesToXml");
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
                } catch (Exception e) {

                    Log.d(TAG, "inside Exception");
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

              mWriteFile=null;
              mRootElementFile1=null;
              mRootElementFile2=null;
              mRootElementFinal=null;
              mHierarchy=null;
              mIndex=0;
              mNodeToBeAdded=null;
              mIsSuccess =false;
              mKeyChildName="";
              mValueChildName="";
              mElementType="";
              mModeOfComparison=0;
              mElementPath="";
              mNodeListFile1=null;
              mNodeListFile2=null;
              mNodeListFinal=null;
              mParentNode=null;
              mParentElement=null;


          //----------------get the input streams of file here from user----------------------------
            mInputStreamFile1 = getResources().getAssets().open("LogCodes.xml");
            mInputStreamFile2 = getResources().getAssets().open("LogCodes1.xml");
          //----------------------------------------------------------------------------------------


            mParseFile1 = new XmlParser(mInputStreamFile1);
            mParseFile2 =  new XmlParser(mInputStreamFile2);

            mNodeListFile1=new ArrayList<NodeElementPOJO>();
            mNodeListFile2=new ArrayList<NodeElementPOJO>();
            mNodeListFinal=new ArrayList<NodeElementPOJO>();

            mFileParameterPOJO =new FileParameterPOJO(mInputStreamFile1, mInputStreamFile2
                    ,ComparisonConstants.PRIORITY_FILE1, "LogCodes1.xml","/sdcard/FTA/Log/",true);

            mInitialParameterList =new InitialParameterList();

            addInitialRootParameters("@version",ComparisonConstants.COMPARE_EQUAL);
            addInitialRootParameters("@name",ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1);


            /*addInitialNodeParameters("Id", "", "Name", "", "Class/Myclass/Employee",
                    null, ComparisonConstants.KEY_VALUE, ComparisonConstants.COMPARE_GREATER_FILE2,true);

            addInitialNodeParameters("Roll", "", "Grade", "", "Student",
                    null, ComparisonConstants.KEY_VALUE, ComparisonConstants.COMPARE_GREATER_FILE2,true);*/

            addInitialNodeParameters("name", "", "value", "", "LTE/Lte_logcode",
                    null, ComparisonConstants.KEY_VALUE, ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2,true);

            addInitialNodeParameters("name", "", "value", "", "WCDMA",
                    null, ComparisonConstants.NODE, ComparisonConstants.NO_COMPARISON,true);

            addInitialNodeParameters("name", "", "value", "", "GSM",
                    null, ComparisonConstants.NODE, ComparisonConstants.NO_COMPARISON,true);

            addInitialNodeParameters("name", "", "value", "", "CDMA",
                    null, ComparisonConstants.NODE, ComparisonConstants.NO_COMPARISON,true);

            addInitialNodeParameters("name", "", "value", "", "EVDO",
                    null, ComparisonConstants.NODE, ComparisonConstants.NO_COMPARISON,true);


            /*addInitialNodeParameters("Roll", "", "Name", "", "Student",
                    null, ComparisonConstants.NODE, ComparisonConstants.PICK_FROM_FILE1,true);*/


            printRootList(mInitialParameterList.getInitialRootList());

            isInitSuccess=true;
        }

        catch(FileNotFoundException ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"inside init FileNotFoundException");
            ex.printStackTrace();

        }
        catch (XmlPullParserException ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"inside init XmlPullParserException");
            ex.printStackTrace();
        }
        catch(IOException ex)
        {
            isInitSuccess=false;
            Log.d(TAG,"inside init IOException");
            ex.printStackTrace();
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
            mInitialParameterList.addInitialRoot(new RootElementPOJO(attributeName, mode));
        }
//--------------------------------------------------------------------------------------------------
//This function is used to add Initial Node attributes to be used for comparing and extracting
void addInitialNodeParameters(String keyChildName,String keyChildValue,String valueChildName,
                              String valueChildValue, String elementPath,
                              Element parentReferenceNode,String nodeType, int modeOfComparison,
                              boolean isToBeAdded
                               )
{
    mInitialParameterList.addInitialNodeAttribute(new NodeElementPOJO(keyChildName, keyChildValue,
            valueChildName, valueChildValue, elementPath, parentReferenceNode, nodeType,
            modeOfComparison,isToBeAdded,null));

}



private boolean compareAndMergeRootParameters(Element rootElementFinal,Element mRootElementFile1,
                                                                            Element mRootElementFile2)
{
    boolean isSuccess=false;

    if(rootElementFinal==null ||( mRootElementFile1==null && mRootElementFile2==null))
    {
        return isSuccess;
    }



    List<RootElementPOJO> initialRootList=mInitialParameterList.getInitialRootList();
    RootElementPOJO initialRootElement=null;
    String attributeName="";

    String attributeValue1=null;
    String attributeValue2=null;

    int modeComparison;

    if(initialRootList==null || initialRootList.size()==0)
    {
        Log.d(TAG,"----[compareAndMergeRootParameters]:initialRootList.size()==0------");
        return isSuccess=true;
    }

    OUTER_LOOP : for(int counter=0;counter<initialRootList.size();counter++)
    {
        isSuccess=false;
        initialRootElement=initialRootList.get(counter);
        attributeName=initialRootElement.getElementName();
        attributeName=attributeName.replace(ComparisonConstants.DELIMINATOR_ATTRIBUTE,"");

        modeComparison=initialRootElement.getModeOfComparison();

        if(attributeName!=null && !attributeName.equals("")) {
            attributeValue1 = mRootElementFile1.getAttributeValue("", attributeName);
            attributeValue2 = mRootElementFile2.getAttributeValue("", attributeName);

            if((attributeValue1==null || attributeValue1.equals("")) &&
                                            (attributeValue2==null || attributeValue2.equals("")))
            {
                Log.d(TAG,"----attributeValue1== null && attributeValue2== null------");
                isSuccess = false;
                break OUTER_LOOP;
            }


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


                case ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2:

                    if (compareAttributeValues(attributeValue1, attributeValue2,
                            ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2)) {

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


                case ComparisonConstants.PICK_FROM_FILE1:


                    if (attributeValue1!=null || !attributeValue1.equals("")) {

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


                case ComparisonConstants.PICK_FROM_FILE2:

                    if (attributeValue2!=null || !attributeValue2.equals("")) {

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


                case ComparisonConstants.NO_COMPARISON:

                    if((attributeValue1!=null || !attributeValue1.equals(""))&&
                                                (attributeValue2!=null || !attributeValue2.equals("")))
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
                    else if((attributeValue1!=null || !attributeValue1.equals("")))
                    {
                        setAttribute(attributeName, attributeValue1, rootElementFinal);
                        isSuccess = true;
                    }
                    else if((attributeValue2!=null || !attributeValue2.equals("")))
                    {
                        setAttribute(attributeName, attributeValue2, rootElementFinal);
                        isSuccess = true;
                    }


                    break;

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

    //This function will create an attribute and will append it to the element passed
    private void setAttribute(String attributeName, String attributeValue, Element element)
    {
        element.setAttribute("",attributeName,attributeValue);
    }


    private boolean writeNodesToXml(Element rootElementFile1,Element rootElementFile2,Element rootElementFinal)
    {
        boolean isWriteSuccess=false;


        //Get all initial Node attributes set by the user
        List<NodeElementPOJO> initialNodes = mInitialParameterList.getInitialNodeAttributeList();

        if(initialNodes==null || initialNodes.size()==0)
        {
            return isWriteSuccess;
        }

        NodeElementPOJO nodeElementPOJO=null;



        OUTER_LOOP:for (int count=0;count<initialNodes.size();count++) {

            nodeElementPOJO = initialNodes.get(count);

            mElementType=nodeElementPOJO.getElementType();
            mKeyChildName=nodeElementPOJO.getKeyChildName();
            mValueChildName=nodeElementPOJO.getValueChildName();
            mElementPath=nodeElementPOJO.getElementPath();
            mModeOfComparison=nodeElementPOJO.getModeOfComparison();
            mIsSuccess =false;
            mIndex=0;
            mNodeToBeAdded=null;
            mParentNode=null;
            if(!(mElementType.equals(ComparisonConstants.NODE)||
                    mElementType.equals(ComparisonConstants.KEY_VALUE)
            ))
            {
                Log.d(TAG,"[writeNodesToXml]: break loop---- element type wrong main-----");
                break OUTER_LOOP;
            }


            switch (mElementType)
            {
                case ComparisonConstants.NODE:

                    mHierarchy=mElementPath.split(ComparisonConstants.ABSOLUTE_PATH);


                    if(mModeOfComparison==ComparisonConstants.NO_COMPARISON)
                    {
                        if(mFileParameterPOJO.getFilePriority()==ComparisonConstants.PRIORITY_FILE1)
                        {
                            searchAndMergeNodeElements(rootElementFile1, rootElementFinal);


                            if(mIsSuccess){

                                isWriteSuccess=true;
                            }
                            else
                            {
                                isWriteSuccess=false;
                                Log.d(TAG,"writeNodesToXml:[NO_COMPARISON][PRIORITY_FILE1]: break loop---------");
                                break OUTER_LOOP;
                            }

                        }else {

                            searchAndMergeNodeElements(rootElementFile2, rootElementFinal);

                            if(mIsSuccess){

                                isWriteSuccess=true;
                            }
                            else
                            {
                                isWriteSuccess=false;
                                Log.d(TAG,"writeNodesToXml:[NO_COMPARISON][PRIORITY_FILE2]: break loop---------");
                                break OUTER_LOOP;
                            }


                        }

                    }
                    else if(mModeOfComparison==ComparisonConstants.PICK_FROM_FILE1)
                    {
                        searchAndMergeNodeElements(rootElementFile1, rootElementFinal);

                        if(mIsSuccess){

                            isWriteSuccess=true;
                        }
                        else
                        {
                            isWriteSuccess=false;
                            Log.d(TAG,"writeNodesToXml:[PICK_FROM_FILE1]: break loop---------");
                            break OUTER_LOOP;
                        }



                    }
                    else if(mModeOfComparison==ComparisonConstants.PICK_FROM_FILE2)
                    {
                        searchAndMergeNodeElements(rootElementFile2, rootElementFinal);

                        if(mIsSuccess){

                            isWriteSuccess=true;
                        }
                        else
                        {
                            isWriteSuccess=false;
                            Log.d(TAG,"writeNodesToXml:[PICK_FROM_FILE2]: break loop---------");
                            break OUTER_LOOP;
                        }


                    }else{

                        isWriteSuccess=false;
                        Log.d(TAG,"writeNodesToXml:[NODE]: break loop---- element type wrong NODE-----");
                        break OUTER_LOOP;

                    }

                    break;


                case ComparisonConstants.KEY_VALUE:
                    mHierarchy=mElementPath.split(ComparisonConstants.ABSOLUTE_PATH);

                   // searchKeyValueNode(mNodeListFile1, mRootElementFile1);
                    searchKeyValueNode(mNodeListFile1, mRootElementFile1);

                    if(mIsSuccess && mNodeListFile1!=null && mNodeListFile1.size()>0)
                    {
                        mIsSuccess =false;
                       // searchKeyValueNode(mNodeListFile2, mRootElementFile2);

                        mNodeToBeAdded=null;
                        mIndex=0;
                        mParentNode=null;
                        searchKeyValueNode(mNodeListFile2, mRootElementFile2);

                        if(mIsSuccess && mNodeListFile2!=null && mNodeListFile2.size()>0)
                        {
                            mIsSuccess =false;
                            mNodeToBeAdded=null;
                            mIndex=0;
                            mParentNode=null;

                            compareAndMergeKeyNode(mNodeListFile1,mNodeListFile2,mNodeListFinal);

                            if(mIsSuccess && mNodeListFinal!=null && mNodeListFinal.size()>0)
                            {
                                mIsSuccess =false;
                                mNodeToBeAdded=null;
                                mIndex=0;
                                mParentNode=null;


                                addNodeElementsToFinalFile(mNodeListFinal,mRootElementFinal);

                                if(mIsSuccess)
                                isWriteSuccess=true;

                            }
                            else {

                                isWriteSuccess=false;
                                Log.d(TAG, "writeNodesToXml:[NODE]:mIsSuccess && mNodeListFile1!=null && mNodeListFile1.size()>0-----");
                                break OUTER_LOOP;
                            }


                        }
                        else
                        {
                            isWriteSuccess=false;
                            Log.d(TAG, "writeNodesToXml:[NODE]:mIsSuccess && mNodeListFile2!=null && mNodeListFile2.size()>0-----");
                            break OUTER_LOOP;
                        }

                    }
                    else
                    {
                        isWriteSuccess=false;
                        Log.d(TAG, "writeNodesToXml:[NODE]:mIsSuccess && mNodeListFile1!=null && mNodeListFile1.size()>0-----");
                        break OUTER_LOOP;

                    }



                    break;
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


    private void addNodeElementsToFinalFile(List<NodeElementPOJO> nodeElementPOJOList,Element rootElementFinal)
    {
        String elementName=nodeElementPOJOList.get(0).getElementPath();
        NodeElementPOJO nodeElement=null;


        if(elementName!=null && elementName.length()>0)
        {
            String []splitPath=elementName.split(ComparisonConstants.ABSOLUTE_PATH);

            if(splitPath.length==1)
            {
                for(int i=0;i<nodeElementPOJOList.size();i++)
                {
                    nodeElement=nodeElementPOJOList.get(i);
                    rootElementFinal.addChild(Node.ELEMENT,nodeElement.getReferenceNode());
                    mIsSuccess=true;
                }

            }
            else if(splitPath.length==2)
            {
                Element parent=nodeElementPOJOList.get(0).getParentNode();
                Element inner_element=null;
                Outer_loop:for(int i=0;i<parent.getChildCount();i++)
                {
                    inner_element=parent.getElement(i);
                    if(inner_element!=null && inner_element.getName().equals(mHierarchy[1]))
                    {
                        for(int j=0;j<nodeElementPOJOList.size();j++)
                        {
                            NodeElementPOJO element=nodeElementPOJOList.get(j);

                            Element keyNameElement=inner_element.getElement("",element.getKeyChildName());
                            Element valueNameElement=inner_element.getElement("",element.getValueChildName());

                            if(keyNameElement!=null && valueNameElement!=null)
                            {
                                if((keyNameElement.getText(0)).equals(element.getKeyChildValue())) {

                                    if (!element.isToBeAdded()) {
                                        parent.removeChild(i);
                                    }
                                    mIsSuccess=true;
                                    continue Outer_loop;

                                }

                            }

                        }

                    }
                }

                rootElementFinal.addChild(Node.ELEMENT,parent);

            }
            else
            {
                Element parent=nodeElementPOJOList.get(0).getParentNode();
                mHierarchy=splitPath;
                appendNodeKeyValue(nodeElementPOJOList,parent.getParent());

                if(mIsSuccess && mParentElement!=null)
                {
                    rootElementFinal.addChild(Node.ELEMENT,mParentElement);
                }

            }




        }



    }

    private void appendNodeKeyValue(List<NodeElementPOJO> nodeElementPOJOList,Node parentNodeElement)
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
                    for(int j=0;j<nodeElementPOJOList.size();j++)
                    {
                        NodeElementPOJO nodeElementPOJO=nodeElementPOJOList.get(j);

                        if((childElement.getElement("",nodeElementPOJO.getKeyChildName())!=null)&&
                                (childElement.getElement("",nodeElementPOJO.getValueChildName())!=null))
                        {
                            String keyValue=childElement.getElement("",nodeElementPOJO.getKeyChildName()).getText(0);
                            String valueValue=childElement.getElement("",nodeElementPOJO.getValueChildName()).getText(0);

                            if((keyValue!=null && keyValue.length()>0) && (valueValue!=null && valueValue.length()>0))
                            {
                                if(keyValue.equals(nodeElementPOJO.getKeyChildValue())) {

                                    if (!nodeElementPOJO.isToBeAdded()) {
                                        parentNodeElement.removeChild(i);
                                    }
                                    mIsSuccess=true;

                                }
                            }

                        }

                    }


                }
                else
                {
                    mIndex++;
                    appendNodeKeyValue(nodeElementPOJOList, childElement);
                }




            }
        }
    }

    private void searchAndMergeNodeElements(Element rootElement, Element finalElement)
    {

        for(int j=0;j<rootElement.getChildCount();j++)
        {
            Element childElement=rootElement.getElement(j);

            if( childElement!=null && (mHierarchy[mIndex].equals(childElement.getName())))
            {
                if(mIndex==0)
                {
                    mNodeToBeAdded=childElement;
                }
                if(mIndex==mHierarchy.length-1)
                {
                    mIsSuccess =true;
                    finalElement.addChild(org.kxml2.kdom.Node.ELEMENT,mNodeToBeAdded);
                    mNodeToBeAdded=null;
                    mIndex=0;

                }else
                {
                      mIndex++;
                     searchAndMergeNodeElements(childElement, finalElement);
                }
            }

        }


    }

    private void searchKeyValueNode(List<NodeElementPOJO> nodeElementList, Element rootElement)
    {
        if(mHierarchy.length==0)
        {
            mIsSuccess =false;
        }
        else if(mHierarchy.length==1)
        {
            for(int i=0;i<rootElement.getChildCount();i++)
            {
                Element childElement=rootElement.getElement(i);

                if( childElement!=null && (mHierarchy[0].equals(childElement.getName())))
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
                                    mElementPath,childElement,mElementType,mModeOfComparison,true,null));
                        }


                    }
                }

            }

        }
        else {

            for(int j=0;j<rootElement.getChildCount();j++)
            {
                Element childElement=rootElement.getElement(j);

                if( childElement!=null && (mHierarchy[mIndex].equals(childElement.getName())))
                {
                    if(mIndex==0 )
                    {
                        mParentNode=childElement;
                    }
                    if(mIndex==mHierarchy.length-2)
                    {
                        mNodeToBeAdded=childElement;
                    }
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
                                            mElementPath,mNodeToBeAdded,mElementType,mModeOfComparison,true,mParentNode));
                                }
                            }

                    }else
                    {
                        mIndex++;
                        searchKeyValueNode(nodeElementList, childElement);
                    }
                }

            }

        }

    }



    private boolean compareAndMergeKeyNode(List<NodeElementPOJO> nodeElementList1,
                                        List<NodeElementPOJO> nodeElementList2,
                                        List<NodeElementPOJO> nodeElementListFinal
                                        )

    {
        mIsSuccess=false;

        NodeElementPOJO nodeListElement1=null;
        NodeElementPOJO nodeListElement2=null;
        int mode=0;

        if(nodeElementList1.size()==0 || nodeElementList2.size()==0)
        {
             return mIsSuccess;
        }

        for(int l_i=0;l_i<nodeElementList1.size();l_i++)
        {
            nodeListElement1=nodeElementList1.get(l_i);

            for(int l_j=0;l_j<nodeElementList2.size();l_j++)
            {
                nodeListElement2=nodeElementList2.get(l_j);

                if(((nodeListElement1.getElementPath()).equals(nodeListElement2.getElementPath()))
                        &&((nodeListElement1.getKeyChildName()).equals(nodeListElement2.getKeyChildName()))
                        &&((nodeListElement1.getKeyChildValue()).equals(nodeListElement2.getKeyChildValue()))
                        &&((nodeListElement1.getModeOfComparison())==(nodeListElement2.getModeOfComparison()))
                        &&((nodeListElement1.getValueChildName()).equals(nodeListElement2.getValueChildName()))
                        )
                {

                    mode=nodeListElement1.getModeOfComparison();

                    switch (mode)
                    {
                        case ComparisonConstants.NO_COMPARISON:

                                if(mFileParameterPOJO.getFilePriority()==ComparisonConstants.PRIORITY_FILE1)
                                {
                                    nodeElementListFinal.add(nodeListElement1);
                                }
                                else
                                {
                                    nodeElementListFinal.add(nodeListElement2);
                                }

                                mIsSuccess=true;


                            break;

                        case ComparisonConstants.COMPARE_GREATER_FILE1:

                            if (compareAttributeValues(nodeListElement1.getValueChildValue(), nodeListElement2.getValueChildValue(),
                                    ComparisonConstants.COMPARE_GREATER_FILE1)) {

                                nodeElementListFinal.add(nodeListElement1);
                                mIsSuccess=true;
                            }
                            else
                            {
                                nodeListElement1.setToBeAdded(false);
                                nodeElementListFinal.add(nodeListElement1);
                            }

                            break;

                        case ComparisonConstants.COMPARE_GREATER_FILE2:

                            if (compareAttributeValues(nodeListElement1.getValueChildValue(), nodeListElement2.getValueChildValue(),
                                    ComparisonConstants.COMPARE_GREATER_FILE2)) {


                                nodeElementListFinal.add(nodeListElement2);
                                mIsSuccess=true;
                            }
                            else
                            {
                                nodeListElement2.setToBeAdded(false);
                                nodeElementListFinal.add(nodeListElement2);
                            }

                            break;

                        case ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1:

                            if (compareAttributeValues(nodeListElement1.getValueChildValue(), nodeListElement2.getValueChildValue(),
                                    ComparisonConstants.COMPARE_GREATER_EQUAL_FILE1)) {

                                nodeElementListFinal.add(nodeListElement1);

                                mIsSuccess=true;

                            }
                            else
                            {
                                nodeListElement1.setToBeAdded(false);
                                nodeElementListFinal.add(nodeListElement1);
                            }


                            break;

                        case ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2:

                            if (compareAttributeValues(nodeListElement1.getValueChildValue(), nodeListElement2.getValueChildValue(),
                                    ComparisonConstants.COMPARE_GREATER_EQUAL_FILE2)) {

                                nodeElementListFinal.add(nodeListElement2);


                                mIsSuccess=true;
                            }
                            else
                            {
                                nodeListElement2.setToBeAdded(false);
                                nodeElementListFinal.add(nodeListElement2);
                            }

                            break;

                        case ComparisonConstants.PICK_FROM_FILE1:

                            nodeElementListFinal.add(nodeListElement1);

                            mIsSuccess=true;

                            break;

                        case ComparisonConstants.PICK_FROM_FILE2:

                            nodeElementListFinal.add(nodeListElement2);

                            mIsSuccess=true;

                            break;

                        case ComparisonConstants.COMPARE_EQUAL:

                            if((nodeListElement1.getKeyChildName().equals(nodeListElement2.getKeyChildName()))
                                    &&(nodeListElement1.getKeyChildValue().equals(nodeListElement2.getKeyChildValue())))
                        {
                            if (compareAttributeValues(nodeListElement1.getValueChildValue(), nodeListElement2.getValueChildValue(),
                                    ComparisonConstants.COMPARE_EQUAL)) {

                                if (mFileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE1) {
                                    nodeElementListFinal.add(nodeListElement1);

                                } else {
                                    nodeElementListFinal.add(nodeListElement2);
                                }
                                mIsSuccess = true;
                            } else {
                                if (mFileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE1) {
                                    nodeListElement1.setToBeAdded(false);
                                    nodeElementListFinal.add(nodeListElement1);

                                } else {
                                    nodeListElement2.setToBeAdded(false);
                                    nodeElementListFinal.add(nodeListElement2);
                                }

                            }
                        }

                            break;

                    }


                }
            }

        }

        return mIsSuccess;

    }


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
