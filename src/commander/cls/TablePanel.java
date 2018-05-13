/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 *
 * @author Nam
 */
public class TablePanel extends javax.swing.JPanel {
    
    private Path currentPath;
    private LoadDirectoryThread threadStop; // change directory thread
    private int previousIndex; // previous combobox index
    
    /**
     * Creates new form TablePanel1
     */
    public TablePanel() {
        initComponents();
        setupTable();
        setupCombobox();
        changeDirectory(Paths.get("C:/"));
    }
    
    /** Setup table */
    public void setupTable() {
        
        jTable1.setModel(new FileModel());
        jTable1.getColumnModel().getColumn(0).setCellRenderer(new IconCellRenderer());
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(200);
        
        jTable1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    FileModel model = (FileModel)table.getModel();
                    row = table.convertRowIndexToModel(row);
                    changeDirectory(model.getRow(row).path);
                }
            }
        });
        jTable1.addKeyListener(new KeyAdapter() {
             public void keyPressed(KeyEvent e) {
                JTable table =(JTable) e.getSource();
                int row = table.getSelectedRow();
                if (e.getKeyCode() == KeyEvent.VK_ENTER && row != -1) {
                    FileModel model = (FileModel)table.getModel();
                    row = table.convertRowIndexToModel(row);
                    changeDirectory(model.getRow(row).path);
                }
             }
        });
        jTable1.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                ((JTable)e.getSource()).clearSelection();
            }
        });
        
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setDragEnabled(true);
        jTable1.setDropMode(DropMode.INSERT_ROWS);
        jTable1.setTransferHandler(new TableRowTransferHandler(jTable1)); 
    }
    
    public void setupCombobox() {
        
        jComboBox1.removeAllItems();
        jComboBox1.addActionListener(e -> {
            JComboBox comboBox = (JComboBox)e.getSource();
            ComboItem item = (ComboItem)comboBox.getSelectedItem();
            RootInfo fileInfo = (RootInfo)item.getValue();
            if (!changeDirectory(fileInfo.path)) {
                comboBox.setSelectedIndex(previousIndex);
            } else {
                jLabel1.setText("Drive size: " + fileInfo.size);
                previousIndex = comboBox.getSelectedIndex();
            }
        });
        
        Iterable<Path> roots = FileSystems.getDefault().getRootDirectories();
        roots.forEach(root -> jComboBox1.addItem(new ComboItem(root.toString(), new RootInfo(root))));
    }
    
    public void setupNavigationBar() {
        
    }
    
    /** Change directory using thread to make loading smoother */
    public boolean changeDirectory(Path path) {
        
        if (this.currentPath != null && (this.currentPath.toString().equals(path.toString()) || !Files.isReadable(path))) {
            return false;
        }
        
        if (threadStop != null) {
            threadStop.stopRequest();
            try {
                Thread.currentThread().sleep(10);
            } catch (Exception e) { }
            
        } 
        
        this.currentPath = path;
        this.threadStop = new LoadDirectoryThread(jTable1, path);
        
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

        jPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnBackward = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 28));
        jPanel2.setMinimumSize(new java.awt.Dimension(0, 28));
        jPanel2.setPreferredSize(new java.awt.Dimension(25, 28));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jComboBox1.setMaximumSize(new java.awt.Dimension(75, 25));
        jComboBox1.setMinimumSize(new java.awt.Dimension(75, 25));
        jComboBox1.setName(""); // NOI18N
        jComboBox1.setPreferredSize(new java.awt.Dimension(75, 25));
        jPanel2.add(jComboBox1);

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
        jPanel2.add(jLabel1);

        add(jPanel2);

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 28));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 28));
        jPanel1.setPreferredSize(new java.awt.Dimension(25, 28));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        btnBackward.setText("<");
        btnBackward.setBorder(null);
        btnBackward.setFocusable(false);
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
        btnForward.setFocusable(false);
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

        btnBack.setText("^");
        btnBack.setBorder(null);
        btnBack.setFocusable(false);
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
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackwardActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnBackwardActionPerformed

    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnForwardActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        if (currentPath.getNameCount() > 0) {
            changeDirectory(currentPath.getParent());
        }
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnBackward;
    private javax.swing.JButton btnForward;
    private javax.swing.JComboBox<ComboItem> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
