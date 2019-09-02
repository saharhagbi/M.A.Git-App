package main;

import common.MAGitResourceConstants;
import common.StringConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.RepositoryController;
import starting.StartingController;

import java.net.URL;

public class MAGitmain extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        URL startingFXML = getClass().getResource(MAGitResourceConstants.STARTING_SCENE);
        FXMLLoader loader = new FXMLLoader(startingFXML);
///////////////////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////////
        // load MAGitmain.MAGitmain fxml
        loader.setLocation(startingFXML);
        Parent root = loader.load(startingFXML.openStream());
        StartingController startingController = loader.getController();

        primaryStage.setTitle(StringConstants.MAGIT);

        Scene scene = new Scene(root, 1050, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


   /* private void initStartingComponents()
    {
        StartingController startingController = new StartingController();
        startingController.initAllMembers();
    }*/
}
