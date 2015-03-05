package com.practise.xmlparse.xmldom;



import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;


import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * The class will be used to parse the provided input stream of Xml file
 */

//
public class XmlParser {

    private Document document;

    public XmlParser(InputStream is) throws XmlPullParserException,SAXException,IOException
    {
        document =new Document();
        XmlPullParser configParser = new KXmlParser();
        configParser.setInput(is, null);
        configParser.setFeature(
                XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        document.parse(configParser);

    }

    // This function will return the document's root element
    public Element parseRootElement()
    {
        Element element=document.getRootElement();
        return element;
    }

}
