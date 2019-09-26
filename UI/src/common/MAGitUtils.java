package common;

import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

public class MAGitUtils
{
    public static String GetString(String i_Prompt, String i_Label, String i_Title) throws Exception
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(i_Title);
        dialog.setHeaderText(i_Prompt);
        dialog.setContentText(i_Label);
        Optional<String> result = dialog.showAndWait();

        if (result == null)
            throw new Exception("you didn't enter anything!" + System.lineSeparator() + "Please try again");

        return result.get();
    }

    public static String GetUserChoice(String i_Title, String i_HeaderText, String i_DefaultChoice, String[] i_UserChoices) throws Exception
    {
        ChoiceDialog<String> dialog = new ChoiceDialog(i_DefaultChoice, i_UserChoices);

        dialog.setTitle(i_Title);
        dialog.setHeaderText(i_HeaderText);

        dialog.setOnCloseRequest(event ->
                dialog.close()
        );



        Optional<String> result = dialog.showAndWait();

        String strWanted = result.get();
        return result.get();
    }

    public static Stage GetStage(Control i_Control)
    {
        return (Stage) i_Control.getScene().getWindow();
    }

    public static void InformUserPopUpMessage(Alert.AlertType i_AlertType, String i_Title, String i_HeaderText, String i_ContextText)
    {
        Alert alert = new Alert(i_AlertType);
        alert.setTitle(i_Title);
        alert.setHeaderText(i_HeaderText);
        alert.setContentText(i_ContextText);

        alert.showAndWait();
    }

    public static File GetDirectory(Window i_CurrentStage, String i_Title) throws FileNotFoundException
    {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(i_Title);
        File selectedDir = dirChooser.showDialog(i_CurrentStage);


        if (selectedDir == null)
        {
            throw new FileNotFoundException("No Directory was chosen");
        }
        return selectedDir;
    }

    public static File GetFile(Window i_CurrentStage) throws FileNotFoundException
    {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(i_CurrentStage);

        if (selectedFile == null)
        {
            throw new FileNotFoundException("No Directory was chosen");
        }
        return selectedFile;
    }

    public static void HighlightText(Text i_Txt)
    {
        i_Txt.setStyle("-fx-font-weight: bold; -fx-stroke: #5b72ff");
    }

    public static void UnhighlightText(Text i_Txt)
    {
        i_Txt.setStyle(null);
    }

    public static void HighlightLabel(Label branchName)
    {
        branchName.setStyle("-fx-font-weight: bold; -fx-stroke: #5b72ff");
    }
}
