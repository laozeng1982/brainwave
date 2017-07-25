/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brainwave;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javax.swing.SwingUtilities;
import datamodel.AllCurvesHolder;
import datamodel.SingleFileCurveSets;
import java.awt.Dimension;
import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;
import javax.swing.ImageIcon;
import tools.files.IOHelper;
import tools.files.JFxFileChooser;
import tools.utilities.Logs;
import ui.CurvePloter;
import utilities.DataParaser;

/**
 *
 * @author JianGe
 */
public class FXMLDocumentController implements Initializable {

    private Label label;
    @FXML
    private TreeView<String> dateSetsTreeView;
    @FXML
    private TabPane drawTablePane;
    @FXML
    private ToggleGroup zoomToggle;
    @FXML
    private ComboBox<Integer> cmBxScale;
    @FXML
    private ToggleButton pointTglBtn;
    @FXML
    private ToggleButton zoomInTglBtn;
    @FXML
    private ToggleButton zoomOutTglBtn;

    private static AllCurvesHolder allCurvesHolder = new AllCurvesHolder();
    private TreeItem<String> rootItem;

    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        init();
    }

    @FXML
    private void onOpenAscii(ActionEvent event) {
        JFxFileChooser jFxFileChooser = new JFxFileChooser(BrainWave.stage, null, "All Ascii", "*txt");
        File openedFile = jFxFileChooser.selectSingleFile(null, true);
        if (openedFile != null) {
            Logs.e(openedFile.getAbsoluteFile().toString());
            SingleFileCurveSets singleFileCurveSets = DataParaser.paraseArrayList(openedFile.getName(), IOHelper.readAsciiFile(openedFile, "utf-8"));
            allCurvesHolder.addFileCurveSets(singleFileCurveSets);

            String fileName = openedFile.getName().split("\\.")[0];

            boolean hasTab = false;
            for (TreeItem item : rootItem.getChildren()) {
                if (item.getValue().equals(fileName)) {
                    hasTab = true;
                }
            }

            if (!hasTab) {
                rootItem.getChildren().add(new TreeItem<String>(fileName));
            }

            createdTab(fileName);

        } else {
            Logs.e("No file selected");
        }

    }

    @FXML
    private void onOpenDb(ActionEvent event) {
    }

    @FXML
    private void onRun(ActionEvent event) {
    }

    @FXML
    private void onExit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void onMouseType(ActionEvent event) {
        CurvePloter curvePloter = (CurvePloter) ((SwingNode) drawTablePane.getSelectionModel().getSelectedItem().getContent()).getContent();
        if (event.getSource() == pointTglBtn) {
            curvePloter.pointer();
        } else if (event.getSource() == zoomInTglBtn) {
            curvePloter.zoomIn();
        } else if (event.getSource() == zoomOutTglBtn) {
            curvePloter.zoomOut();
        }
    }

    private void init() {

        //Init TreeView
        rootItem = new TreeItem<>("Data Sets");
        rootItem.setExpanded(true);
        dateSetsTreeView.setRoot(rootItem);

        dateSetsTreeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {

            @Override
            public TreeCell<String> call(TreeView<String> arg0) {

                return new TreeCell<String>() {

                    @Override
                    protected void updateItem(String str, boolean empty) {

                        // TODO Auto-generated method stub  
                        super.updateItem(str, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(str);
                        }
                    }
                };
            }

        });

        dateSetsTreeView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                    TreeItem<String> oldItem, TreeItem<String> newItem) {
                if (!newItem.getValue().equals("Data Sets")) {
                    updatePlots(newItem.getValue());
                }

            }
        });
        // Init ChartView
        // Chart Viewer

//        CurvePloter sourceChartPane = new CurvePloter(new Dimension(1750, 950), "Raw brain wave!");
////        Logs.e(drawTablePane.getWidth()+", " + drawTablePane.getHeight());
//
//        SwingUtilities.invokeLater(() -> {
//            swingNode.setContent(sourceChartPane);
//        });
    }

    //
    // A utility to load an image icon from the Java class path
    //
    private ImageIcon loadImageIcon(String path) {
        try {
            return new ImageIcon(getClass().getClassLoader().getResource(path));
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void onAddTab(ActionEvent event) {
    }

    private void updatePlots(String fileName) {
        int selected = -1;
        for (int i = 0; i < drawTablePane.getTabs().size(); i++) {
            if (drawTablePane.getTabs().get(i).getText().equals(fileName)) {
                selected = i;
                break;
            }
        }
        if (selected != -1) {
            drawTablePane.getSelectionModel().select(selected);
        } else {
            createdTab(fileName);
        }

        Logs.e(fileName + " will be updated!");
    }

    /**
     *
     * @param tabName
     */
    private void createdTab(String tabName) {
        Tab tab = new Tab(tabName);
        Logs.e(allCurvesHolder.getSelectedFileCurveSets(tabName).getFileName());
        CurvePloter sourceChartPane = new CurvePloter(new Dimension(1750, 950), tabName, allCurvesHolder.getSelectedFileCurveSets(tabName));
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(sourceChartPane);
        });
        tab.setContent(swingNode);
        drawTablePane.getTabs().add(tab);
        drawTablePane.getSelectionModel().select(tab);
    }
}
