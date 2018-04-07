package main.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

public class WorkspaceController {

    public WorkspaceController() {

    }

    @FXML
    TableView<File> tableView;
    @FXML
    TableColumn<File, String> columnName;
    @FXML
    TableColumn<File, String> columnExtension;
    @FXML
    TableColumn<File, String> columnSize;
    @FXML
    TableColumn<File, String> columnDate;
    @FXML
    TableColumn<File, String> columnAttribute;

    public void initialize() {

//        columnName.setCellFactory(new Callback<TableColumn<File, File>, TableCell<File, File>>() {
//            @Override
//            public TableCell<File, File> call(TableColumn<File, File> param) {
//                TableCell<File, File> cell = new TableCell<File, File>() {
//                    @Override
//                    protected void updateItem(File item, boolean empty) {
//                        super.updateItem(item, empty);
//                        ImageView imageView = new ImageView();
//                        imageView.setFitWidth(15);
//                        imageView.setFitHeight(15);
//                        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(item);
//                        java.awt.Image image = ((ImageIcon) icon).getImage();
//                        BufferedImage bi = (BufferedImage) image;
//                        imageView.setImage(SwingFXUtils.toFXImage(bi, null));
//                        setGraphic(imageView);
//                    }
//                };
//                return cell;
//            }
//        });
        columnName.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().getName());
        });
        tableView.setOnMouseClicked(event -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                File selected = tableView.getSelectionModel().getSelectedItem();
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

        assert children != null : "Children is null";
        tableView.getItems().clear();
        tableView.getItems().addAll(children);
    }

}
