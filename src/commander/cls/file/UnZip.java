/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Nam
 */
public class UnZip
{
    ArrayList<String> fileList;
    /**
     * Unzip it
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    public File unZipIt(String zipFile, String outputFolder){

        byte[] buffer = new byte[1024];

        try{
            // create output directory is not exists
            File folder = new File(outputFolder);
            if(!folder.exists()){
                folder.mkdir();
            }

            //get the zip file content
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(fis);
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null){

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                
                if (ze.isDirectory()) {
                    newFile.mkdirs();
                }
                else {
                    System.out.println("file unzip : "+ newFile.getAbsoluteFile());
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);             
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();   
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
            fis.close();

            System.out.println("Done");
            return folder;

        } catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }    
}
