package org.kdhaliwal.benevity.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.kdhaliwal.benevity.utility.Config;
import org.kdhaliwal.benevity.utility.LogFiles;
import org.kdhaliwal.benevity.utility.VirtualMachine;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.collections.FXCollections;

public class MainViewController implements Initializable {
    @FXML
    TabPane mainTabPane;
    private Config config;
    // initialize block
    {
        try {
            config = Config.create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize(URL location, ResourceBundle resources) {

        // Add Main Tabs in Main TabPane
        try {
            for (Tab tab : getParentTabs()) {
                mainTabPane.getTabs().add(tab);
            }
            // for zooming by touchpad
            mainTabPane.setOnZoom(e -> {
                mainTabPane.setScaleX(mainTabPane.getScaleX() * e.getZoomFactor());
                mainTabPane.setScaleY(mainTabPane.getScaleY() * e.getZoomFactor());
            });
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private List<Tab> getParentTabs() throws Exception {
        List<Tab> list = new ArrayList<Tab>();
        List<TabPane> subTabPane = getSubTabPanes();
        int count = 0;

        for (VirtualMachine vm : config.getVm().values()) {
            Tab tab = new Tab(vm.getName());
            tab.setText(vm.getName());
            tab.setContent(subTabPane.get(count++));
            list.add(tab);
        }
        return list;
    }

    /*
     * Inside TabPanes that contains ListView
     */
    private List<TabPane> getSubTabPanes() throws Exception {
        List<TabPane> list = new ArrayList<TabPane>();
        for (VirtualMachine vm : config.getVm().values()) {
            TabPane pane = new TabPane();
            for (LogFiles files : vm.getLogFiles()) {
                // System.out.println(files.getName()+" = "+files.getUrl());
                Tab tab = new Tab();
                tab.setGraphic(new Label(files.getName()));
                tab.getGraphic().setStyle("-fx-text-fill: gray");

                // Adding controls to application(log viewer)

                ListView<String> logView = new ListView<String>();
                ButtonBar buttonBar = new ButtonBar();

                // Clear Button
                Button clearButton = new Button("Clear");
                clearButton.setOnAction(e -> {
                        logView.setItems(FXCollections.observableArrayList());
                });

                // Refresh Button
                Button refreshButton = new Button("Refresh");
                refreshButton.setOnAction(e -> {
                    logView.refresh();
                });

                // Exit Application Button
                Button exitButton = new Button("Exit Application");
                exitButton.setOnAction(e -> {
                    Stage stage = (Stage) this.mainTabPane.getScene().getWindow();
                    stage.close();
                    System.exit(0);
                });

                TextField text = new TextField();
                text.setPromptText("Search");
                /*text.setOnAction(e -> {
                    logView.getItems();
                    System.out.println(logView.getAccessibleText());
                });*/
                FilteredList<String> data = new FilteredList<>(logView.getItems());
                text.textProperty().addListener(e -> {
                    String filter = text.getText();
                    if( filter == null || filter.length() == 0){
                        data.setPredicate(s -> true);
                    }else{
                        //it will display only searched values
                        data.setPredicate(s -> s.toLowerCase().contains(filter.toLowerCase()));
                        logView.setItems(data);
                        logView.refresh();
                    }
                });
                
                buttonBar.getButtons().addAll(text, clearButton, refreshButton, exitButton);

                AnchorPane anchorPane = new AnchorPane();
                AnchorPane.setBottomAnchor(logView, 0.0);
                AnchorPane.setTopAnchor(logView, 30.0);
                AnchorPane.setRightAnchor(logView, 0.0);
                AnchorPane.setLeftAnchor(logView, 0.0);

                AnchorPane.setRightAnchor(buttonBar, 1.0);
                AnchorPane.setLeftAnchor(buttonBar, 1.0);
                AnchorPane.setTopAnchor(buttonBar, 1.0);
                anchorPane.getChildren().addAll(buttonBar, logView);
                tab.setContent(anchorPane);

                // Creating Remote Connection
                JSch jsch = new JSch();
                Session session = jsch.getSession(vm.getUserName(), vm.getIp());
                jsch.setKnownHosts(config.getKnownHostsFile());
                jsch.addIdentity(config.getIdentityFile());
                session.setPassword("c1rcl3");
                // System.out.println(session.getTimeout());
                try {
                    session.connect(config.getTimeout() * 1000);

                    System.out.println("Session = " + session.isConnected());

                    // reading log files
                    read(logView, files.getUrl(), session);
                    pane.getTabs().add(tab);
                } catch (JSchException ex) {
                    String message = String.format("\nConnection with [%s]: %s is not established. ", vm.getName(),
                            vm.getIp());
                    message += String.format("\nUser Name : %s", vm.getUserName());
                    message += String.format("\nType : %s", vm.getType());
                    System.out.printf("\n%s", message);

                    Stage stage = new Stage(StageStyle.UTILITY);

                    VBox vbox = new VBox(10);
                    vbox.setPadding(new Insets(20));
                    Label label = new Label(message);
                    Button btnExit = new Button("OK");
                    btnExit.setOnAction(e -> {
                        stage.close();
                    });
                    vbox.getChildren().addAll(label, btnExit);

                    System.out.println(message.length());
                    stage.setScene(new Scene(vbox, label.getPrefWidth(), 200));
                    stage.show();
                }
            }
            pane.setSide(Side.TOP);
            list.add(pane);
        }
        return list;
    }

    public void read(ListView<String> logView, String file, Session session) throws Exception {
        StringProperty lines = new SimpleStringProperty();
        lines.addListener((observable, oldValue, newValue) -> {
            // System.out.println(newValue);
            logView.getItems().add(newValue);

            // view.getItems().add(newValue);
        });

        Thread t = null;

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        // ((ChannelExec)channel).setCommand("tail -F /opt/java/apache/tomcat/logs/catalina.out");
        ((ChannelExec) channel).setCommand("tail -F " + file);
        ((ChannelExec) channel).setErrStream(System.err);
        InputStream stream = channel.getInputStream();
        channel.setPty(true); // tail will stop when program close.
        channel.connect();
        System.out.println(channel.isConnected());

        t = new Thread(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            while (true) {

                String line;
                try {
                    line = br.readLine();
                    lines.setValue(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Starting Thread
        t.start();

    }

    public TabPane geTabPane() {
        return this.mainTabPane;
    }
}
