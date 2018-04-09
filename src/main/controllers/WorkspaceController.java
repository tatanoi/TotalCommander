package main.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.util.Callback;
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
import java.util.HashMap;

public class WorkspaceController {

    public WorkspaceController() {

    }

    @FXML
    TreeTableView<Path> treeTableView;
    @FXML
    TreeTableColumn<Path, String> columnName;
    @FXML
    TreeTableColumn<Path, String> columnExtension;
    @FXML
    TreeTableColumn<Path, String> columnSize;
    @FXML
    TreeTableColumn<Path, String> columnDate;
    @FXML
    TreeTableColumn<Path, String> columnAttribute;
    @FXML
    TextField textField;
    @FXML
    ComboBox<ComboBoxValue> comboBoxDrive;

    @FXML
    public void initialize() {

        columnName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getFileName().toString()));
        columnExtension.setCellValueFactory(param -> {
            Path path = param.getValue().getValue();
            if (Files.isDirectory(path)) {
                return new SimpleStringProperty("");
            }
            else {
                return new SimpleStringProperty(getFileExtension(path.toString()));
            }
        });
        columnSize.setCellValueFactory(param -> {
            Path path = param.getValue().getValue();
            if (Files.isDirectory(path)) {
                return new SimpleStringProperty("<DIR>");
            }
            else {
                try {
                    return new SimpleStringProperty(Long.toString(Files.size(path)));
                } catch (IOException e) {
                    return new SimpleStringProperty("<unknown>");
                }
            }
        });
        columnDate.setCellValueFactory(param -> {
            Path path = param.getValue().getValue();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                return new SimpleStringProperty(sdf.format(Files.getLastModifiedTime(path).toMillis()));
            } catch (IOException e) {
                return new SimpleStringProperty("<unknown>");
            }

        });
        columnAttribute.setCellValueFactory(param -> {
            Path path = param.getValue().getValue();
            String result = "";
            try {
                result += Files.isHidden(path) ? "H-" : "";
            } catch (IOException ignored) {

            }

            result += Files.isReadable(path) ? "R-" : "UR-";
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
                if (file.exists() && file.isDirectory() && Files.isReadable(file.toPath())) {
                    changeDirectory(path);
                }
                else {
                    textField.setText(treeTableView.getRoot().getValue().toString());
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
                Path selected = treeTableView.getSelectionModel().getSelectedItem().getValue();
                if (event.getClickCount() == 2 && selected != null && Files.isDirectory(selected) && Files.isReadable(selected)) {
                    changeDirectory(selected.toString());
                }
            }
        });
        treeTableView.setRowFactory(param -> {
            final TreeTableRow<Path> row = new TreeTableRow<>();
            row.setOnDragDetected(event -> {
                TreeItem<Path> selected = treeTableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Dragboard db = treeTableView.startDragAndDrop(TransferMode.ANY);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent content = new ClipboardContent();
                    content.putString(selected.getValue().toString());
                    db.setContent(content);
                    event.consume();;
                }
            });
            row.setOnDragOver(event -> {
                // data is dragged over the target
                Dragboard db = event.getDragboard();
                if (event.getDragboard().hasString()){
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });
            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (event.getDragboard().hasString()) {

                    if (!row.isEmpty()) {
                        // This is were you do your magic.
                        // Move your row in the tree etc
                        // Here is two examples of how to access
                        // the drop destination:
                        int dropIndex = row.getIndex();
                        TreeItem<Path> droppedon = row.getTreeItem();
                        success = true;
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });
            return row;
        });
    }


    private void changeDirectory(String path) {

        Path folder = Paths.get(path);
        TreeItem<Path> root = new TreeItem<>(folder);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {

            ComboBoxValue result = getRootDrive(folder.toString());
            if (result != null) {
                result.isActiveChangeListener = false;
                comboBoxDrive.getSelectionModel().select(result);
            }

            for (Path entry : stream) {
                TreeItem<Path> child = new TreeItem<>(entry, getCacheImageView(entry));
                root.getChildren().add(child);
            }

            treeTableView.setRoot(root);
            treeTableView.setShowRoot(false);
            textField.setText(treeTableView.getRoot().getValue().toString());
        } catch (IOException ex) {
            // An I/O problem has occurred
        }
    }


    private ImageView getCacheImageView(Path entry) {

        ImageView imageView = new ImageView();
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(entry.toFile());
        java.awt.Image image = ((ImageIcon) icon).getImage();
        BufferedImage bi = (BufferedImage) image;
        imageView.setImage(SwingFXUtils.toFXImage(bi, null));
        return imageView;
    }


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
        Path current = treeTableView.getRoot().getValue();
        if (current.getNameCount() != 0) {
            changeDirectory(current.getParent().toString());
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
