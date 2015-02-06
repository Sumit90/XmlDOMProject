package com.practise.xmlparse.xmldom;


import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by e00959 on 2/6/2015.
 */
public class XmlParser {

    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;

    public XmlParser(InputStream is) throws ParserConfigurationException,SAXException,IOException
    {
        documentBuilderFactory=DocumentBuilderFactory.newInstance();
        documentBuilder=documentBuilderFactory.newDocumentBuilder();
        document =documentBuilder.parse(is);

    }

    public Element parseRootElement()
    {
        Element element=document.getDocumentElement();
        return element;
    }


}
