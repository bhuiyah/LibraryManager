import java.io.Serializable;
import java.util.ArrayList;

public class LoginInfo implements Serializable{

    private String UserName;
    private String Password;
    private ArrayList<String> issuedItems;

    public LoginInfo(String UserName, String Password, ArrayList<String> issuedItems){
        this.UserName = UserName;
        this.Password = Password;
        this.issuedItems = issuedItems;
    }

    public String getUserName(){
        return UserName;
    }

    public String getPassword(){
        return Password;
    }

    public ArrayList<String> getIssuedItems(){
        return issuedItems;
    }

    public String toString(){
        return "Username: " + UserName + " Password: " + Password + " Issued Items: " + issuedItems;
    }

}
