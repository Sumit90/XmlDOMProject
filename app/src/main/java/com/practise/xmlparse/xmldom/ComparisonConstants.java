package com.practise.xmlparse.xmldom;

/**
 * Created by e00959 on 2/4/2015.
 *
 * This file consists of an Interface that have constants, that will be used across the source for
 * comparing and merging of the Xml files .
 */
public interface ComparisonConstants {

 //The deliminator to be used for separating the strings
    public static final  String DELIMINATOR=";";

  //Constant to be used to tell that a specific attribute has to be taken from File2
    public static final int PICK_FROM_FILE1=0;

  //Constant to be used to tell that a specific attribute has to be taken from File2
    public static final int PICK_FROM_FILE2=1;

  //Constant to be used to tell that a specific attribute is to be compared in both files for equality
    public static final int COMPARE_EQUAL=2;

    /*Constant to be used to tell that a specific attribute is to be compared in both files with
  * value to be greater in File1*/
     public static final int COMPARE_GREATER_FILE1=3;

    /*Constant to be used to tell that a specific attribute is to be compared in both files with
      * value to be greater in File2*/
    public static final int COMPARE_GREATER_FILE2=4;

    /*Constant to be used to tell that a specific attribute can be picked from any file on the basis
    * on file priority*/
    public static final int NO_COMPARISON=5;

    // Constant to be used to tell that the given element is Root element
    public static final int ELEMENT_ROOT=6;

    // Constant to be used to tell that the given element is not a Root element
    public static final int ELEMENT_NON_ROOT=7;

    /*Constant used to tell that File 1 is of higher priority */
    public static final int PRIORITY_FILE1 =8;

    /*Constant used to tell that File 2 is of higher priority */
    public static final int PRIORITY_FILE2 =9;

//TODO remove constants
    public static final int PUSH_XML =10;
    public static final int PUSH_XML_NOT =11;



}
