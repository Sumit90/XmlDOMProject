package com.practise.xmlparse.xmldom;

import java.io.File;
import java.io.InputStream;

/**
 * Created by e00959 on 2/4/2015.
 */
public class FileParameterPOJO {

    InputStream inputFile1;
    InputStream inputFile2;
    private int filePriority;
    private String finalFileName;
    private String finalFilePath;
    private boolean isWriteXml;

    public FileParameterPOJO(InputStream file1, InputStream file2, int filePriority,
                             String finalFileName, String finalFilePath,boolean isWriteXml)

    {
        this.inputFile1=file1;
        this.inputFile2=file2;
        this.filePriority=filePriority;
        this.finalFileName=finalFileName;
        this.finalFilePath=finalFilePath;
        this.isWriteXml=isWriteXml;

    }

    public InputStream getInputFile1() {
        return inputFile1;
    }

    public InputStream getInputFile2() {
        return inputFile2;
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
