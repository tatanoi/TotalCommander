/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.controller;

import commander.MainJFrame;
import commander.cls.TablePanel;
import commander.cls.file.FileInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author Nam
 */
public class AppController {
    
    /* Singleton setup */
    
    private static AppController instance;
    
    public static AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }
    
    /* Working functions ----------------------------------------------------------------*/
    
    private MainJFrame parent;
    private ArrayList<FileInfo> listItem;    // List item we're going to transfer
    private TransferPanel transferPanel;
    private boolean isShowHidden;
            
    private final String fileName = "localCache.ini";
    private final String defaultPath = "C:/";
    
    private ArrayList<String> listPath;
    private ArrayList<TablePanel> listPanel;
    
    public AppController() {
        listItem = new ArrayList<>();
        listPanel = new ArrayList<>();
        listPath = new ArrayList<>();
        transferPanel = new TransferPanel();
        readFile();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    writeFile();  
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }
    
    public void addPanel(TablePanel panel) {
        int index = listPanel.size();
        listPanel.add(panel);
        listPanel.get(index).changeDirectory(getPath(index));
    }
    
    public ArrayList<TablePanel> getListPanel() {
        return listPanel;
    }
  
    public void setParent(MainJFrame parent) {
        this.parent = parent;
    }
    
    public JFrame getParent() {
        return this.parent;
    }
    
    // Show all item we're going to transfer
    public void showTransferDialog() {
        transferPanel.showDialog(DataController.getInstance().getListItem(), "AAA", 0);
    }
    
    public TransferPanel getTransferPanel() {
        return transferPanel;
    }
    
    // Add item that we're going to transfer
    public void addItem(FileInfo item) {
        listItem.add(item);
    }
    
    // Clear item from list
    public void clearItems() {
        listItem.clear();
    }
    
    public Path getPath(int index) {
        if (listPath.isEmpty() || index < 0 || index > listPath.size()) {
            return Paths.get(defaultPath);
        } 
         
        Path path = Paths.get(listPath.get(index));
        if (Files.exists(path) && Files.isDirectory(path) && Files.isReadable(path)) {
            return path;
        }
        return Paths.get(defaultPath);
    }
    
    public void readFile() {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    listPath.add(line.trim());
                    System.out.println(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void writeFile() {
        File file = new File(fileName);
        try {
            Files.deleteIfExists(file.toPath());
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < listPanel.size(); i++) {
                writer.write(listPanel.get(i).getPath().toString());
                System.out.println(listPanel.get(i).getPath().toString());
                if (i != listPanel.size() - 1) {
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException ignored) { }
    }
    
    public void setShowHidden(boolean hidden) {
        if (isShowHidden != hidden) {
            isShowHidden = hidden;
            listPanel.forEach(p -> p.reload());
        }
    }
    
    public boolean getShowHidden() {
        return isShowHidden;
    }
}
