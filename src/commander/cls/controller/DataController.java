/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.controller;

import commander.cls.TablePanel;
import commander.cls.file.FileInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Nam
 */
public class DataController {
    
    private static DataController instance;
    
    public static DataController getInstance() {
        if (instance == null) {
            instance = new DataController();
        }
        return instance;
    }
    
    private ArrayList<FileInfo> listItem;
    
    public DataController() {
        listItem = new ArrayList<>();
    }
    
    public void copyFile(File src, File dst, Runnable toRun) {
        try
        {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            // Transfer bytes from in to out
            long expectedBytes = src.length(); // This is the number of bytes we expected to copy..
            long totalBytesCopied = 0; // This will track the total number of bytes we've copied
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                totalBytesCopied += len;
                int progress = (int)Math.round(((double)totalBytesCopied / (double)expectedBytes) * 100);
            }
            toRun.run();
            in.close();
            out.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public ArrayList<FileInfo> getListItem() {
        return listItem;
    }
    
    public TablePanel srcPanel;
    
    
}
