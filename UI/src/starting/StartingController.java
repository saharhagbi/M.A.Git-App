package starting;

import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import main.MAGitController;

//import java.awt.*;
import java.io.File;
import javafx.scene.control.Button;

public class StartingController
{

    private MAGitController m_MagitController;

    @FXML
    private Button m_BtnNewRepository;

    @FXML
    private void createNewRepositry()
    {
        //open stage that input PathToRepository and name Of Repository

        DirectoryChooser DirChooser = new DirectoryChooser();
        File selectedDir = DirChooser.showDialog(null);

        if(selectedDir != null)
        {
            System.out.println(selectedDir.getName());
        }

        m_MagitController.CreateNewRepositry();
    }

}
