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

    private Document document;
    private Element docRootElement;
    private FileParameterPOJO fileParameterPOJO;
    private static final String TAG="WriteXmlFile";

//--------------------------------------------------------------------------------------------------
    //Parameterised constructor to set and get ParameterList object
    public WriteXmlFile(FileParameterPOJO fileParameterPOJO)
    {
        this.fileParameterPOJO=fileParameterPOJO;
        document= new Document();

    }

    /**
     * This Function will be used to add Root Element to the final document
     * @param finalRootElement - Final root element to be added
     * @return- boolean stating whether addition of final root element was successful or not
     */
    public boolean addRootElementToDocument(Element finalRootElement)
    {
        boolean isSuccessElement=false;
        if(document!=null) {
            document.addChild(Node.ELEMENT, finalRootElement);
            isSuccessElement=true;
        }

        return isSuccessElement;
    }


    /**
     * This function is used for writing the final XML file from the document element
     * @throws IOException
     */
    public void WriteXml() throws IOException
    {
            if (document != null ) {
                XmlSerializer xmlWriter = new KXmlSerializer();

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


            }
        }

    /**
     * This is a helper method that is used to create a directory
     * @param createNewFolder - Final path where directory has to be created
     * @return File object containing created directory
     */
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
