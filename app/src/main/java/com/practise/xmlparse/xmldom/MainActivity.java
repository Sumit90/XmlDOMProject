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
                    initialParameterList=new InitialParameterList();

                    ParameterListAssetsFile =new ParametersList();
                    ParameterListSdCardFile=new ParametersList();

                    addInitialRootParameters("fastLogcodes;",0);
                    addInitialRootParameters("fastLogcodes;version",0);
                    addInitialRootParameters("fastLogcodes;name",0);
                    addInitialRootParameters("fastLogcodes;name1",0);

                    printRootList(initialParameterList.getRootParameterList());

                    //open the assets file
                    inputStreamAssetsFile = getResources().getAssets().open("LogCodes.xml");
                    inputStreamSdCardFile = getResources().getAssets().open("LogCodes1.xml");

                    parseAssetsFile = new XmlParser(inputStreamAssetsFile);
                    parseSdCardFile=  new XmlParser(inputStreamSdCardFile);

                    Element rootElementAssetsFile = parseAssetsFile.parseRootElement();
                    Element rootElementSdCardFile = parseSdCardFile.parseRootElement();

                    addRootParameters(ParameterListAssetsFile,rootElementAssetsFile);
                    addRootParameters(ParameterListSdCardFile,rootElementSdCardFile);

                    printRootList(ParameterListAssetsFile.getRootParameterList());
                    printRootList(ParameterListSdCardFile.getRootParameterList());

                    WriteXmlFile writeFile = new WriteXmlFile(ParameterListSdCardFile);
                    writeFile.writeXml();

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

    //This function is used to add Initial Root attributes to be used for comparing and extracting
        void addInitialRootParameters(String attributeName,int mode)
        {
            initialParameterList.addInitialRootParameter(new RootElementPOJO(attributeName,mode));
        }

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
                      after splitting (tagAttribute[1])*/
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

                    //If user has pass only Root tag name add it to the list.
                    else
                    {
                        paramList.addRootParameter(new
                                RootElementPOJO(tagAttribute[0]));

                    }



                }


            }

        }


   private void printRootList(List<RootElementPOJO> list)
   {

       Log.d(TAG,"--------inside printRootList Start-----------");
       for (int i=0;i<list.size();i++)
       {
           RootElementPOJO elementPOJO=list.get(i);
           Log.d(TAG,"Root Element Name"+elementPOJO.getElementName());
           Log.d(TAG,"Root Element Value"+elementPOJO.getElementValue());
           Log.d(TAG,"Root Element Mode"+elementPOJO.getModeOfComparison());
       }

       Log.d(TAG,"--------inside printRootList End-----------");
   }


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
