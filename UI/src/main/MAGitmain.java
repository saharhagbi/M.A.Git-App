package main;

import common.MAGitResourceConstants;
import common.constants.StringConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MAGitmain extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        URL startingFXML = getClass().getResource(MAGitResourceConstants.STARTING_SCENE);
        FXMLLoader loader = new FXMLLoader(startingFXML);

        loader.setLocation(startingFXML);
        Parent root = loader.load(startingFXML.openStream());

        primaryStage.setTitle(StringConstants.MAGIT);

        Scene scene = new Scene(root);

        //scene.getStylesheets().add("/common/themes/dark.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
