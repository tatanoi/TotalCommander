/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.datatransfer;

import commander.cls.FileModel;
import commander.cls.file.FileInfo;
import commander.cls.datatransfer.FileInfoTransferable;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 *
 * @author Nam
 */
public class TableRowTransferHandler extends TransferHandler {
    
    public interface Reorderable {
        public void reorder(int fromIndex, int toIndex);
    }
    
   private final DataFlavor localObjectFlavor = new DataFlavor(FileInfo.class, "A FileInfo Object");
   private JTable           table             = null;

   public TableRowTransferHandler(JTable table) {
      this.table = table;
   }

   @Override
   protected Transferable createTransferable(JComponent c) {
      assert (c == table);
      return new DataHandler(table.getSelectedRow(), FileInfoTransferable.fileInfoFlavor.getMimeType());
   }

   @Override
   public boolean canImport(TransferHandler.TransferSupport info) {
        boolean b = info.getComponent() == table && info.isDrop();
        table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        return b;
   }

   @Override
   public int getSourceActions(JComponent c) {
      return TransferHandler.COPY_OR_MOVE;
   }
   

   @Override
   public boolean importData(TransferHandler.TransferSupport info) {
//      JTable target = (JTable) info.getComponent();
//      JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
//      int index = dl.getRow();
//      int max = table.getModel().getRowCount();
//      if (index < 0 || index > max)
//         index = max;
//      target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//      try {
//         Integer rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
//         if (rowFrom != -1 && rowFrom != index) {
//            ((Reorderable)table.getModel()).reorder(rowFrom, index);
//            if (index > rowFrom)
//               index--;
//            target.getSelectionModel().addSelectionInterval(index, index);
//            return true;
//         }
//      } catch (Exception e) {
//         e.printStackTrace();
//      }
//      return false;
        
        try {
            FileInfo row = (FileInfo) info.getTransferable().getTransferData(FileInfoTransferable.fileInfoFlavor);
            
            FileModel model = (FileModel)this.table.getModel();
            JTable target = (JTable) info.getComponent();
            FileModel targetModel = (FileModel)target.getModel();
            
            System.out.println("THIS = " + model.getRowCount());
            System.out.println("TARG = " + target.getRowCount());
            if (row != null) {
                model.addRow(row);
                return true;
                
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
   }

   @Override
   protected void exportDone(JComponent c, Transferable t, int act) {
      if ((act == TransferHandler.MOVE) || (act == TransferHandler.NONE)) {
         table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }
}

