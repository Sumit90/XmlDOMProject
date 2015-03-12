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

    /**
     * Parameterised constructor to create FileParameterPOJO object
     * @param filePriority  - whether file1 has more priority or file 2
     * @param finalFileName - Name of the final merged file
     * @param finalFilePath - Path where final merged file will be saved
     */
    public FileParameterPOJO( int filePriority, String finalFileName, String finalFilePath)

    {
        this.filePriority=filePriority;
        this.finalFileName=finalFileName;
        this.finalFilePath=finalFilePath;


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


}
