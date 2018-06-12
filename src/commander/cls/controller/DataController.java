/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.controller;

import commander.cls.FileModel;
import commander.cls.TablePanel;
import commander.cls.file.FileInfo;
import commander.cls.file.MyFileUtils;
import commander.cls.file.UnZip;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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
            JOptionPane.showMessageDialog(null, 
                    "Oops, something must be wrong. Check whether file / folder exists or permissison restrict", 
                    "Error occur", 
                    JOptionPane.ERROR_MESSAGE);
            desPanel.reload();
        }
    }
    
    public void moveFile(FileInfo src, File dst, Runnable toRun) {
        try
        {
            boolean isDesExist = Files.exists(dst.toPath());
            if (src.isReadable) {
                if (src.isDirectory) {
                    FileUtils.moveDirectoryToDirectory(src.file, dst.getParentFile(), true);
                }
                else {
                    FileUtils.moveFileToDirectory(src.file, dst.getParentFile(), true);
                }
                if (!isDesExist) {
                    toRun.run();
                }
            }
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, 
                    "Oops, something must be wrong. Check whether file / folder exists or permissison restrict", 
                    "Error occur", 
                    JOptionPane.ERROR_MESSAGE);
            desPanel.reload();
        }
    }
    
    public void deleteFile(FileInfo src, Runnable toRun) {
        try
        {
            if (src.isReadable) {
                if (src.isFile) {
                    FileUtils.forceDelete(src.file);
                }
                else {
                    FileUtils.deleteDirectory(src.file);
                }
                toRun.run();
            }
        }
        catch (Exception ex)
        {
//            try {
//                ZipFile z = new ZipFile(src.file);
//                z.close();
//                FileUtils.forceDelete(src.file);
//                toRun.run();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Oops, something must be wrong. Check whether file / folder exists or permissison restrict", 
                    "Error occur", 
                    JOptionPane.ERROR_MESSAGE);
            desPanel.reload();
        }
    }
    
    public void deleteFileFromSourcePanel() {
        JTable table = getSourcePanel().getTable();
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length > 0) {
            ArrayList<Integer> convertRows = new ArrayList<>();
            for (int i = 0; i < selectedRows.length; i++) {
                convertRows.add(table.convertRowIndexToModel(selectedRows[i]));
            }
            Collections.sort(convertRows, Collections.reverseOrder());
            FileModel model = (FileModel)table.getModel();
            for (int i = 0; i < convertRows.size(); i++) {
                final int r = convertRows.get(i);
                DataController.getInstance().deleteFile(model.getRow(r), () -> {
                    model.removeRow(r);
                });
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "You select nothing", "Warning", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(null, 
                    "Oops, something must be wrong. Check whether file / folder exists or permissison restrict", 
                    "Error occur", 
                    JOptionPane.ERROR_MESSAGE);
            desPanel.reload();
            return null;
        }
    }
    
    public File renameFile(FileInfo src, String newName, String extension) {
        try {
            Path des = null;
            String name = newName;
            int index = 0;
            extension = src.isDirectory ? "" : extension;
            do {
                des = Paths.get(src.file.getParent(), name + extension);
                name = newName + " (" + ++index + ")";
            } while (Files.exists(des) && !Files.isSameFile(des, src.path));
           
            if (Files.isReadable(des)) {
                System.out.println(des.toString() + " permission");
                return null;
            } 
            else {
                File desFile = des.toFile();
                src.file.renameTo(desFile);
                return desFile;
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    "Oops, something must be wrong. Check whether file / folder exists or permissison restrict", 
                    "Error occur", 
                    JOptionPane.ERROR_MESSAGE);
            desPanel.reload();
            return null;
        }
    }
    
    public ArrayList<FileInfo> getListItem() {
        return listItem;
    }
    
    public void newTextDocument() {
        if (!srcPanel.getIsDone()) { 
            JOptionPane.showMessageDialog(null,
                    String.format("Please wait for finish loading.."),
                    "Please wait", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int number = 0;
        String textDocumentName = "New Text Document.txt";
        Path textDocumentPath = Paths.get(srcPanel.getPath().toString(), textDocumentName);
        
        while (Files.exists(textDocumentPath)) {
            number++;
            textDocumentName = String.format("New Text Document (%d).txt", number);
            textDocumentPath = Paths.get(srcPanel.getPath().toString(), textDocumentName);
        }
        
        try {
            Path newFile = Files.createFile(textDocumentPath);
            JTable table = srcPanel.getTable();
            FileModel model = (FileModel)table.getModel();
            model.addRow(new FileInfo(newFile));
            int row = model.getRowCount() - 1;
            table.scrollRectToVisible(table.getCellRect(row, 0, true));
            table.setRowSelectionInterval(row, row);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    String.format("Cannot create %s", textDocumentPath.toString()),
                    "Error creating file", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void newFolder() {
        if (!srcPanel.getIsDone()) { 
            JOptionPane.showMessageDialog(null,
                    String.format("Please wait for finish loading.."),
                    "Please wait", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int number = 0;
        String folderName = "New Folder";
        Path folderPath = Paths.get(srcPanel.getPath().toString(), folderName);
        
        while (Files.exists(folderPath)) {
            number++;
            folderName = String.format("New Folder (%d)", number);
            folderPath = Paths.get(srcPanel.getPath().toString(), folderName);
        }
        
        try {
            Path newFile = Files.createDirectory(folderPath);
            JTable table = srcPanel.getTable();
            FileModel model = (FileModel)table.getModel();
            model.addRow(new FileInfo(newFile));
            table.scrollRectToVisible(table.getCellRect(model.getRowCount() - 1, 0, true));
            table.setRowSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    String.format("Cannot create %s", folderPath.toString()),
                    "Error creating folder", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            if (children.length == 0) {
                ZipEntry zipEntry = new ZipEntry(fileName + "/");
                zipOut.putNextEntry(zipEntry);
                zipOut.closeEntry();
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
    
    public void unZipFile(File fileToUnZip) throws IOException {
        if (!MyFileUtils.isArchive(fileToUnZip)) {
            return;
        }
        
        TablePanel panel = DataController.getInstance().getSourcePanel();
        FileModel model = (FileModel)panel.getTable().getModel();
        
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileToUnZip));
        ZipEntry zipEntry = zis.getNextEntry();
        
        int start = fileToUnZip.getName().lastIndexOf(".zip");
        String folderName = fileToUnZip.getName().substring(0, start);
        
        StringBuilder builder = new StringBuilder();
        builder.append(fileToUnZip.getName().substring(0, start));
        
        while(zipEntry != null){
            String fileName = zipEntry.getName();
            Path o = Paths.get(fileToUnZip.getParent(), folderName, fileName);
            File newFile = new File(o.toString());
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        model.addRow(new FileInfo(Paths.get(fileToUnZip.getParent(), folderName)));
    }
    
    public void zipFileFromTable(String name) throws IOException {
        TablePanel panel = DataController.getInstance().getSourcePanel();
        FileModel model = (FileModel)panel.getTable().getModel();
        int[] rows = panel.getTable().getSelectedRows();
        
        if (rows.length > 0) {
            Path o;
            int index = 0;
            String zipName = name;
            do {
                o = Paths.get(panel.getPath().toString(), zipName + ".zip");
                zipName = name + " (" + ++index + ")";
            } while (Files.exists(o));
            
            FileOutputStream fos = new FileOutputStream(o.toString());
            ZipOutputStream zipOut = new ZipOutputStream(fos);      
            for (int i = 0; i < rows.length && rows[i] != -1; i++) {
                File f = model.getRow(panel.getTable().convertRowIndexToModel(rows[i])).file;
                model.getValueAt(panel.getTable().convertRowIndexToModel(rows[i]), 0);
                DataController.getInstance().zipFile(f, f.getName(), zipOut);
            }
            zipOut.close();
            fos.close();
            model.addRow(new FileInfo(o));
        }
    }
    
    public void zipFileFromTable(String name, int[] rows) throws IOException {
        TablePanel panel = DataController.getInstance().getSourcePanel();
        FileModel model = (FileModel)panel.getTable().getModel();
        
        if (rows.length > 0) {
            Path o;
            int index = 0;
            String zipName = name;
            do {
                o = Paths.get(panel.getPath().toString(), zipName + ".zip");
                zipName = name + " (" + ++index + ")";
            } while (Files.exists(o));
            
            FileOutputStream fos = new FileOutputStream(o.toString());
            ZipOutputStream zipOut = new ZipOutputStream(fos);      
            for (int i = 0; i < rows.length && rows[i] != -1; i++) {
                File f = model.getRow(panel.getTable().convertRowIndexToModel(rows[i])).file;
                model.getValueAt(panel.getTable().convertRowIndexToModel(rows[i]), 0);
                DataController.getInstance().zipFile(f, f.getName(), zipOut);
            }
            zipOut.close();
            fos.close();
            model.addRow(new FileInfo(o));
        }
    }
    
    public void unZipFileFromTable() throws IOException {
        TablePanel panel = DataController.getInstance().getSourcePanel();
        FileModel model = (FileModel)panel.getTable().getModel();
        int row = panel.getTable().getSelectedRow();
        
        if (row != -1) {
            File f = model.getRow(panel.getTable().convertRowIndexToModel(row)).file;
            if (MyFileUtils.isArchive(f)) {
                int start = f.getName().lastIndexOf(".zip");
                String folderName = start > 0 ? f.getName().substring(0, start) : f.getName();
                UnZip uz = new UnZip();
                uz.unZipIt(f.getPath(), Paths.get(f.getParent(), folderName).toString());
                model.addRow(new FileInfo(Paths.get(f.getParent(), folderName)));
            }
        }
    }
    
    public void unZipFileFromTable(String name, int row) throws IOException {
        TablePanel panel = DataController.getInstance().getSourcePanel();
        FileModel model = (FileModel)panel.getTable().getModel();
        
        if (row != -1) {
            File f = model.getRow(panel.getTable().convertRowIndexToModel(row)).file;
            if (MyFileUtils.isArchive(f)) {
//                int start = f.getName().lastIndexOf(".zip");
//                String folderName = start > 0 ? f.getName().substring(0, start) : f.getName();
                UnZip uz = new UnZip();
                uz.unZipIt(f.getPath(), Paths.get(f.getParent(), name).toString());
                model.addRow(new FileInfo(Paths.get(f.getParent(), name)));
            }
        }
    }
    
    public TablePanel srcPanel;
    public TablePanel desPanel;
    
    public void setDestinationPanel(TablePanel panel) {
        desPanel = panel;
    }
    
    public void setSourcePanel(TablePanel panel) {
        srcPanel.getPanelFocus().setBackground(panel.getBackground());
        srcPanel = panel;
        srcPanel.getPanelFocus().setBackground(Color.decode("#1c5ec9"));
    }
    
    public TablePanel getSourcePanel() {
        return srcPanel;
    }
    
    public TablePanel getDesPanel() {
        desPanel = AppController.getInstance().getListPanel().get(0);
        if (desPanel == getSourcePanel()) {
            desPanel = AppController.getInstance().getListPanel().get(1);
        }
        return desPanel;
    }
}
