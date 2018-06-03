/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.controller;

import commander.cls.TablePanel;
import commander.cls.file.FileInfo;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    
    public void copyFile(FileInfo src, File dst, Runnable toRun) {
        try
        {
            boolean isDesExist = Files.exists(dst.toPath());
            if (src.isReadable) {
                if (src.isFile) {
                    FileUtils.copyFile(src.file, dst);
                }
                else {
                    FileUtils.copyDirectory(src.file, dst, new FileFilter() {
                        @Override 
                        public boolean accept(File file) {
                            return Files.isReadable(file.toPath());
                        }
                    });
                }
                if (!isDesExist) {
                    toRun.run();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void moveFile(FileInfo src, File dst, Runnable toRun) {
        try
        {
            boolean isDesExist = Files.exists(dst.toPath());
            if (src.isReadable) {
                if (src.isFile) {
                    FileUtils.moveFile(src.file, dst);
                }
                else {
                    FileUtils.moveDirectory(src.file, dst);
                }
                if (!isDesExist) {
                    toRun.run();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void deleteFile(FileInfo src, File dst, Runnable toRun) {
         try
        {
            boolean isDesExist = Files.exists(dst.toPath());
            if (src.isReadable) {
                if (src.isFile) {
                    FileUtils.forceDelete(src.file);
                }
                else {
                    FileUtils.deleteDirectory(src.file);
                }
                if (!isDesExist) {
                    toRun.run();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public File renameFile(FileInfo src, String newName) {
        try {
            Path des = Paths.get(src.file.getParent(), newName);
            if (Files.exists(des) || Files.isReadable(des)) {
                System.out.println(des.toString() + " exists or permission");
                return null;
            } 
            else {
                File desFile = des.toFile();
                src.file.renameTo(desFile);
                return desFile;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ArrayList<FileInfo> getListItem() {
        return listItem;
    }
    
    public TablePanel srcPanel;
}
