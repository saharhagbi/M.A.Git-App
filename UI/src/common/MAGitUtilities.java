package common;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;

public class MAGitUtilities
{
    public static String GetString(String i_Prompt, String i_Label, String i_Title) throws Exception
    {
        //TODO
        // handling in case of emptyString or cancelling

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(i_Title);
        dialog.setHeaderText(i_Prompt);
        dialog.setContentText(i_Label);
        Optional<String> repositoryName = dialog.showAndWait();

        return repositoryName.get();
    }


    public static String GetUsetChoice(String i_Title, String i_HeaderText, String i_DefaultChoice, String[] i_UserChoices)
    {
        ChoiceDialog<String> dialog = new ChoiceDialog(i_DefaultChoice, i_UserChoices);

        dialog.setTitle(i_Title);
        dialog.setHeaderText(i_HeaderText);
        dialog.showAndWait();

        return dialog.getSelectedItem();
    }


    public static Stage GetStage(Control i_Control)
    {
        return (Stage) i_Control.getScene().getWindow();
    }


}
