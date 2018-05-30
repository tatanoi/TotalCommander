/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import commander.cls.file.FileInfo;
import java.awt.Image;
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
public class RootInfo extends FileInfo {

    protected FileSystemView fsv;
    
    public RootInfo(Path path) {
        super(path);
    }
    
    @Override
    public void init(Path p) {
        path = p.toAbsolutePath();
        file = path.toFile();
        fsv = FileSystemView.getFileSystemView();
        
        if (!Files.notExists(p)) {
            name = fsv.getSystemDisplayName(file);
            isDirectory = true;
            isReadable = Files.isReadable(path);
            extension = fsv.getSystemTypeDescription(file);
            size = file.getTotalSpace();
        }
    }
}
