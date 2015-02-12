package com.practise.xmlparse.xmldom;

/**
 * Created by e00959 on 2/4/2015.
 */
public interface ComparisonConstants {

    //The deliminator to be used for separating the strings
    public static final  String DELIMINATOR=";";

    //Constant that tells  particular element or attribute is not present in both the files
    public static final int NOT_PRESENT_BOTH=0;

    //Constant that tells  particular element or attribute is  present only in file present in sdcard
    public static final int PRESENT_ONLY_SDCARD=1;

    //Constant that tells  particular element or attribute is  present only in file present in assets
    public static final int PRESENT_ONLY_ASSETS=2;

    //Constant that tells  a particular element or attribute is present in both the files
    public static final int PRESENT_BOTH=3;

    //constant telling type of element is root element
    public static final int ROOT_ELEMENT=4;

    //constant telling type of element is Key element
    public static final int KEY_ELEMENT=5;

    //constant telling type of element is Inner element
    public static final int INNER_ELEMENT=6;

    public static final int PUSH_XML =7;
    public static final int PUSH_XML_NOT =8;

    public static final int ASSET_FILE=9;
    public static final int SDCARD_FILE=10;

}
