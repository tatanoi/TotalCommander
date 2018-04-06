package main.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import jdk.nashorn.internal.runtime.Debug;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

public class ProcessController {

    public ProcessController() {

    }

    @FXML
    TreeTableView<File> treeTableView;
    @FXML
    TreeTableColumn<File, String> columnName;
    @FXML
    TreeTableColumn<File, String> columnExtension;
    @FXML
    TreeTableColumn<File, String> columnSize;
    @FXML
    TreeTableColumn<File, String> columnDate;
    @FXML
    TreeTableColumn<File, String> columnAttribute;


    @FXML
    public void initialize() {
        columnName.setCellValueFactory(param -> {
            if (param.getValue().getValue().getParentFile() != null) {
                return new SimpleStringProperty(param.getValue().getValue().getName());
            }
            if (param.getValue().previousSibling() == null ) {
                return new SimpleStringProperty("<-- Back..");
            }
            return new SimpleStringProperty(param.getValue().getValue().getName());
        });
        treeTableView.setOnMouseClicked(event -> {
            if (treeTableView.getSelectionModel().getSelectedItem() != null) {
                File selected = treeTableView.getSelectionModel().getSelectedItem().getValue();
                if (event.getClickCount() == 2 && selected != null && selected.isDirectory() && Files.isReadable(selected.toPath())) {
                    changeDirectory(selected.getAbsolutePath());
                }
            }
        });
        changeDirectory("/");
    }

    public void changeDirectory(String path) {
        File fileNode = new File(path);
        File[] children = fileNode.listFiles();
        TreeItem<File> root = new TreeItem<>(fileNode);

        if (fileNode.getParent() != null) {
            File backNode = new File(fileNode.getParent());
            root.getChildren().add(new TreeItem<>(backNode));
        }

        assert children != null : "Children is null";
        for (File file : children) {

            ImageView imageView = new ImageView();
            imageView.setFitWidth(15);
            imageView.setFitHeight(15);
            Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
            java.awt.Image image = ((ImageIcon)icon).getImage();
            BufferedImage bi = (BufferedImage)image;
            imageView.setImage(SwingFXUtils.toFXImage(bi, null));

            TreeItem<File> child = new TreeItem<>(file, imageView);
            root.getChildren().add(child);
        }

        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

}
