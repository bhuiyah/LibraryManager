import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private TextField AddAuthor;

    @FXML
    private Button AddButton;

    @FXML
    private Button AddEntriesButton;

    @FXML
    private Pane AddEntriesPane;

    @FXML
    private TextField AddGenre;

    @FXML
    private TextField AddTitle;

    @FXML
    private TextField AddType;

    @FXML
    private Button ClearButton;

    @FXML
    private ComboBox<?> EntriesDropDown;

    @FXML
    private TextField ExistingCount;

    @FXML
    private StackPane MainInterface;

    @FXML
    private Button ManageUsersButton;

    @FXML
    private Pane ManageUsersPane;

    @FXML
    private TextField NewCount;

    @FXML
    private TableColumn<?, ?> PasswordColumn;

    @FXML
    private Button RemoveButton;

    @FXML
    private TableColumn<?, ?> RemoveCount;

    @FXML
    private Button RemoveEntriesButton;

    @FXML
    private Pane RemoveEntriesPane;

    @FXML
    private Button RemoveUserButton;

    @FXML
    private TableView<?> ReturnTableView;

    @FXML
    private TableColumn<?, ?> Title;

    @FXML
    private Button UserListItems;

    @FXML
    private TableColumn<?, ?> UsernameColumn;
    private Client client;
    private String username;
    private HashMap<String, LoginInfo> loginInfo;
    private HashMap<String, Entry> entries;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public void setLoginInfo(HashMap<String, LoginInfo> loginInfo) {
        this.loginInfo = loginInfo;
    }

    public void setEntries(HashMap<String, Entry> entries){
        this.entries = entries;
    }


}