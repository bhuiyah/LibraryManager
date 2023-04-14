import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class LoginInfo implements Serializable {
    private String UserName;
    private String Password;
    private ArrayList<IssuedItem> issuedItems;

    public LoginInfo(String userName, String password, ArrayList<IssuedItem> issuedItems) {
        UserName = userName;
        Password = password;
        this.issuedItems = issuedItems;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public ArrayList<IssuedItem> getIssuedItems() {
        return issuedItems;
    }

    public void setIssuedItems(ArrayList<IssuedItem> issuedItems) {
        this.issuedItems = issuedItems;
    }

    public static LoginInfo fromJson(String json) {
        return new Gson().fromJson(json, LoginInfo.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static class IssuedItem {
        private String item;
        private String issuedDate;
        private String dueDate;

        public IssuedItem(String item, String issuedDate, String dueDate) {
            this.item = item;
            this.issuedDate = issuedDate;
            this.dueDate = dueDate;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getIssuedDate() {
            return issuedDate;
        }

        public void setIssuedDate(String issuedDate) {
            this.issuedDate = issuedDate;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public String toString(){
            //The Catcher in the Rye,2023-04-14,2023-04-28
            return item + "," + issuedDate + "," + dueDate;
        }
    }
}