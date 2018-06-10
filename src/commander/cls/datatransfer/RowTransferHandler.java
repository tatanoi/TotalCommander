/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.datatransfer;

import commander.cls.FileModel;
import commander.cls.TablePanel;
import commander.cls.controller.AppController;
import commander.cls.controller.DataController;
import commander.cls.file.FileInfo;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 *
 * @author Nam
 */
public class RowTransferHandler extends TransferHandler {
    
    private TablePanel panel = null;
    
    private DataFlavor flavor = new DataFlavor(ArrayList.class, "ArrayList");

    public RowTransferHandler(TablePanel panel) {
       this.panel = panel;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == panel.getTable());
        
        int[] selectedRows = panel.getTable().getSelectedRows();
        ArrayList<Integer> listInteger = new ArrayList<>();
        FileModel model = (FileModel)panel.getTable().getModel();
        for (int i = 0; i < selectedRows.length; i++) {
            if (model.getRow(selectedRows[i]).isReadable) {
                listInteger.add(selectedRows[i]);
            }
        }
        
        return new DataHandler(listInteger, flavor.getMimeType());
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        boolean b = info.isDrop();
        return b;
    }

    @Override
    public int getSourceActions(JComponent c) {
        DataController.getInstance().setSourcePanel(panel);
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        try {
            ArrayList<Integer> rows = (ArrayList<Integer>) info.getTransferable().getTransferData(flavor);
            DataController.getInstance().setDestinationPanel(panel);
            AppController.getInstance()
                    .getTransferPanel()
                    .showDialog(DataController.getInstance().srcPanel, panel, rows, 0);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
        if ((act == TransferHandler.MOVE) || (act == TransferHandler.NONE)) {
            panel.getTable().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
