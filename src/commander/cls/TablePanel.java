/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import commander.cls.controller.AppController;
import commander.cls.controller.DataController;
import commander.cls.datatransfer.RowTransferHandler;
import commander.cls.file.FileInfo;
import commander.cls.file.FileUtils;
import commander.cls.file.History;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 *
 * @author Nam
 */
public class TablePanel extends javax.swing.JPanel {
    
    private TablePanel thisPanel;
    private Path currentPath;
    private History history;
    private boolean isDone = false;
    private LoadDirectoryThread threadStop; // change directory thread
    
    /** For jCombobox1 */
    private int previousIndex; // previous combobox index
    private ActionListener comboActionListener;
    
    public TablePanel() {
        initComponents();
        thisPanel = this;
        history = new History();
        if (DataController.getInstance().srcPanel == null) {
            DataController.getInstance().srcPanel = this;
        }
        setupFocus();
        setupTable();
        setupCombobox();
        setupNavigationBar();
        AppController.getInstance().addPanel(this);
    }
    
    public void setupFocus() {
        FocusAdapter focusAdapter = new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                DataController.getInstance().setSourcePanel(thisPanel);
            }
        };
        
        btnBack.addFocusListener(focusAdapter);
        btnBackward.addFocusListener(focusAdapter);
        btnForward.addFocusListener(focusAdapter);
        btnReload.addFocusListener(focusAdapter);
        btnRoot.addFocusListener(focusAdapter);
        comboBoxDirectory.addFocusListener(focusAdapter);
        jTextField1.addFocusListener(focusAdapter);
        jTable1.addFocusListener(focusAdapter);
    }
    
    public JTable getTable() {
        return jTable1;
    }
    
    public JPanel getPanelFocus() {
        return panelFocus;
    }
    
    public Path getPath() {
        return currentPath;
    }
    
    public boolean getIsDone() {
        return isDone;
    }
    
    public void setStatus(boolean done) {
        if (!done) {
            labelStatus.setText("Loading...");
            labelStatus.setForeground(Color.red);
        }
        else {
            labelStatus.setText("Done");
            labelStatus.setForeground(Color.decode("#188203"));
        }
        isDone = done;
    }
    
    public void reload() {
        changeDirectory(currentPath, true);
    }
    
    public void setupTable() {
        
        jTable1.setModel(new FileModel());
        jTable1.getColumnModel().getColumn(0).setCellRenderer(new IconCellRenderer());
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new CustomCellRenderer());
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new CustomCellRenderer());
        jTable1.getColumnModel().getColumn(3).setCellRenderer(new CustomCellRenderer());
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new CustomCellRenderer());
        
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(200);
        
        jTable1.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getButton() == MouseEvent.BUTTON1 &&
                        mouseEvent.getClickCount() == 2 && 
                        table.getSelectedRow() != -1) {
                    FileModel model = (FileModel)table.getModel();
                    row = table.convertRowIndexToModel(row);
                    FileInfo f = model.getRow(row);
                    if (f.isReadable) {
                        if (f.isDirectory) {
                            changeDirectory(f.path);
                        }
                        else {
                            try {
                                Desktop.getDesktop().open(f.file);
                            }
                            catch (Exception ex) { ex.printStackTrace(); }
                        }
                    }
                }
                else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                    ArrayList<FileInfo> listItem = DataController.getInstance().getListItem();
                    FileModel model = (FileModel)table.getModel();
                    FileInfo data = model.getRow(table.convertRowIndexToModel(row));
                    if (data.isReadable) {
                        if (listItem.contains(data)) {
                            listItem.remove(data);
                            model.fireTableDataChanged();
                            System.out.println("OUT");
                        } else {
                            listItem.add(data);
                            model.fireTableDataChanged();
                            System.out.println("IN");
                        }
                    }
                }
            }
        });
        jTable1.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                JTable table =(JTable) e.getSource();
                int row = table.getSelectedRow();
                if (row != -1); {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        FileModel model = (FileModel)table.getModel();
                        row = table.convertRowIndexToModel(row);
                        FileInfo f = model.getRow(row);
                        if (f.isDirectory) {
                            changeDirectory(f.path);
                        }
                        else {
                            try {
                                Desktop.getDesktop().open(f.file);
                            }
                            catch (Exception ex) { }
                        }
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_F12) {
                        FileModel model = (FileModel)table.getModel();
                        row = table.convertRowIndexToModel(row);
                        FileInfo f = model.getRow(row);
                        if (f.isReadable && f.isWriteable) {
                            model.setCellEditable(row, 0, true);
                            model.setValueAt(model.getValueAt(row, 0), row, 0);
                        }
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        int[] selectedRows = table.getSelectedRows();
                        ArrayList<Integer> convertRows = new ArrayList<>();
                        for (int i = 0; i < selectedRows.length; i++) {
                            convertRows.add(table.convertRowIndexToModel(selectedRows[i]));
                        }
                        Collections.sort(convertRows, Collections.reverseOrder());
                        FileModel model = (FileModel)table.getModel();
                        for (int i = 0; i < convertRows.size(); i++) {
                            final int r = convertRows.get(i);
                            DataController.getInstance().deleteFile(model.getRow(r), () -> {
                                model.removeRow(r);
                            });
                        }
                    }
                }
            }
        });
        jTable1.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                ((JTable)e.getSource()).clearSelection();
            }
        });
        
        // Change how enter work - disable auto moving down one row when press "Enter"
        InputMap inputMap = jTable1.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "Back");
        
        ActionMap actionMap = jTable1.getActionMap();
        actionMap.put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        });
        actionMap.put("Back", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (currentPath.getNameCount() > 0) {
                    changeDirectory(currentPath.getParent());
                }
            }
        });
        
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setFillsViewportHeight(true);
        jTable1.setDragEnabled(true);
        jTable1.setDropMode(DropMode.INSERT_ROWS);
        jTable1.setTransferHandler(new RowTransferHandler(this));
    }
    
    public void setupCombobox() {
       
        comboActionListener = (ActionEvent e) -> {
            JComboBox comboBox = (JComboBox)e.getSource();
            ComboItem item = (ComboItem)comboBox.getSelectedItem();
            RootInfo fileInfo = (RootInfo)item.getValue();
            
            if (!changeDirectory(fileInfo.path)) {
                comboBox.setSelectedIndex(previousIndex);
            } 
        };
        
        comboBoxDirectory.removeAllItems();
        comboBoxDirectory.addActionListener(comboActionListener);
        
        Iterable<Path> roots = FileSystems.getDefault().getRootDirectories();
        roots.forEach(root -> comboBoxDirectory.addItem(new ComboItem(root.toString(), new RootInfo(root))));
    }
    
    public void setupNavigationBar() {
        jTextField1.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                JTextField textField = (JTextField)e.getSource();
                try {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        Path path = Paths.get(textField.getText());
                        if (Files.exists(path)) {
                            path = path.toAbsolutePath();
                        }
                        changeDirectory(path);
                        textField.setText(currentPath.toString());
                    } 
                }
                catch (Exception ex) {
                    textField.setText(currentPath.toString());
                }
            }
        });
    }
    
    public boolean changeDirectory(Path path) {
        return changeDirectory(path, false);
    }
    
    public boolean changeDirectory(Path path, boolean isReload) {
        if (path == null) { return false; }
        if (!isReload && this.currentPath != null && (this.currentPath.toString().equals(path.toString()) 
            || !Files.exists(path) || !Files.isDirectory(path) || !Files.isReadable(path))) {
            return false;
        }
        
        if (threadStop != null) {
            threadStop.stopRequest();
            try {
                Thread.currentThread().sleep(10);
            } catch (Exception e) { }
            
        } 
        
        this.currentPath = path;
        this.history.add(path);
        this.jTextField1.setText(currentPath.toString());
        
        // For combobox
        comboBoxDirectory.removeActionListener(comboActionListener);
        for (int i = 0; i < comboBoxDirectory.getItemCount(); i++) {
            if (comboBoxDirectory.getItemAt(i).getKey().equals(path.getRoot().toString())) {
                comboBoxDirectory.setSelectedIndex(i);
                previousIndex = i;
                updateDriveSizeText();
                break;
            }
        }
        comboBoxDirectory.addActionListener(comboActionListener);
        
        this.threadStop = new LoadDirectoryThread(this, path);
        
        Thread thread = new Thread(threadStop);
        thread.start();
        return true;
    }
    
    public void updateDriveSizeText() {
        ComboItem item = (ComboItem)comboBoxDirectory.getSelectedItem();
        RootInfo fileInfo = (RootInfo)item.getValue();
        jLabel1.setText("Drive size: " + FileUtils.convertSize(fileInfo.size, AppController.getInstance().getSizeUnit()) + 
                " " + AppController.getInstance().getSizeUnitSymbol());
    }
    
    public boolean changeDirectoryByHistory(Path path) {
        
        if (path == null) { return false; }
        if (this.currentPath != null && (currentPath.compareTo(path) == 0
            || !Files.exists(path) || !Files.isDirectory(path) || !Files.isReadable(path))) {
            return false;
        }
        
        if (threadStop != null) {
            threadStop.stopRequest();
            try {
                Thread.currentThread().sleep(10);
            } catch (Exception e) { }
            
        } 
        
        this.currentPath = path;
        this.jTextField1.setText(currentPath.toString());
        // For combobox
        comboBoxDirectory.removeActionListener(comboActionListener);
        for (int i = 0; i < comboBoxDirectory.getItemCount(); i++) {
            if (comboBoxDirectory.getItemAt(i).getKey().equals(path.getRoot().toString())) {
                comboBoxDirectory.setSelectedIndex(i);
                previousIndex = i;
                updateDriveSizeText();
                break;
            }
        }
        comboBoxDirectory.addActionListener(comboActionListener);
        
        this.threadStop = new LoadDirectoryThread(this, path);
        
        
        Thread thread = new Thread(threadStop);
        thread.start();
        return true;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        comboBoxDirectory = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        labelStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnBackward = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        btnRoot = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        panelFocus = new javax.swing.JPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 28));
        jPanel2.setMinimumSize(new java.awt.Dimension(0, 28));
        jPanel2.setPreferredSize(new java.awt.Dimension(25, 28));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        comboBoxDirectory.setMaximumSize(new java.awt.Dimension(75, 25));
        comboBoxDirectory.setMinimumSize(new java.awt.Dimension(75, 25));
        comboBoxDirectory.setName(""); // NOI18N
        comboBoxDirectory.setPreferredSize(new java.awt.Dimension(75, 25));
        jPanel2.add(comboBoxDirectory);

        jPanel3.setMaximumSize(new java.awt.Dimension(5, 32767));
        jPanel3.setMinimumSize(new java.awt.Dimension(5, 100));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel3);

        jLabel1.setText("Drive size");
        jLabel1.setMaximumSize(new java.awt.Dimension(32767, 16));
        jLabel1.setMinimumSize(new java.awt.Dimension(100, 16));
        jPanel2.add(jLabel1);

        jPanel4.setMaximumSize(new java.awt.Dimension(75, 32767));
        jPanel4.setMinimumSize(new java.awt.Dimension(75, 100));

        labelStatus.setText("Loading...");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 75, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(labelStatus)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(labelStatus)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel2.add(jPanel4);

        add(jPanel2);

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 28));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 28));
        jPanel1.setPreferredSize(new java.awt.Dimension(25, 28));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        btnBackward.setText("<");
        btnBackward.setBorder(null);
        btnBackward.setFocusPainted(false);
        btnBackward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBackward.setMaximumSize(new java.awt.Dimension(25, 25));
        btnBackward.setMinimumSize(new java.awt.Dimension(25, 25));
        btnBackward.setPreferredSize(new java.awt.Dimension(25, 25));
        btnBackward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBackward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackwardActionPerformed(evt);
            }
        });
        jPanel1.add(btnBackward);

        btnForward.setText(">");
        btnForward.setBorder(null);
        btnForward.setFocusPainted(false);
        btnForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnForward.setMaximumSize(new java.awt.Dimension(25, 25));
        btnForward.setMinimumSize(new java.awt.Dimension(25, 25));
        btnForward.setPreferredSize(new java.awt.Dimension(25, 25));
        btnForward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardActionPerformed(evt);
            }
        });
        jPanel1.add(btnForward);

        btnReload.setText("@");
        btnReload.setBorder(null);
        btnReload.setFocusPainted(false);
        btnReload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReload.setMaximumSize(new java.awt.Dimension(25, 25));
        btnReload.setMinimumSize(new java.awt.Dimension(25, 25));
        btnReload.setPreferredSize(new java.awt.Dimension(25, 25));
        btnReload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });
        jPanel1.add(btnReload);

        btnBack.setText("^");
        btnBack.setBorder(null);
        btnBack.setFocusPainted(false);
        btnBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBack.setMaximumSize(new java.awt.Dimension(25, 25));
        btnBack.setMinimumSize(new java.awt.Dimension(25, 25));
        btnBack.setPreferredSize(new java.awt.Dimension(25, 25));
        btnBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        jPanel1.add(btnBack);

        jTextField1.setMinimumSize(new java.awt.Dimension(14, 25));
        jTextField1.setPreferredSize(new java.awt.Dimension(73, 25));
        jPanel1.add(jTextField1);

        btnRoot.setText("*");
        btnRoot.setBorder(null);
        btnRoot.setFocusPainted(false);
        btnRoot.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRoot.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRoot.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRoot.setPreferredSize(new java.awt.Dimension(25, 25));
        btnRoot.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRoot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRootActionPerformed(evt);
            }
        });
        jPanel1.add(btnRoot);

        add(jPanel1);

        jScrollPane1.setToolTipText("");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Extension", "Size", "Date", "Attr"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        add(jScrollPane1);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 2));
        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanel5.setMinimumSize(new java.awt.Dimension(100, 5));
        jPanel5.setPreferredSize(new java.awt.Dimension(433, 5));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        panelFocus.setMaximumSize(new java.awt.Dimension(32767, 5));
        panelFocus.setMinimumSize(new java.awt.Dimension(100, 5));
        panelFocus.setPreferredSize(new java.awt.Dimension(433, 5));

        javax.swing.GroupLayout panelFocusLayout = new javax.swing.GroupLayout(panelFocus);
        panelFocus.setLayout(panelFocusLayout);
        panelFocusLayout.setHorizontalGroup(
            panelFocusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 428, Short.MAX_VALUE)
        );
        panelFocusLayout.setVerticalGroup(
            panelFocusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanel5.add(panelFocus);

        add(jPanel5);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackwardActionPerformed
        // TODO add your handling code here:
        changeDirectoryByHistory(history.backward());
    }//GEN-LAST:event_btnBackwardActionPerformed

    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        // TODO add your handling code here:
        changeDirectoryByHistory(history.forward());
    }//GEN-LAST:event_btnForwardActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        if (currentPath.getNameCount() > 0) {
            changeDirectory(currentPath.getParent());
        }
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
        // TODO add your handling code here:
        reload();
    }//GEN-LAST:event_btnReloadActionPerformed

    private void btnRootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRootActionPerformed
        // TODO add your handling code here:
        changeDirectory(currentPath.getRoot());
    }//GEN-LAST:event_btnRootActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnBackward;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnRoot;
    private javax.swing.JComboBox<ComboItem> comboBoxDirectory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JPanel panelFocus;
    // End of variables declaration//GEN-END:variables
}
