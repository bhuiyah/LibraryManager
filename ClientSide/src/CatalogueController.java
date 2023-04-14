import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import java.time.LocalDate;

import java.net.URL;
import java.util.*;

public class CatalogueController implements Initializable {

    String UserName;
    Set<Entry> entries = new HashSet<>();
    Set<LoginInfo.IssuedItem> history = new HashSet<>();
    LoginInfo loginInfo;
    ArrayList<LoginInfo.IssuedItem> checkOutList = new ArrayList<>();
    Client client;
    String buttonPressed = "";

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
    private javafx.scene.control.TableView<LoginInfo.IssuedItem> CartListToCheckOut;
    @FXML
    private TableColumn<String, String> ItemOnFinalList;
    @FXML
    private TableColumn<String, String> DueDateOnFinalList;
    @FXML
    private TableColumn<String, String> StartDateOnFinalList;
    @FXML
    private Button FinalizeCheckOutButton;
    @FXML
    private Pane FinalizeScreen;
    @FXML
    private Button FinalizeScreenDeleteButton;
    @FXML
    private TextFlow NoteAboutCheckingOut;


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
        CartListToCheckOut.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setClient(Client client){
        this.client = client;
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
        NoteAboutCheckingOut.getChildren().clear();
        NoteAboutCheckingOut.getChildren().add(new javafx.scene.text.Text("Note: Make sure that you will be able to return the item before the due date. If you cannot, you will be charged a late fee. Please see the librarian for more information."));
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
            populateCartListToCheckOut();
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

    public void populateCartListToCheckOut(){
        //cartlisttocheckout is a tableview, populate with the items in the cartlist, populate the columns with dates
        CartListToCheckOut.getItems().clear();
        List<String> entryList = new ArrayList<>(CartList.getItems());
        ObservableList<String> data = FXCollections.observableArrayList(entryList);
        //make a new Observable List of type IssuedItem that converts all the items in data to an IssuedItem and put in the new list
        ObservableList<LoginInfo.IssuedItem> data2 = FXCollections.observableArrayList();
        for(String item : data){
            data2.add(new LoginInfo.IssuedItem(item, LocalDate.now().toString(), LocalDate.now().plusDays(14).toString()));
        }
        CartListToCheckOut.setItems(data2);
        //set each column of ItemOnFinalList to each item in the cartlist
        ItemOnFinalList.setCellValueFactory(new PropertyValueFactory<>("item"));
        //set each column of StartDateOnFinalList to the current date using LocalDate.now()
        StartDateOnFinalList.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        //set each column of  to the current date + 14 days
        DueDateOnFinalList.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
    }

    public void FinalizeScreenDeleteButtonPressed(){
        //remove all the selected items from the CartListToCheckOut
        ObservableList<LoginInfo.IssuedItem> selectedItems = CartListToCheckOut.getSelectionModel().getSelectedItems();
        CartListToCheckOut.getItems().removeAll(selectedItems);
    }

    public void FinalizeCheckOutButtonPressed(){
        //make each entry in the CartListToCheckOut an IssuedItem and add it to the current user's issued items
        checkOutList.addAll(CartListToCheckOut.getItems());
        buttonPressed = "CheckOut";
    }

    public void setCheckOutList(ArrayList<LoginInfo.IssuedItem> checkOutList){
        this.checkOutList = checkOutList;
    }

    public String getCheckOutList(){
        //return the checkOutList as a string that can be turned into a ArrayList<LoginInfo.IssuedItem> later
        StringBuilder checkOutListString = new StringBuilder();
        for(LoginInfo.IssuedItem item : checkOutList){
            checkOutListString.append(item.getItem()).append(",").append(item.getIssuedDate()).append(",").append(item.getDueDate()).append(";");
        }
        return checkOutListString.toString();
    }

}
