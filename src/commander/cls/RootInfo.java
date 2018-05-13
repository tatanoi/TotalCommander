/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Nam
 */
public class RootInfo extends FileInfo {

    protected FileSystemView fsv;
    protected File file;
    
    public RootInfo() {
        super();
    }

    public RootInfo(String stringPath) {
        super(stringPath);
    }

    public RootInfo(Path path) {
        super(path);
    }

    
    @Override
    public void initialize(Path path) {
        this.path = path;
        this.fsv = FileSystemView.getFileSystemView();
        this.file = path.toFile();
        
        if (!Files.notExists(path)) {
            this.name = fsv.getSystemDisplayName(file);
            this.isDirectory = true;
            this.isReadable = Files.isReadable(path);
            this.extension = fsv.getSystemTypeDescription(file);
            this.size = String.format("%d", file.getTotalSpace());
            
            try {
                this.icon = (ImageIcon)fsv.getSystemIcon(file);
                Image image = this.icon.getImage(); // transform it 
                Image newimg = image.getScaledInstance(14, 14,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
                this.icon = new ImageIcon(newimg);
            } catch (Exception ignored) {}
            
        }
    }
}
