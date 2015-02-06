package com.practise.xmlparse.xmldom;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by e00959 on 2/6/2015.
 */

/* This class is used for writing merged Xml file*/
public class WriteXmlFile {

    private ParametersList parametersList;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;
    private Element docRootElement;

    //Parameterised constructor to set and get ParameterList object
    public WriteXmlFile(ParametersList parametersList) throws ParserConfigurationException
    {
        this.parametersList=parametersList;
        documentBuilderFactory=DocumentBuilderFactory.newInstance();
        documentBuilder=documentBuilderFactory.newDocumentBuilder();
        document=documentBuilder.newDocument();
    }

    public void writeXml() throws ParserConfigurationException
    {
        System.out.print("-----hello inside writeXml()----------");
        try {
            if (writeRootElement()) {

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);

                //StreamResult result = new StreamResult(new File("C:\\file.xml"));

                // Output to console for testing
                 StreamResult result = new StreamResult(System.out);
                 transformer.transform(source, result);

            }

            System.out.print("-----hello done writeXml()----------");
        }
        catch (TransformerException ex) {
            ex.printStackTrace();
        }

    }


    private boolean writeRootElement() throws ParserConfigurationException
    {
        try {
            int counter = 0;
            List<RootElementPOJO> rootElementPOJOList = parametersList.getRootParameterList();

            // If there is no root element in the list return as document cannot be written without it
            if (rootElementPOJOList.size() < 0) {
                return false;
            }

            for (int count = 0; count < rootElementPOJOList.size(); count++) {
                RootElementPOJO rootElement = rootElementPOJOList.get(count);
                String[] splitTagAttribute = rootElement.getElementName().split(ComparisonConstants.DELIMINATOR);

                //If the length after split is greater than 1 just add attributes to the root element
                if (splitTagAttribute.length > 1) {
                    createAttribute(splitTagAttribute[1], rootElement.getElementValue(), docRootElement);
                }
                // If the length after split is equal to 1 add root element tag. This will run only once.
                else {
                    docRootElement = document.createElement(splitTagAttribute[0]);
                    document.appendChild(docRootElement);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

            return true;
    }

    //This function will create an attribute and will append it to the element passed
    private void createAttribute(String attributeName,String attributeValue,Element element)
    {
        Attr rootElementAttribute   =   document.createAttribute(attributeName);
        rootElementAttribute.setValue(attributeValue);
        element.setAttributeNode(rootElementAttribute);
    }


}
