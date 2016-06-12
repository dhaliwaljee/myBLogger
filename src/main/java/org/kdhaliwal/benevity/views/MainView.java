package org.kdhaliwal.benevity.views;

import java.awt.Button;
import java.util.ArrayList;
import java.util.List;

import org.kdhaliwal.benevity.utility.Config;
import org.kdhaliwal.benevity.utility.VirtualMachine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainView extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {    
        Platform.setImplicitExit(true);
        
        String workingDir = System.getProperty("user.dir");
        System.out.println("Current working directory : " + workingDir);
        
        FXMLLoader loader = new FXMLLoader();
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        MainViewController controller = loader.getController();
        
        Scene scene = new Scene(root);
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    
    public static void run(){
        launch();
    }
    

}
