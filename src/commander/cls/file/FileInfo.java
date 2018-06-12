package commander.cls.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;


public class FileInfo {

    public File file;
    public Path path;
    
    // Atrribute show in table
    public String name;
    public String extension;
    public long size;
    public String lastModified;
    public String attribute;
    public ImageIcon icon;
    
    // Hidden Attribute
    public boolean isExist;
    public boolean isHidden;
    public boolean isFile;
    public boolean isDirectory;
    public boolean isReadable;
    public boolean isWriteable;
    
    
    public FileInfo(Path p) {
        init(p);
    }

    public void init(Path p) {
        path = p.toAbsolutePath();
        file = path.toFile();
        
        if (isExist = file.exists()) {
            isHidden = file.isHidden();
            isFile = file.isFile();
            isDirectory = file.isDirectory();
            isReadable = Files.isReadable(path);
            isWriteable = Files.isWritable(path);
            
            name = file.getName();
            extension = isDirectory ? "[DIR]" : parseExtension(name);
            size = isDirectory ? -1 : file.length();
            lastModified = new SimpleDateFormat("dd/MM/yyyy").format(new Date(file.lastModified()));
            attribute = parseAttribute(isReadable, isWriteable, isHidden);
            try {
                icon = (ImageIcon)FileSystemView.getFileSystemView().getSystemIcon(path.toFile());
            }
            catch (Exception e) {
                System.out.print(e.getMessage());
            }   
            
        }
    }
    
    public void updateByFile() {
        init(file.toPath());
    }
    
    @Override
    public String toString() {
        if (path != null) {
            return path.toString();
        }
        else {
            return null;
        }
    }
    
    private static String parseExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension.toUpperCase();
    }
    
    private static String parseAttribute(boolean r, boolean w, boolean h) {
        String attr = "";
        attr += !r ? "R" : "-";
        attr += !w ? "W" : "-";
        attr += h ? "H" : "-";
        return attr;
    }
}
