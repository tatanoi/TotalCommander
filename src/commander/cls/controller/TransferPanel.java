/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.controller;

import commander.cls.Enums;
import commander.cls.FileModel;
import commander.cls.TablePanel;
import commander.cls.file.FileInfo;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author Nam
 */
public class TransferPanel extends javax.swing.JPanel {

    private static TransferPanel instance;
    public static TransferPanel getInstance() {
        if (instance == null) {
            instance = new TransferPanel();
        }
        return instance;
    }
    /**
     * Creates new form TransferPanel
     */
    public TransferPanel() {
        initComponents();
        dialogTransfer.pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogTransfer = new javax.swing.JDialog();
        btnOK = new javax.swing.JButton();
        labelTransferItem = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbTransferOption = new javax.swing.JComboBox<>();
        textTransferDestination = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();

        dialogTransfer.setTitle("Transfer item...");
        dialogTransfer.setModal(true);
        dialogTransfer.setResizable(false);

        btnOK.setText("OK");
        btnOK.setMaximumSize(new java.awt.Dimension(69, 32));
        btnOK.setMinimumSize(new java.awt.Dimension(69, 32));
        btnOK.setPreferredSize(new java.awt.Dimension(69, 32));
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        labelTransferItem.setText("Transfer [FileName] to:");

        jLabel2.setText("As option:");

        cbTransferOption.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Copy", "Cut" }));
        cbTransferOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTransferOptionActionPerformed(evt);
            }
        });

        textTransferDestination.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textTransferDestinationActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogTransferLayout = new javax.swing.GroupLayout(dialogTransfer.getContentPane());
        dialogTransfer.getContentPane().setLayout(dialogTransferLayout);
        dialogTransferLayout.setHorizontalGroup(
            dialogTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogTransferLayout.createSequentialGroup()
                .addGroup(dialogTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(dialogTransferLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dialogTransferLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(dialogTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textTransferDestination, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(dialogTransferLayout.createSequentialGroup()
                                .addGroup(dialogTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(labelTransferItem))
                                .addGap(0, 343, Short.MAX_VALUE))
                            .addComponent(cbTransferOption, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(12, 12, 12))
        );
        dialogTransferLayout.setVerticalGroup(
            dialogTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogTransferLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(labelTransferItem)
                .addGap(14, 14, 14)
                .addComponent(textTransferDestination, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbTransferOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(dialogTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        // TODO add your handling code here:
        if (transferCallback != null) {
            transferCallback.run();
        }
        dialogTransfer.setVisible(false);
    }//GEN-LAST:event_btnOKActionPerformed

    private void cbTransferOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTransferOptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbTransferOptionActionPerformed

    private void textTransferDestinationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textTransferDestinationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textTransferDestinationActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        dialogTransfer.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox<String> cbTransferOption;
    private javax.swing.JDialog dialogTransfer;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel labelTransferItem;
    private javax.swing.JTextField textTransferDestination;
    // End of variables declaration//GEN-END:variables

    public void showDialog(ArrayList<FileInfo> listItem, String des, int type) {
        if (listItem.size() == 0) {
            System.out.println("List item size = 0");
        }
        if (listItem.size() == 1) {
            labelTransferItem.setText("Transfer " + listItem.get(0).name + " to:");
        } 
        else {
            labelTransferItem.setText("Transfer " + listItem.size() + " items to:");
        }
        textTransferDestination.setText(des);
        cbTransferOption.setSelectedIndex(type < 0 || type > 1 ? 0 : type);
        dialogTransfer.setLocationRelativeTo(AppController.getInstance().getParent());
        dialogTransfer.setVisible(true);
    }
    
    public void showDialog(TablePanel src, TablePanel des, ArrayList<Integer> rows, Enums.DragMode mode) {
        if (rows.size() > 0 && !src.getPath().equals(des.getPath())) {
            Collections.sort(rows, Collections.reverseOrder());
            labelTransferItem.setText("Transfer " + rows.size() + " item(s) to:");
            cbTransferOption.setSelectedItem(mode.toString());
            textTransferDestination.setText(des.getPath().toString());
            
            final FileModel srcModel = (FileModel)src.getTable().getModel();
            final FileModel desModel = (FileModel)des.getTable().getModel();
            
            transferCallback = () -> {
                for (int i = 0; i < rows.size(); i++) {
                    final FileInfo fileInfo = srcModel.getRow(rows.get(i));
                    final Path desPath = Paths.get(des.getPath().toString(), fileInfo.name);
                    final int index = rows.get(i);
                    switch (cbTransferOption.getSelectedIndex()) {
                    case 0:
                        DataController.getInstance().copyFile(fileInfo, desPath.toFile(), () -> { 
                            desModel.addRow(new FileInfo(desPath)); 
                            System.out.println("DONE COPY");
                        });
                        break;
                    case 1:
                        DataController.getInstance().moveFile(fileInfo, desPath.toFile(), () -> { 
                            srcModel.removeRow(index);
                            desModel.addRow(new FileInfo(desPath)); 
                            System.out.println("DONE MOVE");
                        });
                        break;
                    }
                }
            };
            
            dialogTransfer.setLocationRelativeTo(AppController.getInstance().getParent());
            dialogTransfer.setVisible(true);
        }
        else {
            JOptionPane.showMessageDialog(null, "You select nothing", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void showDialog(ArrayList<Integer> rows, Enums.DragMode mode) {
        TablePanel srcPanel = DataController.getInstance().getSourcePanel();
        TablePanel desPanel = DataController.getInstance().getDesPanel();
        showDialog(srcPanel, desPanel, rows, mode);
    }
    
    public void showDialog(Enums.DragMode mode) {        
        TablePanel srcPanel = DataController.getInstance().getSourcePanel();
        TablePanel desPanel = DataController.getInstance().getDesPanel();
        JTable table = srcPanel.getTable();
        ArrayList<Integer> rows = new ArrayList<>();
        int[] rowArray = DataController.getInstance().getSourcePanel().getTable().getSelectedRows();
        for (int i = 0; i < rowArray.length; i++) {
            rows.add(table.convertRowIndexToModel(rowArray[i]));
        }
        showDialog(srcPanel, desPanel, rows, mode);
    }
    
    private Runnable transferCallback;
    
    
}
