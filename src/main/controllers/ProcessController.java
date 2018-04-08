package main.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
    TextField textField;
    @FXML
    ComboBox<ComboBoxValue> comboBoxDrive;

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
            String result = "";
            result += file.isHidden() ? "H " : " ";
            result += Files.isReadable(file.toPath()) ? "R" : "UR";
            return new SimpleStringProperty(result);
        });

        //---------------------- TEXT FIELD --------------------------//
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                String path = textField.getText();
                if (path.charAt(path.length() - 1) != File.separatorChar) {
                    path += File.separatorChar;
                }
                File file = new File(path);
                if (file.exists() && file.isDirectory()) {
                    changeDirectory(path);
                }
                else {
                    textField.setText(treeTableView.getRoot().getValue().getAbsolutePath());
                }
            }
        });

        //---------------------- COMBO BOX ---------------------------//
        comboBoxDrive.setItems(FXCollections.observableArrayList(getComboBoxValues()));
        comboBoxDrive.setConverter(new StringConverter<ComboBoxValue>() {
            @Override
            public String toString(ComboBoxValue object) {
                return object.value.getAbsolutePath();
            }

            @Override
            public ComboBoxValue fromString(String string) {
                return null;
            }
        });
        comboBoxDrive.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.value.isDirectory() && newValue.isActiveChangeListener) {
                changeDirectory(newValue.value.getAbsolutePath());
            }
            newValue.isActiveChangeListener = true;
        });
        comboBoxDrive.getSelectionModel().selectFirst();

        treeTableView.setOnMouseClicked(event -> {
            if (treeTableView.getSelectionModel().getSelectedItem() != null) {
                File selected = treeTableView.getSelectionModel().getSelectedItem().getValue();
                if (event.getClickCount() == 2 && selected != null && selected.isDirectory() && Files.isReadable(selected.toPath())) {
                    changeDirectory(selected.getAbsolutePath());
                }
            }
        });
    }


    private void changeDirectory(String path) {

        Path folder = Paths.get(path);
        File node = folder.toFile();
        TreeItem<File> root = new TreeItem<>(node);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {

            ComboBoxValue result = getRootDrive(node.getAbsolutePath());
            if (result != null) {
                result.isActiveChangeListener = false;
                comboBoxDrive.getSelectionModel().select(result);
            }

            for (Path entry : stream) {
                File file = entry.toFile();
                Files.getLastModifiedTime(entry);

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
            textField.setText(treeTableView.getRoot().getValue().getAbsolutePath());
        } catch (IOException ex) {
            // An I/O problem has occurred
        }
    }


    private void changeDirectory(String path, boolean activeComboBox) {

        File fileNode = new File(path);
        File[] children = fileNode.listFiles();
        TreeItem<File> root = new TreeItem<>(fileNode);

        ComboBoxValue result = getRootDrive(fileNode.getAbsolutePath());
        if (result != null) {
            result.isActiveChangeListener = activeComboBox;
            comboBoxDrive.getSelectionModel().select(result);
        }

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
        textField.setText(fileNode.getAbsolutePath());
    }


//    private void changeDirectory(String path) {
//       changeDirectory(path, false);
//    }


    private ComboBoxValue getRootDrive(String child) {
        for (ComboBoxValue comboBoxValue : comboBoxDrive.getItems()) {
            if (child.startsWith(comboBoxValue.value.getAbsolutePath()) ||
                    comboBoxValue.value.getAbsolutePath().startsWith(child)) {
                return  comboBoxValue;
            }
        }
        return null;
    }


    private String getFileExtension(String fullName) {
        if (fullName != null) {
            String fileName = new File(fullName).getName();
            int dotIndex = fileName.lastIndexOf('.');
            return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        }
        return null;
    }


    private ArrayList<ComboBoxValue> getComboBoxValues() {
        ArrayList<ComboBoxValue> values = new ArrayList<>();
        for (int i = 0; i < File.listRoots().length; i++) {
            if (File.listRoots()[i].isDirectory()) {
                values.add(new ComboBoxValue(File.listRoots()[i], true));
            }
        }
        return values;
    }


    public void backToParent() {
        File current = treeTableView.getRoot().getValue();
        if (current.toPath().getNameCount() != 0) {
            changeDirectory(current.getParent());
        }
    }


    public class ComboBoxValue {
        File value;
        boolean isActiveChangeListener;

        public ComboBoxValue(File value) {
            this.value = value;
            this.isActiveChangeListener = false;
        }

        ComboBoxValue(File value, boolean isActiveChangeListener) {
            this.value = value;
            this.isActiveChangeListener = isActiveChangeListener;
        }
    }
}
