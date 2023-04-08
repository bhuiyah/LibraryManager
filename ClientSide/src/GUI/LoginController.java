package GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button LoginButton;
    @FXML
    private TextField UserTextField;
    @FXML
    private TextField PassTextField;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void LoginSelected(){
        System.out.println("Login Selected");
        if(UserTextField.getText() == null || UserTextField.getText().equals("")) {
            System.out.println("Username is empty");
        }
        else{
            System.out.println("Username: " + UserTextField.getText());
        }
        if(PassTextField.getText() == null || PassTextField.getText().equals("")) {
            System.out.println("Password is empty");
        }
        else{
            System.out.println("Password: " + PassTextField.getText());
        }
    }




}
