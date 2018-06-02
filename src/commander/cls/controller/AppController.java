/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.controller;

import commander.MainJFrame;
import commander.cls.file.FileInfo;
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
    
    public AppController() {
        listItem = new ArrayList<>();
        transferPanel = new TransferPanel();
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
}
