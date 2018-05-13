/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import java.awt.List;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Nam
 */
public class FileModel extends AbstractTableModel {

    private ArrayList<FileInfo> files;
    
    
    public FileModel() {
        this.files = new ArrayList<>();
    }
    
    
    public FileModel(ArrayList<FileInfo> files) {
        if (files != null) {
            this.files = files;
        } 
        else {
            this.files = new ArrayList<>();
        }
    }
    
    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = "??";
        FileInfo fileInfo = files.get(rowIndex);
        switch (columnIndex) {
            case 0: value = fileInfo.name; break;
            case 1: value = fileInfo.extension; break;
            case 2: value = fileInfo.size; break;
            case 3: value = fileInfo.lastModified; break;
            case 4: value = fileInfo.attribute; break;
        }
        return value;
    }
    
    
    public FileInfo getRow(int rowIndex) {
        return files.get(rowIndex);
    }
    
    public void addRow(FileInfo fileInfo) {
        this.files.add(fileInfo);
        this.fireTableRowsInserted(files.size() - 1, files.size() - 1);
    }
    
    public void clear() {
        this.files.clear();
        this.fireTableDataChanged();
    }
}
