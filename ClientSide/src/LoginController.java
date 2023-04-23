
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    @FXML
    private Button LoginButton;
    @FXML
    private TextField UserTextField;
    @FXML
    private PasswordField PassTextField;
    @FXML
    private Button RegisterButton;
    @FXML
    private Button AdminBackButton;
    @FXML
    private TextField AdminUserField;
    @FXML
    private PasswordField AdminPassField;
    @FXML
    private Button AdminLoginButton;
    @FXML
    private Pane AdminPage;
    @FXML
    private Pane LoginScreen;
    @FXML
    private StackPane MainInterface;
    @FXML
    private Button GoToAdminLogin;
    @FXML
    private PasswordField IDAdminField;


    Client client;
    String UserName = "";
    String Password = "";
    String AdminUserName = "";
    String AdminPassword = "";
    String AdminID = "";

    String buttonPressed = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MainInterface.getChildren().clear();
        MainInterface.getChildren().add(LoginScreen);
        //when the mouse is above any of the LoginButton, RegisterButton, or GoToAdminLogin buttons, the text will change to #C0392B and back to  #EC7063 when the mouse is not hovering over the button
        LoginButton.setOnMouseEntered(event -> {
            LoginButton.setStyle("-fx-background-color: #C0392B");
        });
        LoginButton.setOnMouseExited(event -> {
            LoginButton.setStyle("-fx-background-color: #EC7063");
        });
        RegisterButton.setOnMouseEntered(event -> {
            RegisterButton.setStyle("-fx-background-color: #C0392B");
        });
        RegisterButton.setOnMouseExited(event -> {
            RegisterButton.setStyle("-fx-background-color: #EC7063");
        });
        GoToAdminLogin.setOnMouseEntered(event -> {
            GoToAdminLogin.setStyle("-fx-background-color: #C0392B");
        });
        GoToAdminLogin.setOnMouseExited(event -> {
            GoToAdminLogin.setStyle("-fx-background-color: #EC7063");
        });
        AdminBackButton.setOnMouseEntered(event -> {
            AdminBackButton.setStyle("-fx-background-color: #C0392B");
        });
        AdminBackButton.setOnMouseExited(event -> {
            AdminBackButton.setStyle("-fx-background-color: #EC7063");
        });
        AdminLoginButton.setOnMouseEntered(event -> {
            AdminLoginButton.setStyle("-fx-background-color: #C0392B");
        });
        AdminLoginButton.setOnMouseExited(event -> {
            AdminLoginButton.setStyle("-fx-background-color: #EC7063");
        });

        //change the mouse cursor to a hand when hovering over the buttons
        LoginButton.setCursor(javafx.scene.Cursor.HAND);
        RegisterButton.setCursor(javafx.scene.Cursor.HAND);
        GoToAdminLogin.setCursor(javafx.scene.Cursor.HAND);
        AdminBackButton.setCursor(javafx.scene.Cursor.HAND);
        AdminLoginButton.setCursor(javafx.scene.Cursor.HAND);

        //when the mouse is clicked on the username or password text fields, the color of the background will change to #C0392B. when another text field is clicked, the color will change back to #EC7063
        UserTextField.setOnMouseClicked(event -> {
            UserTextField.setStyle("-fx-background-color: #F1948A");
            PassTextField.setStyle("-fx-background-color: #EC7063");
        });
        PassTextField.setOnMouseClicked(event -> {
            PassTextField.setStyle("-fx-background-color: #F1948A");
            UserTextField.setStyle("-fx-background-color: #EC7063");
        });
        AdminUserField.setOnMouseClicked(event -> {
            AdminUserField.setStyle("-fx-background-color: #F1948A");
            AdminPassField.setStyle("-fx-background-color: #EC7063");
            IDAdminField.setStyle("-fx-background-color: #EC7063");
        });
        AdminPassField.setOnMouseClicked(event -> {
            AdminPassField.setStyle("-fx-background-color: #F1948A");
            AdminUserField.setStyle("-fx-background-color: #EC7063");
            IDAdminField.setStyle("-fx-background-color: #EC7063");
        });
        IDAdminField.setOnMouseClicked(event -> {
            IDAdminField.setStyle("-fx-background-color: #F1948A");
            AdminUserField.setStyle("-fx-background-color: #EC7063");
            AdminPassField.setStyle("-fx-background-color: #EC7063");
        });
    }

    public void LoginSelected() {
        System.out.println("Login Selected");
        buttonPressed = "Login";
        if (UserTextField.getText() != null && PassTextField.getText() != null) {
            System.out.println("Username: " + UserTextField.getText());
            System.out.println("Password: " + PassTextField.getText());
            UserName = UserTextField.getText();
            Password = PassTextField.getText();
        }
    }

    public void RegisterSelected(){
        System.out.println("Register Selected");
        buttonPressed = "Register";
        if(!Objects.equals(UserTextField.getText(), "") && !Objects.equals(PassTextField.getText(), "")){
            UserName = UserTextField.getText();
            Password = PassTextField.getText();
            System.out.println("Username: " + UserTextField.getText());
            System.out.println("Password: " + PassTextField.getText());

//            client.sendToServer("REGISTER:" + UserTextField.getText() + " " + PassTextField.getText());
        }
        else{
            System.out.println("Username or Password is empty");
        }
    }

    //getter functions for username and password

    public String getUserName(){
        return UserName;
    }

    public String getPassword(){
        return Password;
    }

    public String getButtonPressed(){
        return buttonPressed;
    }


    public void setClient(Client client) {
        this.client = client;
        System.out.println(client.gethost());
    }

    public void setUserName(String s) {
        UserName = s;
    }

    public void setPassword(String s) {
        Password = s;
    }

    public void setAdminUserName(String s) {
        AdminUserName = s;
    }

    public void setAdminPassword(String s) {
        AdminPassword = s;
    }

    public void setAdminID(String s) {
        AdminID = s;
    }

    public String getAdminUserName() {
        return AdminUserName;
    }

    public String getAdminPassword() {
        return AdminPassword;
    }

    public String getAdminID() {
        return AdminID;
    }


    public void setButtonPressed(String s) {
        buttonPressed = s;
    }

    public void setLoginError(String invalidLogin) {
        //send an alert error message
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Login Error");
        alert.setContentText(invalidLogin);
        alert.showAndWait();
    }

    public void setRegisterError(String alreadyRegistered) {
        //send an alert error message
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Registration Error");
        alert.setContentText(alreadyRegistered);
        alert.showAndWait();
    }

    public void GoToAdminLogin() {
        MainInterface.getChildren().clear();
        MainInterface.getChildren().add(AdminPage);
    }

    public void AdminBackButton() {
        MainInterface.getChildren().clear();
        MainInterface.getChildren().add(LoginScreen);
    }

    public void AdminLoginButton() {
        if (AdminUserField.getText() != null && AdminPassField.getText() != null && IDAdminField.getText() != null) {
            System.out.println("Username: " + AdminUserField.getText());
            System.out.println("Password: " + AdminPassField.getText());
            System.out.println("ID: " + IDAdminField.getText());
            AdminUserName = AdminUserField.getText();
            AdminPassword = AdminPassField.getText();
            AdminID = IDAdminField.getText();
            buttonPressed = "AdminLogin";
        }
    }





}
