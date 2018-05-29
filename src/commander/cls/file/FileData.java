/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Nam
 */
public class FileData {
    
    public File file;
    public Path path;
    
    // Atrribute show in table
    public String name;
    public String extension;
    public String size; // As kB
    public String lastModified;
    public String attribute;
    public ImageIcon icon;
    
    // Hidden Attribute
    public boolean isExist;
    public boolean isHidden;
    public boolean isFile;
    public boolean isReadable;
    public boolean isWriteable;
    
    public FileData(File f) {
        init(f);
    }
    
    public void init(File f) {
        file = f.getAbsoluteFile();
        path = f.toPath();
        
        if (isExist = file.exists()) {
            isHidden = file.isHidden();
            isFile = file.isFile();
            isReadable = Files.isReadable(path);
            isWriteable = Files.isWritable(path);
            
            name = file.getName();
            extension = parseExtension(name);
            size = String.valueOf(file.length());
            lastModified = new SimpleDateFormat("dd/MM/yyyy").format(new Date(file.lastModified()));
            attribute = parseAttribute(isReadable, isWriteable, isHidden);
            icon = (ImageIcon)FileSystemView.getFileSystemView().getSystemIcon(path.toFile());
        }
    }
    
    private static String parseExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }
    
    private static String parseAttribute(boolean r, boolean w, boolean h) {
        String attr = "";
        attr += r ? "r" : "-";
        attr += w ? "w" : "-";
        attr += h ? "h" : "-";
        return attr;
    }
}
