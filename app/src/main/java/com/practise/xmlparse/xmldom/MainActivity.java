package com.practise.xmlparse.xmldom;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Element;
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
                        Element rootElementAssetsFile = parseFile1.parseRootElement();
                        Element rootElementSdCardFile = parseFile2.parseRootElement();

                        //add root parameters to list after comparing with initial root elements
                        addRootParameters(ParameterListFile1,rootElementAssetsFile);
                        addRootParameters(ParameterListFile2,rootElementSdCardFile);

                        printRootList(ParameterListFile1.getRootParameterList());
                        printRootList(ParameterListFile2.getRootParameterList());

                        //get the final Root element Lists
                        /*List<RootElementPOJO> finalRootElementList =compareAndAddToListRoot
                                (ParameterListFile1.getRootParameterList(),ParameterListFile2.getRootParameterList(),
                                        initialParameterList.getRootParameterList());*/

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
                            WriteXmlFile writeFile = new WriteXmlFile(ParameterListFinal);
                            writeFile.writeXml();
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
            addInitialRootParameters("fastLogcodes;B",ComparisonConstants.COMPARE_EQUAL);
            addInitialRootParameters("fastLogcodes;C",ComparisonConstants.COMPARE_EQUAL);


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

        List<RootElementPOJO> finalRootList=null;

        // If the user has not provided any Root attribute list
        if(listRootInitial==null || listRootInitial.size()==0)
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
            OUTERLOOP:for(int counter=0;counter<listRootInitial.size();counter++)
            {
                rootElement=listRootInitial.get(counter);
                //get the mode whether element has to be pushed in final XML
                modeComparison=initialParameterList.getModeComparison(rootElement.getElementName());


                if(modeComparison==ComparisonConstants.COMPARE_EQUAL
                        || modeComparison==ComparisonConstants.COMPARE_GREATER_FILE1
                            || modeComparison==ComparisonConstants.COMPARE_GREATER_FILE2)
                {
                    //If file1 does not contain any of the elements exit the loop
                    if(listRootFile1==null || listRootFile1.size()==0)
                    {
                        Log.d(TAG,"-----getMergedRootList(): listRootFile1==null");
                        break OUTERLOOP;
                    }
                    //If file2 does not contain any of the elements exit the loop
                    else if(listRootFile2==null || listRootFile2.size()==0)
                    {
                        Log.d(TAG,"-----getMergedRootList(): listRootFile2==null");
                        break OUTERLOOP;
                    }
                }

                switch(modeComparison)
                {
                    // If mode of comparison is compare for equal
//-------------------------------------COMPARE_EQUAL START------------------------------------------
                    case ComparisonConstants.COMPARE_EQUAL:

                        // Check if element exists in File1 Root list
                        rootElementFile1=getElement(listRootFile1,rootElement);

                       /*If the required element is found in File1 then check for the element in File2*/
                       if(rootElementFile1!=null)
                       {
                           rootElementFile2=getElement(listRootFile2,rootElement);

                       }
                       /*If the required element not found in File1 then break the loop*/
                        else
                       {
                         statusMerging=false;
                         Log.d(TAG,"-----getMergedRootList(): "+rootElement.getElementName()+" Not Found in File1");
                       }

                        /* If the required element is found in File1 and File 2 then add the element
                        * in final list after checking which file has more priority*/
                        if(rootElementFile1!=null && rootElementFile2!=null)
                        {
                           /*Compare the attribute values of File1 and File2. If they are equal add
                           it to final list else exit the outer loop*/
                           if((rootElementFile1.getElementValue().trim()).equals(rootElementFile2.getElementValue().trim()))
                           {
                               /*If file1 has more priority then add the element in final list from file1 list*/
                               if(fileParameterPOJO.getFilePriority()==ComparisonConstants.PRIORITY_FILE1)
                               {
                                   finalRootList.add(rootElementFile1);
                               }
                            /*If file2 has more priority then add the element in final list from file2 list*/
                               else
                               {
                                   finalRootList.add(rootElementFile2);
                               }
                               statusMerging=true;
                           }
                            else
                           {
                               Log.d(TAG,"-----getMergedRootList(): "+rootElementFile1.getElementValue()+" != "+rootElementFile2.getElementValue());
                               statusMerging=false;
                               break OUTERLOOP;
                           }

                        }
                         /*If the required element not found in File2 then break the loop*/
                        else
                        {
                            if(rootElementFile2==null)
                            {
                                Log.d(TAG,"-----getMergedRootList(): "+rootElement.getElementName()+" Not Found File2");
                                statusMerging=false;
                            }

                            break OUTERLOOP;
                        }

                        break;

//-------------------------------------COMPARE_EQUAL END--------------------------------------------

//--------------------------------COMPARE_GREATER_FILE1 START---------------------------------------

                    case ComparisonConstants.COMPARE_GREATER_FILE1:

                        break;
//--------------------------------COMPARE_GREATER_FILE1 END-----------------------------------------

//--------------------------------COMPARE_GREATER_FILE2 START---------------------------------------

                    case ComparisonConstants.COMPARE_GREATER_FILE2:

                        break;


//-------------------------------COMPARE_GREATER_FILE2 END------------------------------------------
                }
            }
        }


        return finalRootList;


    }
//--------------------------------------------------------------------------------------------------
    /*This element basically searches for a element searchElement in list listElements and returns
    * the searched element from the list*/
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

/* This method will compare the Root element List from both Asset file and SdCard File and will
    create a final root element list to be written*/

    private List<RootElementPOJO> compareAndAddToListRoot(List<RootElementPOJO> listAssets,
                                       List<RootElementPOJO> listSdCard,List<RootElementPOJO> listInitial) {

        List<RootElementPOJO> finalRootList=new ArrayList<RootElementPOJO>();


        /* If Attributes in Assets File list is greater than Sdcard file List
           write Assets file Attribute to the final list */

        if ((listSdCard != null && listAssets != null) && (listAssets.size() > listSdCard.size())) {
            Log.d(TAG, "Assets list greater than sdcard");

            int modeComparison=0;
            RootElementPOJO rootElementAsset;
            for(int counter=0;counter<listAssets.size();counter++)
            {
                rootElementAsset=listAssets.get(counter);
                //get the mode whether element has to be pushed in final XML
                modeComparison=initialParameterList.getModeComparison(rootElementAsset.getElementName());

                //If the parameter has to be pushed in XML
                if(modeComparison==ComparisonConstants.PUSH_XML)
                {
                    finalRootList.add(rootElementAsset);
                }

            }
           // finalRootList = listAssets;
        }

         /* If Attributes in sdcard File list is greater than Assets file List
           write sdcard file Attribute to the final list */

        else if ((listSdCard != null && listAssets != null) && (listSdCard.size() > listAssets.size())) {
            Log.d(TAG, "sdcard list greater than Assets");

            int modeComparison=0;
            RootElementPOJO rootElementSdCard;
            for(int counter=0;counter<listSdCard.size();counter++)
            {
                rootElementSdCard=listSdCard.get(counter);
                //get the mode whether element has to be pushed in final XML
                modeComparison=initialParameterList.getModeComparison(rootElementSdCard.getElementName());

                //If the parameter has to be pushed in XML
                if(modeComparison==ComparisonConstants.PUSH_XML)
                {
                    finalRootList.add(rootElementSdCard);
                }

            }

          //  finalRootList = listSdCard;
        }
        else{

            /* If Attributes in sdcard File list and Assets file List are equal to initial root list
               then iterate over the lists. If element has to be push in final Xml file
                then check the file priority. If assets file priority is greater than assets file
                then push assets list root attribute else push sdcard list root attribute */

            if ((listSdCard != null && listAssets != null && listInitial != null) &&
                    (listAssets.size() == listInitial.size() && listSdCard.size() == listInitial.size())) {

                Log.d(TAG, "sdcard list equals than Assets  equals initial list size");

                RootElementPOJO rootEleAssets;
                RootElementPOJO rootEleSdCard;
                RootElementPOJO rootEleInitial;

                for (int count = 0; count < listSdCard.size(); count++) {
                    rootEleAssets = listAssets.get(count);
                    rootEleSdCard = listSdCard.get(count);
                    rootEleInitial = listInitial.get(count);

                    //get the mode whether element has to be pushed in final XML
                    if (rootEleInitial.getModeOfComparison() == ComparisonConstants.PUSH_XML) {
                        Log.d(TAG, "PUSH XML Case");

                        // check the file priority
                        if (fileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE1) {
                            Log.d(TAG, "ASSET File more priority");
                            finalRootList.add(rootEleAssets);
                        } else {
                            Log.d(TAG, "SDcard File more priority");
                            finalRootList.add(rootEleSdCard);
                        }
                    } else if (rootEleInitial.getModeOfComparison() == ComparisonConstants.PUSH_XML_NOT) {
                        Log.d(TAG, "No PUSH XML Case");
                    }

                }


            }
            /* If size of assets root attribute list is equal to sdcard root attribute list but
             both of them is not equal to initial root attribute list
            */
            else {
                if ((listSdCard != null && listAssets != null) && (listSdCard.size() == listAssets.size())) {
                    RootElementPOJO rootEleAssets;
                    RootElementPOJO rootEleSdCard;
                    RootElementPOJO rootEleInitial;

                    Log.d(TAG, "sdcard list equals than Assets but not equals initial list size");

                    for (int count = 0; count < listSdCard.size(); count++) {
                        rootEleAssets = listAssets.get(count);
                        rootEleSdCard = listSdCard.get(count);
                        int modeComparison=0;

                        // If the final Root attribute list does not contain any element
                        if(finalRootList.size()==0)
                        {
                            /*If the name of elements in both file are same then check whether*/
                            if(rootEleAssets.getElementName().equals(rootEleSdCard.getElementName()))
                            {
                                Log.d(TAG, "First elements of both list are same");
                                modeComparison=initialParameterList.getModeComparison(rootEleAssets.getElementName());
                                if ( modeComparison== ComparisonConstants.PUSH_XML) {
                                    Log.d(TAG, "PUSH XML Case1");

                                    // check the file priority
                                    if (fileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE1) {
                                        Log.d(TAG, "ASSET File more priority1");
                                        finalRootList.add(rootEleAssets);
                                    } else {
                                        Log.d(TAG, "SDcard File more priority1");
                                        finalRootList.add(rootEleSdCard);
                                    }
                                } else if (modeComparison == ComparisonConstants.PUSH_XML_NOT) {
                                    Log.d(TAG, "No PUSH XML Case1");
                                }
                            }
                            /*If the name of elements are not same add both the elements after
                            * then add both elements */
                            else
                            {
                                modeComparison=initialParameterList.getModeComparison(rootEleAssets.getElementName());
                                if ( modeComparison== ComparisonConstants.PUSH_XML) {
                                    Log.d(TAG, "PUSH XML Case2");
                                    finalRootList.add(rootEleAssets);
                                }else if (modeComparison == ComparisonConstants.PUSH_XML_NOT) {
                                    Log.d(TAG, "No PUSH XML Case2");
                                }

                                modeComparison=initialParameterList.getModeComparison(rootEleSdCard.getElementName());
                                if ( modeComparison== ComparisonConstants.PUSH_XML) {
                                    Log.d(TAG, "PUSH XML Case3");
                                    finalRootList.add(rootEleSdCard);
                                }else if (modeComparison == ComparisonConstants.PUSH_XML_NOT) {
                                    Log.d(TAG, "No PUSH XML Case3");
                                }
                            }
                        }

                        /*If there are already some elements in the list then iterate over the list
                        * to check whether current element exists in the list. If it exists then
                        * check the file priority and update the element accordingly*/
                        else {

                            if(rootEleAssets.getElementName().equals(rootEleSdCard.getElementName()))
                            {
                                Log.d(TAG, "First elements of both list are same");
                                modeComparison=initialParameterList.getModeComparison(rootEleAssets.getElementName());
                                if ( modeComparison== ComparisonConstants.PUSH_XML) {
                                    Log.d(TAG, "PUSH XML Case4");

                                    // check the file priority
                                    if (fileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE1) {
                                        Log.d(TAG, "ASSET File more priority4");
                                        finalRootList.add(rootEleAssets);
                                    } else {
                                        Log.d(TAG, "SDcard File more priority4");
                                        finalRootList.add(rootEleSdCard);
                                    }
                                } else if (modeComparison == ComparisonConstants.PUSH_XML_NOT) {
                                    Log.d(TAG, "No PUSH XML Case4");
                                }
                            }
                            /*Iterate over the list to check whether the current element exists. If
                            * exists update the existing element in the list on the basis of file priority*/
                            else{

                                int itr=0;
                                for (itr=0;itr<finalRootList.size();itr++)
                                {
                                    if(finalRootList.get(itr).getElementName().equals(rootEleAssets.getElementName()))
                                    {
                                        if (fileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE1) {
                                            Log.d(TAG, "ASSET File more priority4");

                                            finalRootList.get(itr).setElementValue(rootEleAssets.getElementValue());

                                            modeComparison=initialParameterList.getModeComparison(rootEleSdCard.getElementName());

                                            if(modeComparison==ComparisonConstants.PUSH_XML){
                                                finalRootList.add(rootEleSdCard);
                                            }

                                            break;
                                        }
                                    }
                                     if(finalRootList.get(itr).getElementName().equals(rootEleSdCard.getElementName()))
                                    {
                                        if (fileParameterPOJO.getFilePriority() == ComparisonConstants.PRIORITY_FILE2) {
                                            Log.d(TAG, "ASSET File more priority4");

                                            finalRootList.get(itr).setElementValue(rootEleSdCard.getElementValue());

                                            modeComparison=initialParameterList.getModeComparison(rootEleAssets.getElementName());

                                            if(modeComparison==ComparisonConstants.PUSH_XML){
                                                finalRootList.add(rootEleAssets);
                                            }
                                            break;
                                        }
                                    }

                                }

                                /*If no match found in list then add both the elements*/
                                if(itr+1>finalRootList.size())
                                {
                                    Log.d(TAG, "outside loop. Adding both to final list");
                                    modeComparison=initialParameterList.getModeComparison(rootEleAssets.getElementName());

                                    if(modeComparison==ComparisonConstants.PUSH_XML){
                                        finalRootList.add(rootEleAssets);
                                    }

                                    modeComparison=initialParameterList.getModeComparison(rootEleSdCard.getElementName());

                                    if(modeComparison==ComparisonConstants.PUSH_XML){
                                        finalRootList.add(rootEleSdCard);
                                    }
                                }
                            }
                        }
                    }
                }
            }
         }


        return finalRootList;


    }
//--------------------------------------------------------------------------------------------------
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
