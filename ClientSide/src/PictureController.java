import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class PictureController implements Initializable {

    @FXML
    private TextFlow Description;

    @FXML
    private Pane DescriptionPane;

    @FXML
    private ImageView Image;

    @FXML
    private TextFlow Title;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //i will pass a picture url and a description to this function
    public void setPicture(String url, String description){
        //set the image
        Image.setImage(new javafx.scene.image.Image(url));
        //set the description
        Description.getChildren().clear();
        Description.getChildren().add(new javafx.scene.text.Text(description));
    }
}

