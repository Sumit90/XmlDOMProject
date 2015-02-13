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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends ActionBarActivity {

    private Button parseBtn;

    private InputStream inputStreamAssetsFile;
    private InputStream inputStreamSdCardFile;

    private XmlParser parseAssetsFile;
    private XmlParser parseSdCardFile;

    private InitialParameterList initialParameterList;

    private ParametersList ParameterListAssetsFile;
    private ParametersList ParameterListSdCardFile;
    private ParametersList ParameterListFinal;

    private FileParameterPOJO fileParameterPOJO;

    private final String TAG="MYLOGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parseBtn=(Button)findViewById(R.id.parse_button);



        parseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //Create only one instance
                    fileParameterPOJO=new FileParameterPOJO("LogCodes.xml",""
                                            ,ComparisonConstants.SDCARD_FILE,"LogCodes.xml","");
                    initialParameterList=new InitialParameterList();

                    ParameterListAssetsFile =new ParametersList();
                    ParameterListSdCardFile=new ParametersList();
                    ParameterListFinal=new ParametersList();

                    addInitialRootParameters("fastLogcodes",ComparisonConstants.PUSH_XML);
                    /*addInitialRootParameters("fastLogcodes;version",ComparisonConstants.PUSH_XML);
                    addInitialRootParameters("fastLogcodes;name",ComparisonConstants.PUSH_XML);
                    addInitialRootParameters("fastLogcodes;value",ComparisonConstants.PUSH_XML);
                    addInitialRootParameters("fastLogcodes;company",ComparisonConstants.PUSH_XML);
                    addInitialRootParameters("fastLogcodes;company1",ComparisonConstants.PUSH_XML);*/

                    addInitialRootParameters("fastLogcodes;A",ComparisonConstants.PUSH_XML);
                    addInitialRootParameters("fastLogcodes;B",ComparisonConstants.PUSH_XML);
                    addInitialRootParameters("fastLogcodes;C",ComparisonConstants.PUSH_XML);
                    addInitialRootParameters("fastLogcodes;D",ComparisonConstants.PUSH_XML);
                    addInitialRootParameters("fastLogcodes;E",ComparisonConstants.PUSH_XML);


                    printRootList(initialParameterList.getRootParameterList());

                    //open the assets file
                    inputStreamAssetsFile = getResources().getAssets().open("LogCodes.xml");
                    inputStreamSdCardFile = getResources().getAssets().open("LogCodes1.xml");

                    parseAssetsFile = new XmlParser(inputStreamAssetsFile);
                    parseSdCardFile=  new XmlParser(inputStreamSdCardFile);

                    //get the root elements of the XML files
                    Element rootElementAssetsFile = parseAssetsFile.parseRootElement();
                    Element rootElementSdCardFile = parseSdCardFile.parseRootElement();

                    //add root parameters to list after comparing with initial root elements
                    addRootParameters(ParameterListAssetsFile,rootElementAssetsFile);
                    addRootParameters(ParameterListSdCardFile,rootElementSdCardFile);

                    printRootList(ParameterListAssetsFile.getRootParameterList());
                    printRootList(ParameterListSdCardFile.getRootParameterList());


                    //get the final Root element Lists
                    List<RootElementPOJO> finalRootElementList =compareAndAddToListRoot
                                (ParameterListAssetsFile.getRootParameterList(),ParameterListSdCardFile.getRootParameterList(),
                                        initialParameterList.getRootParameterList());
                    if(finalRootElementList!=null)
                    {
                        Log.d(TAG,"---------- print final list start-------------");
                        printRootList(ParameterListSdCardFile.getRootParameterList());
                        Log.d(TAG,"---------- print final list end-------------");
                        ParameterListFinal.setRootParameterList(finalRootElementList);
                        Log.d(TAG,"---------- Writing XML start-------------");
                        WriteXmlFile writeFile = new WriteXmlFile(ParameterListFinal);
                        writeFile.writeXml();
                        Log.d(TAG,"---------- Writing Xml end-------------");
                    }
                }

                catch (ParserConfigurationException e)
                {
                    Log.d(TAG,"inside ParserConfigurationException");
                    e.printStackTrace();
                }
                catch (SAXException e)
                {
                    Log.d(TAG,"inside SAXException");
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    Log.d(TAG,"inside IOException");
                    e.printStackTrace();
                }
                catch (Exception e) {

                    Log.d(TAG,"inside Exception");
                    e.printStackTrace();
                }

            }
        });
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
                            Log.d(TAG,"Add Root Element Attributes");

                                paramList.addRootParameter(new
                                        RootElementPOJO(rootElementName, attributeValue,
                                        rootElementPOJO.getModeOfComparison()));


                        }
                    }

                    //If user has passed only Root tag name add it to the list.
                    else
                    {
                        Log.d(TAG,"Add Root Element");

                            paramList.addRootParameter(new
                                    RootElementPOJO(tagAttribute[0], "", rootElementPOJO.getModeOfComparison()));


                    }
                }
            }
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
                        if (fileParameterPOJO.getFilePriority() == ComparisonConstants.ASSET_FILE) {
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
                                    if (fileParameterPOJO.getFilePriority() == ComparisonConstants.ASSET_FILE) {
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
                        else {

                            if(rootEleAssets.getElementName().equals(rootEleSdCard.getElementName()))
                            {
                                Log.d(TAG, "First elements of both list are same");
                                modeComparison=initialParameterList.getModeComparison(rootEleAssets.getElementName());
                                if ( modeComparison== ComparisonConstants.PUSH_XML) {
                                    Log.d(TAG, "PUSH XML Case4");

                                    // check the file priority
                                    if (fileParameterPOJO.getFilePriority() == ComparisonConstants.ASSET_FILE) {
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
                            else{

                                int itr=0;
                                for (itr=0;itr<finalRootList.size();itr++)
                                {
                                    if(finalRootList.get(itr).getElementName().equals(rootEleAssets.getElementName()))
                                    {
                                        if (fileParameterPOJO.getFilePriority() == ComparisonConstants.ASSET_FILE) {
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
                                        if (fileParameterPOJO.getFilePriority() == ComparisonConstants.SDCARD_FILE) {
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
