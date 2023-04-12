import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.*;

public class CatalogueController implements Initializable {

    String UserName;
    Set<Entry> entries = new HashSet<>();
    Set<LoginInfo.IssuedItem> history = new HashSet<>();
    LoginInfo loginInfo;

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
    private TableColumn<Entry, String> CheckedOutView;
    @FXML
    private Pane DashPane;
    @FXML
    private Button DashButton;
    @FXML
    private ListView<String> CartList;
    @FXML
    private Button CheckoutEntryButton;
    @FXML
    private Button DeleteEntryButton;
    @FXML
    private Button ResetEntryButton;
    @FXML
    private TextFlow CartText;
    @FXML
    private Button SearchButton;
    @FXML
    private TextField SearchBar;
    @FXML
    private Button ResetSearch;
    @FXML
    private TextFlow CurrentlyIssuedLabel;
    @FXML
    private ListView<String> CurrentlyIssuedList;
    @FXML
    private Button BackButtonOnCheckOutScreen;
    @FXML
    private ListView<String> CartListToCheckOut;
    @FXML
    private DatePicker DatePicker;
    @FXML
    private Button FinalizeCheckOutButton;
    @FXML
    private Pane FinalizeScreen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Catalogue Controller Initialized");
        //set the maininterface pane to the dashboard pane
        MainInterfacePane.getChildren().clear();
        MainInterfacePane.getChildren().add(DashPane);
        CartList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedItem = CartList.getSelectionModel().getSelectedItem();
                CartList.getItems().remove(selectedItem);
            }
        });
        CartList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        CartText.getChildren().add(new javafx.scene.text.Text("Your Cart:"));
        CurrentlyIssuedLabel.getChildren().add(new javafx.scene.text.Text("Currently Checked Out:"));
    }

    public void DashButtonPressed(){
        System.out.println("Dashboard Button Pressed");
        //put dashboard pane on top of main interface pane
        MainInterfacePane.getChildren().clear();
        MainInterfacePane.getChildren().add(DashPane);
        //populate the historyList with the user's history
        CurrentlyIssuedList.getItems().clear();
        populateCurrentlyIssuedList();
    }

    public void populateCurrentlyIssuedList(){
        CurrentlyIssuedList.getItems().clear();
        for (LoginInfo.IssuedItem item : history){
            CurrentlyIssuedList.getItems().add(item.getItem());
        }
    }

    public void CheckOutEntryButtonPressed(){
        System.out.println("Checkout Entry Button Pressed");
        //get all the items in the cart
        ObservableList<String> items = CartList.getItems();

    }

    public void setHistory(HashSet<LoginInfo.IssuedItem> history){
        this.history = history;
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

    public void setLoginInfo(LoginInfo loginInfo){
        this.loginInfo = loginInfo;
    }

    public void CheckoutButtonPressed(){
        System.out.println("Checkout Button Pressed");
        //put checkout pane on top of main interface pane
        MainInterfacePane.getChildren().clear();
        MainInterfacePane.getChildren().add(CheckOutPane);
        populateTableView();
    }

    public void SearchButtonPressed(){
        //check the contents of the search bar
        String SearchText = SearchBar.getText();
        //if the search bar is empty, populate the tableview with all entries
        if(SearchText.equals("")){
            populateTableView();
        }
        //if the search bar is not empty, search for entries that contain the search text
        else{
            //clear the tableview
            TableView.getItems().clear();
            //create a new list to store the entries in
            List<Entry> entryList = new ArrayList<>();
            //loop through the entries
            for(Entry entry : entries){
                //if the entry contains the search text, add it to the list
                if(entry.getTitle().contains(SearchText) || entry.getAuthor().contains(SearchText) || entry.getGenre().contains(SearchText)){
                    entryList.add(entry);
                }
            }
            //create ObservableList from the entryList
            ObservableList<Entry> data = FXCollections.observableArrayList(entryList);
            //set the items of the TableView to the ObservableList
            TableView.setItems(data);
            //set the cell value factories for each of the columns
            TitleView.setCellValueFactory(new PropertyValueFactory<>("title"));
            AuthorView.setCellValueFactory(new PropertyValueFactory<>("author"));
            GenreView.setCellValueFactory(new PropertyValueFactory<>("genre"));
            CheckedOutView.setCellValueFactory(new PropertyValueFactory<>("available"));
            //if the entry is available, set the color to green
            CheckedOutView.setCellFactory(column -> {
                return new TableCell<Entry, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            if (item.equals("Yes")) {
                                setStyle("-fx-text-fill: green;");
                            } else {
                                setStyle("-fx-text-fill: red;");
                            }
                        }
                    }
                };
            });
        }
    }

    public void ResetSearchPressed(){
        //clear the search bar
        SearchBar.clear();
        //populate the tableview with all entries
        populateTableView();
    }

    public void populateTableView(){
        // Clear the existing items in the TableView
        TableView.getItems().clear();

        // Create a new list to store the entries in
        List<Entry> entryList = new ArrayList<>(entries);

        // Create ObservableList from the entryList
        ObservableList<Entry> data = FXCollections.observableArrayList(entryList);

        // Set the items of the TableView to the ObservableList
        TableView.setItems(data);

        // Set the cell value factories for each of the columns
        TitleView.setCellValueFactory(new PropertyValueFactory<>("title"));
        AuthorView.setCellValueFactory(new PropertyValueFactory<>("author"));
        GenreView.setCellValueFactory(new PropertyValueFactory<>("genre"));
        CheckedOutView.setCellValueFactory(new PropertyValueFactory<>("available"));
        //if the entry is available, set the color to green
        CheckedOutView.setCellFactory(column -> {
            return new TableCell<Entry, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (item.equals("Yes")) {
                            setStyle("-fx-text-fill: green;");
                        } else {
                            setStyle("-fx-text-fill: red;");
                        }
                    }
                }
            };
        });
    }

    public void selectEntry(){
        //check if the entry is available and already in the CartList
        //if the entry is availble and not in the CartList, add it to the CartList
        Entry selectedEntry = TableView.getSelectionModel().getSelectedItem();
        if(selectedEntry.getAvailable().equals("Yes") && !CartList.getItems().contains(selectedEntry.getTitle())){
            System.out.println(selectedEntry.getTitle());
            CartList.getItems().add(selectedEntry.getTitle());
        }
    }

    public void DeleteEntryButtonPressed(){
        //remove all the selected items from the CartList
        ObservableList<String> selectedItems = CartList.getSelectionModel().getSelectedItems();
        CartList.getItems().removeAll(selectedItems);
    }

    public void ResetEntryButtonPressed(){
        //clear the CartList
        CartList.getItems().clear();
    }

    public void goToCheckout(){
        //put checkout pane on top of main interface pane
        if(CartList.getItems().size() > 0){
            MainInterfacePane.getChildren().clear();
            MainInterfacePane.getChildren().add(FinalizeScreen);
            //put all the cartlist items into the cartlisttocheckout
            CartListToCheckOut.getItems().clear();
            for(String item : CartList.getItems()){
                CartListToCheckOut.getItems().add(item);
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No items in cart");
            alert.setContentText("Please add items to your cart before checking out");
            alert.showAndWait();
        }
    }

    public void BackButtonOnCheckoutPressed(){
        //put the previous screen the user was in on the main interface
        MainInterfacePane.getChildren().clear();
        MainInterfacePane.getChildren().add(CheckOutPane);
    }

}
