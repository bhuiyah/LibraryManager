import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class CatalogueController implements Initializable {

    String UserName;
    Set<Entry> entries = new HashSet<>();

    @FXML
    private TextFlow TopBar;

    @FXML
    private Button CheckOutButton;
    @FXML
    private StackPane MainInterfacePane;
    @FXML
    private Pane CheckOutPane;
    @FXML
    private TableView<Entry> TableView;
    @FXML
    private TableColumn<Entry, String> TitleView;
    @FXML
    private TableColumn<Entry, String> AuthorView;
    @FXML
    private TableColumn<Entry, String> GenreView;
    @FXML
    private TableColumn<Entry, String> CheckOutView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setUserName(String UserName){
        this.UserName = UserName;
    }

    public void setTopBar(){
        TopBar.getChildren().add(new javafx.scene.text.Text("Welcome " + UserName));
    }

    public void setEntries(Set<Entry> entries){
        this.entries = entries;
    }

    public void CheckoutButtonPressed(){
        System.out.println("Checkout Button Pressed");
        //put checkout pane on top of main interface pane
        MainInterfacePane.getChildren().add(CheckOutPane);
        populateTableView();
    }

    public void populateTableView(){
        //populate each column with the appropriate data
        TitleView.setCellValueFactory(new PropertyValueFactory<Entry, String>("title"));
        AuthorView.setCellValueFactory(new PropertyValueFactory<Entry, String>("author"));
        GenreView.setCellValueFactory(new PropertyValueFactory<Entry, String>("genre"));
        CheckOutView.setCellValueFactory(new PropertyValueFactory<Entry, String>("checked_out"));
    }

}
