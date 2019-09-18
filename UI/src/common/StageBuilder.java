package common;

import common.constants.StringConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import starting.StartingController;

import java.io.IOException;
import java.net.URL;

public class StageBuilder
{
    private static String m_Resource;
    private static String m_Title;

    public static void LoadStage() throws IOException
    {
        URL urlFXML = StageBuilder.class.getResource(m_Resource);
        FXMLLoader loader = new FXMLLoader(urlFXML);

        loader.setLocation(urlFXML);
        Parent root = loader.load(urlFXML);
        Stage stage = new Stage();
        stage.setTitle(m_Title);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
