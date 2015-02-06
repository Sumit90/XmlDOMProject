package com.practise.xmlparse.xmldom;

/**
 * Created by e00959 on 2/4/2015.
 */
public class FileParameterPOJO {

    private String fileName;
    private String sdCardFilePath;
    private int filePriority;
    private String finalFileName;
    private String finalFilePath;


    public FileParameterPOJO(String fileName, String sdCardFilePath, int filePriority,
                             String finalFileName, String finalFilePath)

    {
        this.fileName=fileName;
        this.sdCardFilePath=sdCardFilePath;
        this.filePriority=filePriority;
        this.finalFileName=finalFileName;
        this.finalFilePath=finalFilePath;
    }

    public String getFileName() {
        return fileName;
    }


    public String getSdCardFilePath() {
        return sdCardFilePath;
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
