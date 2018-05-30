/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.datatransfer;

import commander.cls.file.FileInfo;
import commander.cls.file.FileInfo;
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
public class FileTransferHandler extends TransferHandler {
    
    protected DataFlavor dataFlavor = new DataFlavor(FileInfo.class, "File-info");
    protected JTable table;
    
    public FileTransferHandler(JTable table) {
        this.table = table;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == table);
        return new DataHandler(table.getSelectedRow(), dataFlavor.getMimeType());
    } 
    
    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        boolean b = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(dataFlavor);
        table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        return b;
    }

    @Override
    public int getSourceActions(JComponent c) {
       return TransferHandler.COPY_OR_MOVE;
    }
}
