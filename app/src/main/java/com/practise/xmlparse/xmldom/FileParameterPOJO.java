package com.practise.xmlparse.xmldom;

import java.io.File;
import java.io.InputStream;

/**
 * This class will hold certain file characteristics related to input files and final file
 */
public class FileParameterPOJO {

    private int filePriority; //This will store the file priority
    private String finalFileName; //This will store the final file name
    private String finalFilePath; // This will store the final file path
    private boolean isWriteXml; //This boolean will decide whether final xml file has to be write or not.

    public FileParameterPOJO( int filePriority, String finalFileName, String finalFilePath,
                              boolean isWriteXml)

    {
        this.filePriority=filePriority;
        this.finalFileName=finalFileName;
        this.finalFilePath=finalFilePath;
        this.isWriteXml=isWriteXml;

    }


    public int getFilePriority() {
        return filePriority;
    }


    public String getFinalFileName() {
        return finalFileName;
    }


    public String getFinalFilePath() {
        return finalFilePath;
    }

    public boolean isWriteXml() {
        return isWriteXml;
    }
}
