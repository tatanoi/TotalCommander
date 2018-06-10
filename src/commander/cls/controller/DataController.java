/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.controller;

import commander.cls.FileModel;
import commander.cls.TablePanel;
import commander.cls.file.FileInfo;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
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
            JOptionPane.showMessageDialog(null, 
                    "Oops, something must be wrong. Check whether file / folder exists or permissison restrict", 
                    "Error occur", 
                    JOptionPane.ERROR_MESSAGE);
            desPanel.reload();
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
            model.setCellEditable(model.getRowCount() - 1, 0, true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    String.format("Cannot create %s", folderPath.toString()),
                    "Error creating folder", 
                    JOptionPane.ERROR_MESSAGE);
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
}
