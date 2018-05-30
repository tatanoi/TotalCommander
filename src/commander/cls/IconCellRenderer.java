/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import commander.cls.controller.DataController;
import commander.cls.file.FileInfo;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Nam
 */
public class IconCellRenderer extends DefaultTableCellRenderer {
    
    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {
        
        
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            this.setForeground(table.getSelectionForeground());
        }
        else {
            this.setBackground(table.getBackground());
            this.setForeground(table.getForeground());
        }
        
        FileModel model = (FileModel)table.getModel();
        FileInfo f = model.getRow(table.convertRowIndexToModel(row));
        if (!f.isReadable && !isSelected) {
            setForeground(Color.red);
        }
        if (f.isReadable && DataController.getInstance().getListItem().contains(f)) {
            setBackground(Color.red);
            setForeground(Color.white);
        }
        
        if (value != null) {
            this.setIcon(f.icon);
            this.setText(String.valueOf(value));
        }
        
        return this;
    }
}
