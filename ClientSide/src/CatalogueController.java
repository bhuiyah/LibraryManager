import com.google.gson.internal.LinkedTreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

import java.lang.reflect.Array;
import java.time.LocalDate;

import java.net.URL;
import java.util.*;

public class CatalogueController implements Initializable {

    String UserName;
    HashMap<String, Entry> entries;
    LoginInfo loginInfo;
    ArrayList<LoginInfo.IssuedItem> checkOutList = new ArrayList<>();
    Client client;
    String buttonPressed = "";
    ArrayList<LoginInfo.IssuedItem> returnList = new ArrayList<>();

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
    @FXML
    private TableColumn<String, String> CountView;
    @FXML
    private TableColumn<String, String> TypeView;
    @FXML
    private Pane ReturnScreen;
    @FXML
    private TableView<LoginInfo.IssuedItem> ReturnView;
    @FXML
    private TableColumn<String, String> EntryTitleForReturnTable;
    @FXML
    private TableColumn<String, String> StartDateForReturnView;
    @FXML
    private TableColumn<String, String> DueDateReturnView;
    @FXML
    private TableColumn<String, String> LateForReturnView;
    @FXML
    private TableColumn<String, String> LateFeeView;
    @FXML
    private TextFlow ReturnText;
    @FXML
    private Button ResetButtonReturn;
    @FXML
    private Button ClearSelectedReturnButton;
    @FXML
    private Button FinalizeReturnButton;
    @FXML
    private Button ReturnButton;
    @FXML
    private TableView<LoginInfo.IssuedItem> ReturnCart;
    @FXML
    private TableColumn<String, String> TitleReturnCart;
    @FXML
    private TableColumn<String, String> FeeReturnCart;
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
        ReturnText.getChildren().add(new javafx.scene.text.Text("Items Returning:"));
        ReturnCart.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ReturnCart.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                LoginInfo.IssuedItem selectedItem = ReturnCart.getSelectionModel().getSelectedItem();
                ReturnCart.getItems().remove(selectedItem);
            }
        });
        //set all table columns to the center
        TitleView.setStyle("-fx-alignment: CENTER;");
        AuthorView.setStyle("-fx-alignment: CENTER;");
        GenreView.setStyle("-fx-alignment: CENTER;");
        CheckedOutView.setStyle("-fx-alignment: CENTER;");
        CountView.setStyle("-fx-alignment: CENTER;");
        TypeView.setStyle("-fx-alignment: CENTER;");
        ItemOnFinalList.setStyle("-fx-alignment: CENTER;");
        DueDateOnFinalList.setStyle("-fx-alignment: CENTER;");
        StartDateOnFinalList.setStyle("-fx-alignment: CENTER;");
        EntryTitleForReturnTable.setStyle("-fx-alignment: CENTER;");
        StartDateForReturnView.setStyle("-fx-alignment: CENTER;");
        DueDateReturnView.setStyle("-fx-alignment: CENTER;");
        LateForReturnView.setStyle("-fx-alignment: CENTER;");
        LateFeeView.setStyle("-fx-alignment: CENTER;");
        TitleReturnCart.setStyle("-fx-alignment: CENTER;");
        FeeReturnCart.setStyle("-fx-alignment: CENTER;");
    }

    public void setClient(Client client){
        this.client = client;
    }

    public void DashButtonPressed(){
        System.out.println("Dashboard Button Pressed");
        //put dashboard pane on top of main interface pane
        MainInterfacePane.getChildren().clear();
        MainInterfacePane.getChildren().add(DashPane);
        CurrentlyIssuedList.getItems().clear();
        populateCurrentlyIssuedList();
    }

    public void populateCurrentlyIssuedList(){
        CurrentlyIssuedList.getItems().clear();
        for (LoginInfo.IssuedItem item : loginInfo.getIssuedItems()){
            CurrentlyIssuedList.getItems().add(item.getItem());
        }
    }

    public void CheckOutEntryButtonPressed(){
        System.out.println("Checkout Entry Button Pressed");
        //get all the items in the cart
        ObservableList<String> items = CartList.getItems();
    }

    public void setUserName(String UserName){
        this.UserName = UserName;
    }

    public void setTopBar(){
        TopBar.getChildren().add(new javafx.scene.text.Text("Welcome " + UserName));
    }

    public void setEntries(HashMap<String, Entry> entries){
        //iterate through the entries values, check if it is a LinkedTreeMap, and if it is, convert it to an Entry
        this.entries = entries;
        //update all the tableviews
        populateTableView();
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
            for(Entry entry : entries.values()){
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
            CountView.setCellValueFactory(new PropertyValueFactory<>("count"));
            TypeView.setCellValueFactory(new PropertyValueFactory<>("media_type"));
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
        //the above code is not working for some reason, so I am using the code below
        //get the entry values from the entries hashmap, and put them in the tableview
        TableView.getItems().clear();
        for(Entry entry : entries.values()){
            TableView.getItems().add(entry);
        }
        //set the cell value factories for each of the columns
        TitleView.setCellValueFactory(new PropertyValueFactory<>("title"));
        AuthorView.setCellValueFactory(new PropertyValueFactory<>("author"));
        GenreView.setCellValueFactory(new PropertyValueFactory<>("genre"));
        CheckedOutView.setCellValueFactory(new PropertyValueFactory<>("available"));
        CountView.setCellValueFactory(new PropertyValueFactory<>("count"));
        TypeView.setCellValueFactory(new PropertyValueFactory<>("media_type"));
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
            if(CartList.getItems().size() > 5){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("You have already issued 5 items");
                alert.setContentText("Please return an item before checking out another one");
                alert.showAndWait();
            }
            //check issuedItems + cartList size
            else if(loginInfo.getIssuedItems().size() + CartList.getItems().size() > 5){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("You can only issue 5 items at a time");
                alert.setContentText("Please return an item before checking out another one");
                alert.showAndWait();
            }else {
                MainInterfacePane.getChildren().clear();
                MainInterfacePane.getChildren().add(FinalizeScreen);
                //put all the cartlist items into the cartlisttocheckout
                CartListToCheckOut.getItems().clear();
                populateCartListToCheckOut();
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

    public void checkoutComplete(){
        //clear the cartlist and cartlisttocheckout
        CartList.getItems().clear();
        CartListToCheckOut.getItems().clear();
        //put the main interface pane on top of the checkout pane
        MainInterfacePane.getChildren().clear();
        MainInterfacePane.getChildren().add(DashPane);
        //alert the user that the checkout was successful
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Checkout Complete");
        alert.setHeaderText("Checkout Complete");
        alert.setContentText("Your items have been checked out");
        alert.showAndWait();
    }

    public void ReturnButtonPressed(){
        //put the return pane on top of the main interface pane
        MainInterfacePane.getChildren().clear();
        MainInterfacePane.getChildren().add(ReturnScreen);
        //populate the tableview with the current user's issued items
        populateReturnList();
    }

    public void populateReturnList(){
        //populate the tableview with the current user's issued items
        ReturnView.getItems().clear();
        //make loginInfo.getIssuedItems() into an ObservableList
        ObservableList<LoginInfo.IssuedItem> data = FXCollections.observableArrayList();
        data.addAll(loginInfo.getIssuedItems());
        ReturnView.getItems().addAll(data);
        //set each column of ItemOnReturnList to each item in the cartlist
        EntryTitleForReturnTable.setCellValueFactory(new PropertyValueFactory<>("item"));
        //set each column of StartDateOnReturnList to the current date using LocalDate.now()
        StartDateForReturnView.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        //set each column of  to the current date + 14 days
        DueDateReturnView.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        LateForReturnView.setCellValueFactory(new PropertyValueFactory<>("Late"));
        //if Yes, set the text to red, if No, set the text to green
        LateForReturnView.setCellFactory(column -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (item.equals("Yes")) {
                            setStyle("-fx-text-fill: red;");
                        } else {
                            setStyle("-fx-text-fill: green;");
                        }
                    }
                }
            };
        });
        LateFeeView.setCellValueFactory(new PropertyValueFactory<>("Fee"));
        //if the late fee is greater than 0, set the text to red, if it is 0, set the text to green
        //Fee is in the form of a string, so we have to convert it to a double: $0
        LateFeeView.setCellFactory(column -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (Double.parseDouble(item.substring(1)) > 0) {
                            setStyle("-fx-text-fill: red;");
                        } else {
                            setStyle("-fx-text-fill: green;");
                        }
                    }
                }
            };
        });
    }

    public void ReturnCartSelected(){
        //add the selected items from the ReturnView to the ReturnCart. only get their titles
        ObservableList<LoginInfo.IssuedItem> selectedItems = ReturnView.getSelectionModel().getSelectedItems();
        for(LoginInfo.IssuedItem item : selectedItems){
            ReturnCart.getItems().add(item);
        }
        //cellfactory for the ReturnCart
        TitleReturnCart.setCellValueFactory(new PropertyValueFactory<>("item"));
        //set each column of FeeReturnCart to the late fee of the item
        FeeReturnCart.setCellValueFactory(new PropertyValueFactory<>("Fee"));
    }

    public void ClearSelectedReturnButtonPressed(){
        //remove all the selected items from the ReturnCart
        ObservableList<LoginInfo.IssuedItem> selectedItems = ReturnCart.getSelectionModel().getSelectedItems();
        ReturnCart.getItems().removeAll(selectedItems);
    }

    public void ResetButtonReturnPressed(){
        //clear the ReturnCart
        ReturnCart.getItems().clear();
    }

    public void FinalizeReturnButtonPressed(){
        //make each entry in the ReturnCart an IssuedItem and add it to the current user's issued items
        returnList.addAll(ReturnCart.getItems());
        buttonPressed = "Return";
    }

    public void setReturnList(ArrayList<LoginInfo.IssuedItem> returnList){
        this.returnList = returnList;
    }

    public String getReturnList(){
        //return the returnList as a string that can be turned into a ArrayList<LoginInfo.IssuedItem> later
        StringBuilder returnListString = new StringBuilder();
        for(LoginInfo.IssuedItem item : returnList){
            returnListString.append(item.getItem()).append(",").append(item.getIssuedDate()).append(",").append(item.getDueDate()).append(";");
        }
        return returnListString.toString();
    }

    public void returnComplete(){
        //clear the ReturnCart and ReturnView
        ReturnCart.getItems().clear();
        ReturnView.getItems().clear();
        //put the main interface pane on top of the return pane
        MainInterfacePane.getChildren().clear();
        MainInterfacePane.getChildren().add(DashPane);
        //alert the user that the return was successful
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Return Complete");
        alert.setHeaderText("Return Complete");
        alert.setContentText("Your items have been returned");
        alert.showAndWait();
    }

    public void ExitButtonPressed(){
        //exit the program
        buttonPressed = "Exit";
    }

}
