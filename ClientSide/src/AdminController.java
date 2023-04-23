import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Pair;

import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    TextField AddAuthor;

    @FXML
    private Button AddCurrentButton;

    @FXML
    private Button AddNewButton;

    @FXML
    private Button AddEntriesButton;

    @FXML
    private Pane AddEntriesPane;

    @FXML
    TextField AddGenre;

    @FXML
    TextField AddTitle;

    @FXML
    TextField AddType;

    @FXML
    private Button ClearButton;

    @FXML
    ComboBox<String> EntriesDropDown;

    @FXML
    TextField ExistingCount;

    @FXML
    private StackPane MainInterface;

    @FXML
    private Button ManageUsersButton;

    @FXML
    private Pane ManageUsersPane;

    @FXML
    TextField NewCount;

    @FXML
    private Button RemoveButton;

    @FXML
    private TableColumn<String, String> RemoveCount;

    @FXML
    private Button RemoveEntriesButton;

    @FXML
    private Pane RemoveEntriesPane;

    @FXML
    private TableView<Entry> RemoveTableView;

    @FXML
    private TableColumn<String, String> Title;

    @FXML
    private Button UserListItems;

    @FXML
    private TableColumn<String, String> UsernameColumn;
    @FXML
    private Pane StartAdminPage;
    @FXML
    private Button AdminExitButton;
    @FXML
    private TableView<LoginInfo> UserTableView;
    @FXML
    private Button ShowPasswordButton;
    @FXML
    TextField AddDescription;
    @FXML
    TextField AddURL;

    private Client client;
    private String username;
    private HashMap<String, LoginInfo> loginInfo;
    private HashMap<String, Entry> entries;
    String buttonPressed = "";
    private String AddTitleText = "";
    private String AddAuthorText = "";
    private String AddGenreText = "";
    private String AddTypeText = "";
    private String ExistingCountText = "";
    private String NewCountText = "";
    private String EntriesToBeRemoved = "";
    private String UserSelectedName = "";
    private Admin admin;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MainInterface.getChildren().clear();
        MainInterface.getChildren().add(StartAdminPage);
        //center all the columns
        UsernameColumn.setStyle("-fx-alignment: CENTER;");
        RemoveCount.setStyle("-fx-alignment: CENTER;");
        Title.setStyle("-fx-alignment: CENTER;");
        //removetableview multiple selection
        RemoveTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //if the user clicks on a username row twice in the UserTableView, have a poppup appear with the user's items
        UserTableView.setRowFactory(new Callback<TableView<LoginInfo>, TableRow<LoginInfo>>() {
            @Override
            public TableRow<LoginInfo> call(TableView<LoginInfo> param) {
                final TableRow<LoginInfo> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        LoginInfo rowData = row.getItem();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("User Items");
                        alert.setHeaderText("User Items");
                        //iterate through getIssuedItems() and add each item to the alert
                        for (LoginInfo.IssuedItem item : rowData.getIssuedItems()) {
//                            alert.setContentText(item.toString());
                            //append the item's toString() to the alert's content text
                            alert.setContentText(alert.getContentText() + item.toString());
                        }
                        alert.showAndWait();
                    }
                });
                return row;
            }
        });
        //when i hover over my AddEntriesButton, RemoveEntriesButton, ManageUsersButton, or AdminExitButton, the background color changes to #76448A and when i exit the button, the background color changes back to  #BB8FCE
        AddEntriesButton.setOnMouseEntered(event -> AddEntriesButton.setStyle("-fx-background-color: #76448A;"));
        AddEntriesButton.setOnMouseExited(event -> AddEntriesButton.setStyle("-fx-background-color: #BB8FCE;"));
        RemoveEntriesButton.setOnMouseEntered(event -> RemoveEntriesButton.setStyle("-fx-background-color: #76448A;"));
        RemoveEntriesButton.setOnMouseExited(event -> RemoveEntriesButton.setStyle("-fx-background-color: #BB8FCE;"));
        ManageUsersButton.setOnMouseEntered(event -> ManageUsersButton.setStyle("-fx-background-color: #76448A;"));
        ManageUsersButton.setOnMouseExited(event -> ManageUsersButton.setStyle("-fx-background-color: #BB8FCE;"));
        AdminExitButton.setOnMouseEntered(event -> AdminExitButton.setStyle("-fx-background-color: #76448A;"));
        AdminExitButton.setOnMouseExited(event -> AdminExitButton.setStyle("-fx-background-color: #BB8FCE;"));

        //change every other button to #F5B7B1 when i hover over it and back to  #F9EBEA when i exit it
        AddCurrentButton.setOnMouseEntered(event -> AddCurrentButton.setStyle("-fx-background-color: #F5B7B1;"));
        AddCurrentButton.setOnMouseExited(event -> AddCurrentButton.setStyle("-fx-background-color: #F9EBEA;"));
        AddNewButton.setOnMouseEntered(event -> AddNewButton.setStyle("-fx-background-color: #F5B7B1;"));
        AddNewButton.setOnMouseExited(event -> AddNewButton.setStyle("-fx-background-color: #F9EBEA;"));
        ClearButton.setOnMouseEntered(event -> ClearButton.setStyle("-fx-background-color: #F5B7B1;"));
        ClearButton.setOnMouseExited(event -> ClearButton.setStyle("-fx-background-color: #F9EBEA;"));
        RemoveButton.setOnMouseEntered(event -> RemoveButton.setStyle("-fx-background-color: #F5B7B1;"));
        RemoveButton.setOnMouseExited(event -> RemoveButton.setStyle("-fx-background-color: #F9EBEA;"));
        ShowPasswordButton.setOnMouseEntered(event -> ShowPasswordButton.setStyle("-fx-background-color: #F5B7B1;"));
        ShowPasswordButton.setOnMouseExited(event -> ShowPasswordButton.setStyle("-fx-background-color: #F9EBEA;"));

        //when i hover over a button change my cursor to a hand
        AddEntriesButton.setCursor(Cursor.HAND);
        RemoveEntriesButton.setCursor(Cursor.HAND);
        ManageUsersButton.setCursor(Cursor.HAND);
        AdminExitButton.setCursor(Cursor.HAND);
        AddCurrentButton.setCursor(Cursor.HAND);
        AddNewButton.setCursor(Cursor.HAND);
        ClearButton.setCursor(Cursor.HAND);
        RemoveButton.setCursor(Cursor.HAND);
        ShowPasswordButton.setCursor(Cursor.HAND);

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

    public void setEntries(HashMap<String, Entry> entries) {
        this.entries = entries;
        populateEntriesDropDown();
        populateRemoveTableView();
    }

    public void AddEntriesButtonPressed() {
        MainInterface.getChildren().clear();
        MainInterface.getChildren().add(AddEntriesPane);
        populateEntriesDropDown();
    }

    public void RemoveEntriesButtonPressed() {
        MainInterface.getChildren().clear();
        MainInterface.getChildren().add(RemoveEntriesPane);
        populateRemoveTableView();
    }

    public void ManageUsersButtonPressed() {
        MainInterface.getChildren().clear();
        MainInterface.getChildren().add(ManageUsersPane);
        populateUserTableView();
    }

    public void AdminExitButtonPressed() {
        buttonPressed = "Exit";
    }

    public void populateEntriesDropDown() {
        EntriesDropDown.getItems().clear();
        for (String key : entries.keySet()) {
            EntriesDropDown.getItems().add(key);
        }
    }

    public void populateRemoveTableView() {
        RemoveTableView.getItems().clear();
        for (Entry key : entries.values()) {
            RemoveTableView.getItems().add(key);
        }
        //populate Title
        Title.setCellValueFactory(new PropertyValueFactory<>("title"));
        //populate RemoveCount
        RemoveCount.setCellValueFactory(new PropertyValueFactory<>("count"));
    }

    public void AddCurrentButtonPressed() {
        buttonPressed = "AddCurrentEntry";
    }

    public void AddNewButtonPressed() {
        buttonPressed = "AddNewEntry";
    }

    public void RemoveButtonPressed() {
        buttonPressed = "RemoveEntry";
    }

    public void ClearButtonPressed() {
        EntriesDropDown.setValue(null);
        AddAuthor.clear();
        AddGenre.clear();
        AddTitle.clear();
        AddType.clear();
        NewCount.clear();
        ExistingCount.clear();
    }

    public void populateUserTableView() {
        UserTableView.getItems().clear();
        for (LoginInfo key : loginInfo.values()) {
            UserTableView.getItems().add(key);
        }
        //populate UsernameColumn
        UsernameColumn.setCellValueFactory(new PropertyValueFactory<>("UserName"));
    }

    public void selectedRemoveEntry() {
        EntriesToBeRemoved = RemoveTableView.getSelectionModel().getSelectedItem().getTitle();
    }

    public String getbuttonPressed() {
        return buttonPressed;
    }

    public String getAddTitleText() {
        return AddTitleText;
    }

    public String getAddAuthorText() {
        return AddAuthorText;
    }

    public String getAddGenreText() {
        return AddGenreText;
    }

    public String getAddTypeText() {
        return AddTypeText;
    }

    public String getExistingCountText() {
        return ExistingCountText;
    }

    public String getNewCountText() {
        return NewCountText;
    }

    public void setAddTitleText(String AddTitleText) {
        this.AddTitleText = AddTitleText;
    }

    public void setAddAuthorText(String AddAuthorText) {
        this.AddAuthorText = AddAuthorText;
    }

    public void setAddGenreText(String AddGenreText) {
        this.AddGenreText = AddGenreText;
    }

    public void setAddTypeText(String AddTypeText) {
        this.AddTypeText = AddTypeText;
    }

    public void setExistingCountText(String ExistingCountText) {
        this.ExistingCountText = ExistingCountText;
    }


    public void setNewCountText(String NewCountText) {
        this.NewCountText = NewCountText;
    }

    public void setbuttonPressed(String buttonPressed) {
        this.buttonPressed = buttonPressed;
    }

    public String getDropDownValue() {
        return EntriesDropDown.getValue();
    }

    public void setDropDownValue(String value) {
        EntriesDropDown.setValue(value);
    }

    public String getEntriesToBeRemoved() {
        return EntriesToBeRemoved;
    }

    public void setEntriesToBeRemoved(String EntriesToBeRemoved) {
        this.EntriesToBeRemoved = EntriesToBeRemoved;
    }

    public void ShowPasswordButtonPressed() {
        LoginInfo loginInfo = UserTableView.getSelectionModel().getSelectedItem();
        //ask the user to reenter admin password and id before showing the password
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reenter Admin Password");
        dialog.setHeaderText("Reenter Admin Password");
        dialog.setContentText("Please enter your password:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get().equals(admin.getPassword())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("User Password");
                alert.setHeaderText("User Password");
                alert.setContentText(loginInfo.getPassword());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Incorrect Password");
                alert.showAndWait();
            }
        }
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void EntryDoesNotExist(String entryDoesNotExist) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(entryDoesNotExist);
        alert.showAndWait();
    }

    public void EntryAlreadyExists(String entryAlreadyExists) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(entryAlreadyExists);
        alert.showAndWait();
    }

    public void setButtonPressed(String s) {
        buttonPressed = s;
    }

    public void newBookAdded() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Success");
        alert.setContentText("New Book Added");
        alert.showAndWait();
    }

    public void currentBookAdded() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Success");
        alert.setContentText("Current Book Added");
        alert.showAndWait();
    }


    public void bookRemoved() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Success");
        alert.setContentText("Book Removed");
        alert.showAndWait();
    }
}