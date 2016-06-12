package org.kdhaliwal.benevity.views;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Test extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        HBox hbox = new HBox();
        TextArea text = new TextArea("A");
        ListView<String> view = new ListView<>();
        hbox.getChildren().add(text);
        hbox.getChildren().add(view);
        
        Scene scene = new Scene(hbox);
            
        JSch jsch = new JSch();
        Session session = jsch.getSession("root", "192.168.0.111");
        jsch.setKnownHosts("/Users/karandeepsinghdhaliwal/.ssh/known_hosts");
        jsch.addIdentity("/Users/karandeepsinghdhaliwal/.ssh/id_rsa");
        session.setPassword("c1rcl3");
        session.connect();
        System.out.println("\n");
        System.out.println("Session = " + session.isConnected());
        
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        
        System.out.println(sftp.isConnected());
        
        Channel channel=session.openChannel("exec");
        ((ChannelExec)channel).setCommand("tail -F /tmp/BenevityClient.txt");
        ((ChannelExec)channel).setErrStream(System.err);       
        InputStream stream = channel.getInputStream();
        channel.connect();
        System.out.println(channel.isConnected());
        
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        StringProperty lines = new SimpleStringProperty();
        lines.addListener((observable, oldValue, newValue) -> {
                System.out.println(newValue);
                text.appendText(newValue);
            
            view.getItems().add(newValue);
        });
        

        Thread t = new Thread(()->{
            text.appendText("data");
            while(true){
                try {
                    lines.setValue(br.readLine());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
             }

        });
        t.start();
    }
    
    public static void main(String[] args) {
        launch();
    }
}
