
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.TextFlow;

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
    private TextFlow ErrorMessage;

    Client client;
    String UserName = "";
    String Password = "";

    String buttonPressed = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

    public void setButtonPressed(String s) {
        buttonPressed = s;
    }

    public void setLoginError(String invalidLogin) {
        Text text = new Text(invalidLogin);
        text.setFill(Color.RED);
        ErrorMessage.getChildren().clear();
        ErrorMessage.getChildren().add(text);
    }

    public void setRegisterError(String alreadyRegistered) {
        Text text = new Text(alreadyRegistered);
        text.setFill(Color.RED);
        ErrorMessage.getChildren().clear();
        ErrorMessage.getChildren().add(text);
    }
}
