package com.practise.xmlparse.xmldom;

import android.util.Log;
import android.util.Xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kxml2.io.KXmlSerializer;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlSerializer;

/**
 * Created by e00959 on 2/6/2015.
 */

/* This class is used for writing merged Xml file*/
public class WriteXmlFile {

    private ParametersList parametersList;
    private Document document;
    private Element docRootElement;
    private FileParameterPOJO fileParameterPOJO;
    private static final String TAG="WriteXmlFile";

//--------------------------------------------------------------------------------------------------
    //Parameterised constructor to set and get ParameterList object
    public WriteXmlFile(ParametersList parametersList,FileParameterPOJO fileParameterPOJO)
    {
        this.parametersList=parametersList;
        this.fileParameterPOJO=fileParameterPOJO;
        document= new Document();

    }

//--------------------------------------------------------------------------------------------------
    // This function is write the root element of the XML file and its attributes
    public boolean writeRootElement() throws ParserConfigurationException
    {

            int counter = 0;
            List<RootElementPOJO> rootElementPOJOList = parametersList.getRootParameterList();

            // If there is no root element in the list return as document cannot be written without it
            if (rootElementPOJOList.size() < 0) {
                return false;
            }

            for (int count = 0; count < rootElementPOJOList.size(); count++) {
                RootElementPOJO rootElement = rootElementPOJOList.get(count);
                String[] splitTagAttribute = rootElement.getElementName().split(ComparisonConstants.DELIMINATOR_ATTRIBUTE);

                //If the length after split is greater than 1 just add attributes to the root element
                if (splitTagAttribute.length > 1) {
                    setAttribute(splitTagAttribute[1], rootElement.getElementValue(), docRootElement);
                }
                // If the length after split is equal to 1 add root element tag. This will run only once.
                else {
                    docRootElement = document.createElement("",splitTagAttribute[0]);
                    document.addChild(Node.ELEMENT,docRootElement);
                }

            }


            return true;
    }
//--------------------------------------------------------------------------------------------------
    //This function will create an attribute and will append it to the element passed
    private void setAttribute(String attributeName, String attributeValue, Element element)
    {
        element.setAttribute("",attributeName,attributeValue);
    }

//--------------------------------------------------------------------------------------------------

    public Element getDocRootElement()
    {
        if (docRootElement!=null)
        {
            return docRootElement;
        }

        return null;
    }

    public void printXml() throws IOException
    {

            if (docRootElement != null) {
                XmlSerializer xmlWriter = new KXmlSerializer();
                if (fileParameterPOJO.isWriteXml()) {

                    File sdCardPathDirectory = createNewDirectory(fileParameterPOJO.getFinalFilePath());

                    if (sdCardPathDirectory!=null && sdCardPathDirectory.canWrite()) {

                        File file = new File(sdCardPathDirectory, fileParameterPOJO.getFinalFileName());
                        OutputStream outputStream = new FileOutputStream(file);
                        xmlWriter.setOutput(outputStream, null);

                        if (xmlWriter != null) {

                            Log.d(TAG, "-----write file start-----");
                            document.write(xmlWriter);
                            xmlWriter.flush();
                            Log.d(TAG, "-----file written-----");
                            xmlWriter = null;
                            outputStream.close();
                            outputStream = null;

                            Log.d(TAG, "-----write file end-----");
                        }

                    }
                    else
                    {
                        Log.d(TAG, "-----cannot write.Sdcard not available-----");
                    }

                } else {



                }
            }
        }


    private File createNewDirectory(String createNewFolder) {

        // make directory
        File NewDirectory;
        NewDirectory = new File(createNewFolder);
        if (!NewDirectory.exists()) {
            boolean success = NewDirectory.mkdirs();
            if (!success) {
                Log.d(TAG, "mkdirs opn failed");
                return null;
            }
            return NewDirectory;
        }

        return NewDirectory;
    }


}
