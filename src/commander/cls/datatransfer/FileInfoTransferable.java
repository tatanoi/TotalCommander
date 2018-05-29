/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.datatransfer;

import commander.cls.FileInfo;
import commander.cls.FileInfo;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author Nam
 */
public class FileInfoTransferable implements Transferable {

    public static DataFlavor fileInfoFlavor = new DataFlavor(FileInfo.class, "A FileInfo Object");
    public static DataFlavor[] supportedFlavors =  { fileInfoFlavor };
    FileInfo fileInfo;
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(fileInfoFlavor) || flavor.equals(DataFlavor.stringFlavor)) {
            return true;
        }
        return false;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(fileInfoFlavor)) {
            return fileInfo;
        }
        else if (flavor.equals(DataFlavor.stringFlavor)) {
            return "NULL";
        } 
        else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}
