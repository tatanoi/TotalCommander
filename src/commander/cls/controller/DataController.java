/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.controller;

import commander.cls.FileInfo;
import java.util.ArrayList;

/**
 *
 * @author Nam
 */
public class DataController {
    
    private static DataController instance;
    
    public DataController getInstance() {
        if (instance == null) {
            instance = new DataController();
        }
        return instance;
    }
    
    private ArrayList<FileInfo> listItem;
    
    public DataController() {
        listItem = new ArrayList<>();
    }
    
    public ArrayList<FileInfo> getListItem() {
        return listItem;
    }
}
