package main.controllers;

import com.sun.deploy.util.StringUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;

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

        columnName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getName()));
        columnExtension.setCellValueFactory(param -> {
            File file = param.getValue().getValue();
            if (file.isDirectory()) {
                return new SimpleStringProperty("");
            }
            else {
                return new SimpleStringProperty(getFileExtension(file.getAbsolutePath()));
            }
        });
        columnSize.setCellValueFactory(param -> {
            File file = param.getValue().getValue();
            if (file.isDirectory()) {
                return new SimpleStringProperty("<DIR>");
            }
            else {
                return new SimpleStringProperty(Long.toString(file.length()));
            }
        });
        columnDate.setCellValueFactory(param -> {
            File file = param.getValue().getValue();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(sdf.format(file.lastModified()));
        });
        columnAttribute.setCellValueFactory(param -> {
            File file = param.getValue().getValue();
            return new SimpleStringProperty(file.isHidden() ? "Hidden" : "");
        });


        treeTableView.setOnMouseClicked(event -> {
            if (treeTableView.getSelectionModel().getSelectedItem() != null) {
                File selected = treeTableView.getSelectionModel().getSelectedItem().getValue();
                if (event.getClickCount() == 2 && selected != null && selected.isDirectory() && Files.isReadable(selected.toPath())) {
                    changeDirectory(selected.getAbsolutePath());
                }
            }
        });

        changeDirectory("E:/");
    }


    public void changeDirectory(String path) {

        File fileNode = new File(path);
        File[] children = fileNode.listFiles();
        TreeItem<File> root = new TreeItem<>(fileNode);

        assert children != null : "Children is null";
        for (File file : children) {

            ImageView imageView = new ImageView();
            imageView.setFitWidth(15);
            imageView.setFitHeight(15);
            Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
            java.awt.Image image = ((ImageIcon) icon).getImage();
            BufferedImage bi = (BufferedImage) image;
            imageView.setImage(SwingFXUtils.toFXImage(bi, null));

            TreeItem<File> child = new TreeItem<>(file, imageView);
            root.getChildren().add(child);
        }

        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

    public static String getFileExtension(String fullName) {
        if (fullName != null) {
            String fileName = new File(fullName).getName();
            int dotIndex = fileName.lastIndexOf('.');
            return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        }
        return null;
    }
}
