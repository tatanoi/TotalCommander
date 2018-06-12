package commander.cls.file;

import commander.cls.Enums;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MyFileUtils {

    private static final String NullString = "<NUL>";

    public static String getName(Path path) {
        try {
            return path.getFileName().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NullString;
    }

    public static boolean isArchive(File f) {
        int fileSignature = 0;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, "r");
            fileSignature = raf.readInt();
            raf.close();
        } catch (IOException e) {
            // handle if you like
        }
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }
    
    public static String getName(String stringPath) {
        return MyFileUtils.getName(Paths.get(stringPath));
    }

    public static String getExtension(Path path) {

        String extension = "";
        if (Files.isDirectory(path)) {
            String stringPath = path.toString();
            int i = stringPath.lastIndexOf('.');
            int p = Math.max(stringPath.lastIndexOf('/'), stringPath.lastIndexOf('\\'));

            if (i > p) {
                extension = stringPath.substring(i+1);
            }
        }
        return extension;
    }

    public static String getExtension(String stringPath) {
        return  MyFileUtils.getExtension(Paths.get(stringPath));
    }

    public static String getSize(Path path) {
        try {
            return Files.isDirectory(path) ? "<DIR>" : String.valueOf(Files.size(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NullString;
    }

    public static String getSize(String stringPath) {
        return MyFileUtils.getSize(Paths.get(stringPath));
    }

    public static String getLastModifiedDate(Path path) {
        try {
            FileTime fileTime = Files.getLastModifiedTime(path);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.format(fileTime.toMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NullString;
    }

    public static String getLastModifiedDate(String stringPath) {
        return MyFileUtils.getLastModifiedDate(Paths.get(stringPath));
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
   
    public static long convertSize(long bytes, Enums.SizeUnit unit) {
        if (bytes == -1) { 
            return bytes; 
        }
        
        switch (unit) {
            case Byte: return (long)Math.round((double)bytes);
            case KiloByte: return (long)Math.round((double)bytes / 1024);
            case MegaByte: return (long)Math.round((double)bytes / 1024 / 1024); 
            case GigaByte: return (long)Math.round((double)bytes / 1024 / 1024 / 1024);
        }
        return bytes;
    }
}
