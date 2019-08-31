package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import common.*;

import java.net.URL;

public class MAGitmain extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        URL startingFXML = getClass().getResource(MAGitResourceConstants.STARTING_STAGE);
        FXMLLoader loader = new FXMLLoader(startingFXML);

        // load MAGitmain.MAGitmain fxml
        loader.setLocation(startingFXML);
        Pane root = loader.load();

        primaryStage.setTitle("M.A.Git");

        Scene scene = new Scene(root, 1050, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
