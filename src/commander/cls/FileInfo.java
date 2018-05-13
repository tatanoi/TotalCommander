package commander.cls;

import java.awt.Image;
import java.nio.file.FileSystem;
import javafx.scene.image.ImageView;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;


public class FileInfo {

    public Path path;
    public String name;
    public String extension;
    public String size;
    public String lastModified;
    public String attribute;
    public ImageIcon icon;

    public boolean isDirectory;
    public boolean isReadable;

    public FileInfo() {
        this.name = "";
        this.extension = "";
        this.size = "";
        this.lastModified = "";
        this.attribute = "";
        this.icon = null;
    }

    public FileInfo(String stringPath) {
        initialize(stringPath);
    }

    public FileInfo(Path path) {
        initialize(path);
    }

    public void initialize(String stringPath) {
        initialize(Paths.get(stringPath));
    }

    public void initialize(Path path) {
        this.path = path;
        if (!Files.notExists(path)) {
            this.name = FileUtils.getName(this.path);
            this.isDirectory = Files.isDirectory(path);
            this.isReadable = Files.isReadable(path);
            this.extension = FileUtils.getExtension(this.path);
            this.size = FileUtils.getSize(this.path);
            this.lastModified = FileUtils.getLastModifiedDate(this.path);
            this.attribute = "Later";
            
            try {
                this.icon = (ImageIcon)FileSystemView.getFileSystemView().getSystemIcon(path.toFile());
                Image image = this.icon.getImage(); // transform it 
                Image newimg = image.getScaledInstance(14, 14,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
                this.icon = new ImageIcon(newimg);
            } catch (Exception ignored) {}
            
        }
    }
}
