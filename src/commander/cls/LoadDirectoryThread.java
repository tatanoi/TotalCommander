/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import commander.cls.file.FileInfo;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JTable;

/**
 *
 * @author Nam
 */
public class LoadDirectoryThread implements Runnable {
        
    public LoadDirectoryThread(TablePanel panel, Path path) {
        this.panel = panel;
        this.path = path;
        this.table = panel.getTable();
    }

    private volatile boolean stopRequested;
    private TablePanel panel;
    private Thread runThread;
    private JTable table;
    private Path path;

    public void run() {
        runThread = Thread.currentThread();
        stopRequested = false;
        panel.setStatus(false);
        FileModel model = (FileModel)table.getModel();
        model.clear();
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(null);
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path p : directoryStream) {
                model.addRow(new FileInfo(p));
                if (stopRequested) {
                    break;
                }
            }
            table.setAutoCreateRowSorter(true);
            panel.setStatus(true);
            System.out.println(path.toString() + " : " + model.getRowCount());
        } catch (Exception ignored) { 
            ignored.printStackTrace();
        }
    }

    public void stopRequest() {
        stopRequested = true;
        if (runThread != null) {
            runThread.interrupt();
        }
    }
}
