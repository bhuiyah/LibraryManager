import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class CatalogueController implements Initializable {

    String UserName;

    @FXML
    private TextFlow TopBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setUserName(String UserName){
        this.UserName = UserName;
    }

    public void setTopBar(){
        TopBar.getChildren().add(new javafx.scene.text.Text("Welcome " + UserName));
    }


}
