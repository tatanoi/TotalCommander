package commander.cls;

import javafx.scene.image.ImageView;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileInfo {

    public Path path;
    public String name;
    public String extension;
    public String size;
    public String lastModified;
    public String attribute;
    public ImageView icon;

    public boolean isDirectory;
    public boolean isReadable;

    public FileInfo() {
        this.name = "";
        this.extension = "";
        this.size = "";
        this.lastModified = "";
        this.attribute = "";
        this.icon = new ImageView();
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

//            this.icon = FileUtils.getIcon(this.path);
        }
    }
}
