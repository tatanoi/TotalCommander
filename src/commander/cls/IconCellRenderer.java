/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import sun.swing.DefaultLookup;

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
        
        if (value != null) {
            FileModel model = (FileModel)table.getModel();
            this.setIcon(model.getRow(row).icon);
            this.setText((String)value);
        }
        return this;
    }
}
