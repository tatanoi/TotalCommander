/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import java.awt.Rectangle;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Nam
 */
public class LoadDirectoryThread implements Runnable {
        
        public LoadDirectoryThread(JTable table, ArrayList<Path> paths, Path path) {
            this.table = table;
            this.paths = paths;
            this.path = path;
        }
        
         public LoadDirectoryThread(JTable table, ArrayList<Path> paths, Path path, JScrollPane scrollPane) {
            this.table = table;
            this.paths = paths;
            this.path = path;
            this.scrollPane = scrollPane;
        }
        
        private volatile boolean stopRequested;
        private Thread runThread;
        private JTable table;
        private ArrayList<Path> paths;
        private Path path;
        private JScrollPane scrollPane;
        
        public void run() {
            runThread = Thread.currentThread();
            stopRequested = false;
            FileModel model = (FileModel)table.getModel();
            model.clear();
            
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                for (Path p : directoryStream) {
                    paths.add(p);
                    model.addRow(new FileInfo(p));
                    if (stopRequested) {
                        break;
                    }
                }
                System.out.println(path.toString() + " : " + model.getRowCount());
            } catch (Exception ignored) { 
                ignored.printStackTrace();
            }
        }
        
//        public void run() {
//            runThread = Thread.currentThread();
//            stopRequested = false;
//            DefaultTableModel model = (DefaultTableModel)table.getModel();
//            model.setRowCount(0);
//            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
//                for (Path p : directoryStream) {
//                    paths.add(p);
//                    FileInfo fileInfo = new FileInfo(p);
//                    model.addRow(new Object[] {
//                        fileInfo.name,
//                        fileInfo.extension,
//                        fileInfo.size,
//                        fileInfo.lastModified,
//                        fileInfo.attribute
//                    });
//                    if (stopRequested) {
//                        break;
//                    }
//                }
//                System.out.println(path.toString() + " : " + model.getRowCount());
//            } catch (Exception ignored) { 
//                ignored.printStackTrace();
//            }
//        }
        
        public void stopRequest() {
            stopRequested = true;
            if (runThread != null) {
                runThread.interrupt();
            }
        }
    }
