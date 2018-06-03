/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import commander.cls.controller.DataController;
import commander.cls.file.FileInfo;
import java.io.File;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Nam
 */
public class FileModel extends AbstractTableModel {

    private ArrayList<FileInfo> files;
    private ArrayList<boolean[]> editableCells;
    private boolean isInEditing;
    
    public FileModel() {
        this.files = new ArrayList<>();
        this.editableCells = new ArrayList<>();
        this.isInEditing = false;
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
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Name";
            case 1: return "Ext";
            case 2: return "Size";
            case 3: return "Date";
            case 4: return "Attr";
        }
        return "UNKNOWN";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = "??";
        FileInfo fileInfo = files.get(rowIndex);
        switch (columnIndex) {
            case 0: value = fileInfo.name; break;
            case 1: value = fileInfo.extension; break;
            case 2: value = fileInfo.size > 0 ? fileInfo.size : null; break;
            case 3: value = fileInfo.lastModified; break;
            case 4: value = fileInfo.attribute; break;
        }
        return value;
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0: return String.class;
            case 1: return String.class;
            case 2: return Long.class;
            case 3: return String.class;
            case 4: return String.class;
        }
        return Object.class;
    }
    
    @Override
    public boolean isCellEditable(int row, int column) { // custom isCellEditable function
        return this.editableCells.get(row)[column];
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            // throw exception
            return;
        }
        if (columnIndex < 0 || columnIndex >= getColumnCount()) {
           // throw exception
           return;
        }

        FileInfo fileInfo = this.getRow(rowIndex);
        switch (columnIndex) {
        case 0:
            File renameFile = DataController.getInstance().renameFile(fileInfo, String.valueOf(aValue));
            if (renameFile != null) {
                this.files.set(rowIndex, new FileInfo(renameFile.toPath()));
            }
        }
        
        setCellEditable(rowIndex, columnIndex, isInEditing = !isInEditing);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void setCellEditable(int row, int col, boolean value) {
        this.editableCells.get(row)[col] = value; // set cell true/false
        this.fireTableCellUpdated(row, col);
    }
    
    public FileInfo getRow(int rowIndex) {
        return files.get(rowIndex);
    }
    
    public void removeRow(int index) {
        this.files.remove(index);
        this.editableCells.remove(index);
        this.fireTableRowsDeleted(index, index);
    }
    
    public void addRow(FileInfo fileInfo) {
        this.files.add(fileInfo);
        this.editableCells.add(new boolean[getColumnCount()]);
        this.fireTableRowsInserted(files.size() - 1, files.size() - 1);
    }
    
    public void clear() {
        this.files.clear();
        this.editableCells.clear();
        this.fireTableDataChanged();
    }
}
