import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

    private static String host = "20.172.64.102";
    private BufferedReader fromServer;
    private PrintWriter toServer;
    static Parent root;
    static FXMLLoader loader;
    public static Scene scene;
    public static Stage stage;
    public volatile LoginController loginController;
    public volatile CatalogueController catalogueController;
    public volatile AdminController adminController;
    private Thread writerThread;
    Socket socket;
    HashMap<String, Entry> books;
    String username = "";
    LoginInfo loginInfo;
    Admin admin;
    static Type EntryHashMapType;
    static Type LoginInfoHashMapType;
    HashMap<String, LoginInfo> loginInfoHashMap;

    public static void main(String[] args) {
        EntryHashMapType = new TypeToken<HashMap<String, Entry>>(){}.getType();
        LoginInfoHashMapType = new TypeToken<HashMap<String, LoginInfo>>(){}.getType();
        launch(args);
    }

    public String gethost(){
        return host;
    }

    private void setUpNetworking() throws Exception {
        socket = new Socket(host, 4242);
        System.out.println("Connecting to... " + socket);
        toServer = new PrintWriter(socket.getOutputStream());
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while(true) {
            try {
                books = (HashMap<String, Entry>) new ObjectInputStream(socket.getInputStream()).readObject();
                break;
            } catch (EOFException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String input;
                try {
                    while (!socket.isClosed() && (input = fromServer.readLine()) != null) {
                        System.out.println("From server: " + input);
                        if (input.startsWith("REGISTERED")) {
                            accessCatalogue(books);
                        } else if (input.startsWith("REGISTRATION INFORMATION+")) {
                            //input will have a "REGISTRATION INFORMATION" followed by a space and then the login info in the form of a json string
                            //get rid of REGISTRATION INFORMATION and grab the json string
                            //split input by + and grab the second part
                            String[] tokens = input.split("\\+");
                            String json = tokens[1];
                            Gson gson = new Gson();
                            loginInfo = gson.fromJson(json, LoginInfo.class);
                        } else if (input.startsWith("LOGGED IN")) {
                            accessCatalogue(books);
                        } else if (input.startsWith("INVALID LOGIN")) {
                            Platform.runLater(() -> {
                                loginController.setLoginError("Invalid username or password");
                            });
                        } else if (input.startsWith("LOGIN INFORMATION+")) {
                            //input will have a "LOGIN INFORMATION" followed by a space and then the login info in the form of a json string
                            //get rid of LOGIN INFORMATION and grab the json string
                            String[] tokens = input.split("\\+");
                            String json = tokens[1];
                            System.out.println(json);
                            Gson gson = new Gson();
                            loginInfo = gson.fromJson(json, LoginInfo.class);
                        }
//                        else if(input.startsWith("INVALID REGISTER")){
//                            loginController.setRegisterError("Invalid Register");
//                        }
                        else if (input.startsWith("ALREADY REGISTERED")) {
                            Platform.runLater(() -> {
                                loginController.setRegisterError("Already Registered");
                            });
                        }
                        else if(input.startsWith("ALREADY LOGGED IN+")){
                            Platform.runLater(() -> {
                                loginController.setLoginError("Already Logged In");
                            });
                        }
                        else if (input.startsWith("LOGOUT")) {
                            try {
                                //close the gui
                                Platform.runLater(() -> {
                                    stage.close();
                                });
                                socket.close();
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (input.startsWith("CHECKEDOUT+")) {
                            String[] split = input.split("\\+");
                            String lib = split[1];
                            //convert books to a set of entries
                            Gson gson = new Gson();
                            //expecting Collections<Entry>
                            books = gson.fromJson(lib, EntryHashMapType);
                            //update the catalogue
                            Platform.runLater(() -> {
                                catalogueController.setEntries(books);
                                catalogueController.checkoutComplete();
                            });
                        } else if (input.startsWith("CHECKEDOUTUSERNAME+")) {
                            String[] split = input.split("\\+");
                            String log = split[1];
                            //convert log to a loginInfo object
                            Gson gson = new Gson();
                            loginInfo = gson.fromJson(log, LoginInfo.class);
                            catalogueController.setLoginInfo(loginInfo);
                        } else if (input.startsWith("UPDATELIBRARY+")) {
                            String[] split = input.split("\\+");
                            String lib = split[1];
                            //convert books to a set of entries
                            Gson gson = new Gson();
                            //expecting Collections<Entry>
                            books = gson.fromJson(lib, EntryHashMapType);

                            //books should have all the keys of bookMap
                            //update the catalogue
                            Platform.runLater(() -> {
                                //check if the controller is null
                                if (catalogueController != null) {
                                    catalogueController.setEntries(books);
                                }
                            });
                            Platform.runLater(() -> {
                                //check if the controller is null
                                if (adminController != null) {
                                    adminController.setEntries(books);
                                }
                            });
                        } else if (input.startsWith("RETURNEDUSERNAME+")) {
                            String[] split = input.split("\\+");
                            String log = split[1];
                            //convert log to a loginInfo object
                            Gson gson = new Gson();
                            loginInfo = gson.fromJson(log, LoginInfo.class);
                            catalogueController.setLoginInfo(loginInfo);
                            if(adminController != null){
                                Platform.runLater(() -> {
                                    adminController.setEntries(books);
                                });
                            }
                        } else if (input.startsWith("RETURNED+")) {
                            String[] split = input.split("\\+");
                            String lib = split[1];
                            //convert books to a set of entries
                            Gson gson = new Gson();
                            //expecting Collections<Entry>
                            books = gson.fromJson(lib, EntryHashMapType);
                            //update the catalogue
                            if(catalogueController != null){
                                Platform.runLater(() -> {
                                    catalogueController.setEntries(books);
                                    catalogueController.returnComplete();
                                });
                            }
                            if(adminController != null){
                                Platform.runLater(() -> {
                                    adminController.setEntries(books);
                                });
                            }
                        }
                        else if(input.startsWith("ADMININFO+")){
                            String[] tokens = input.split("\\+");
                            String json = tokens[1];
                            System.out.println(json);
                            Gson gson = new Gson();
                            admin = gson.fromJson(json, Admin.class);
                        }
                        else if(input.startsWith("ADMINUSERMANAGEMENT+")){
                            String[] tokens = input.split("\\+");
                            String json = tokens[1];
                            Gson gson = new Gson();
                            loginInfoHashMap = gson.fromJson(json, LoginInfoHashMapType);
                        }
                        else if(input.startsWith("ADMINLOGGEDIN+")){
                            accessAdminPage();
                        }
                        else if(input.startsWith("INVALIDADMINLOGIN+")){
                            Platform.runLater(() -> {
                                loginController.setLoginError("Invalid Admin username or password");
                            });
                        }
                        else if(input.startsWith("BOOKDOESNOTEXIST+")){
                            //get the book name
                            if(adminController != null){
                                Platform.runLater(() -> {
                                    adminController.EntryDoesNotExist("Entry does not exist");
                                });
                            }
                        }
                        else if(input.startsWith("BOOKALREADYEXISTS+")){
                            if(adminController != null){
                                Platform.runLater(() -> {
                                    adminController.EntryAlreadyExists("Entry already exists");
                                });
                            }
                        }
                        else if(input.startsWith("UPDATEDLOGININFO+")){
                            //input will have a "UPDATEDLOGININFO" followed by a space and then the login info in the form of a json string
                            //get rid of UPDATEDLOGININFO and grab the json string
                            String[] tokens = input.split("\\+");
                            String json = tokens[1];
                            Gson gson = new Gson();
                            loginInfoHashMap = gson.fromJson(json, LoginInfoHashMapType);
                            //update the admin page
                            if(adminController != null){
                                Platform.runLater(() -> {
                                    adminController.setLoginInfo(loginInfoHashMap);
                                });
                            }
                        }
                        else if(input.startsWith("NEWBOOKADDED+")){
                            //alert the admin page that a new book has been added
                            if(adminController != null){
                                Platform.runLater(() -> {
                                    adminController.newBookAdded();
                                });
                            }
                        }
                        else if(input.startsWith("CURRENTBOOKADDED+")){
                            //alert the admin page that a new book has been added
                            if(adminController != null){
                                Platform.runLater(() -> {
                                    adminController.currentBookAdded();
                                });
                            }
                        }
                        else if(input.startsWith("BOOKREMOVED+")){
                            //alert the admin page that a book has been removed
                            if(adminController != null){
                                Platform.runLater(() -> {
                                    adminController.bookRemoved();
                                });
                            }
                        }
                        else if(input.startsWith("PASSWORD TOO SHORT+")){
                            Platform.runLater(() -> {
                                loginController.setLoginError("Password must be at least 8 characters long");
                            });
                        }
                        else if(input.startsWith("PASSWORD MUST HAVE A NUMBER+")){
                            Platform.runLater(() -> {
                                loginController.setLoginError("Password must have at least one number");
                            });
                        }
                        else if(input.startsWith("PASSWORD MUST HAVE A SPECIAL CHARACTER+")){
                            Platform.runLater(() -> {
                                loginController.setLoginError("Password must have at least one special character");
                            });
                        }
                        else if(input.startsWith("PASSWORD MUST HAVE AN UPPERCASE LETTER+")){
                            Platform.runLater(() -> {
                                loginController.setLoginError("Password must have at least one uppercase letter");
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                stage.setOnCloseRequest(EventListener -> {
                    try {
                        sendToServer("LOGOUT");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                while (!socket.isClosed() && !Thread.interrupted()) {
                    loginController = loader.getController();
                    if (loginController.buttonPressed.equals("Register")) {
                        if (loginController != null && !Objects.equals(loginController.getUserName(), "") && !Objects.equals(loginController.getPassword(), "")) {
                            String message = "REGISTER:" + loginController.getUserName() + ":" + loginController.getPassword();
                            try {
                                sendToServer(message);
                                username = loginController.getUserName();
                                loginController.setUserName("");
                                loginController.setPassword("");
                                loginController.setButtonPressed("");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else if (loginController.buttonPressed.equals("Login")) {
                        loginController = loader.getController();
                        if (loginController != null && !Objects.equals(loginController.getUserName(), "") && !Objects.equals(loginController.getPassword(), "")) {
                            String message = "LOGIN:" + loginController.getUserName() + ":" + loginController.getPassword();
                            try {
                                sendToServer(message);
                                username = loginController.getUserName();
                                loginController.setUserName("");
                                loginController.setPassword("");
                                loginController.setButtonPressed("");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else if(loginController.buttonPressed.equals("AdminLogin")){
                        loginController = loader.getController();
                        if (loginController != null && !Objects.equals(loginController.getAdminUserName(), "") && !Objects.equals(loginController.getAdminPassword(), "") && !Objects.equals(loginController.getAdminID(), "")){
                            String message = "ADMINLOGIN:" + loginController.getAdminUserName() + ":" + loginController.getAdminPassword() + ":" + loginController.getAdminID();
                            try {
                                sendToServer(message);
                                username = loginController.getUserName();
                                loginController.setUserName("");
                                loginController.setPassword("");
                                loginController.setButtonPressed("");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });
        writerThread.start();
        readerThread.start();
    }

    protected void accessCatalogue(HashMap<String, Entry> books) {
        writerThread.interrupt();
        Platform.runLater(() -> {
            try {
                loader = new FXMLLoader(getClass().getResource("Catalogue.fxml"));
                root = loader.load();
                catalogueController = loader.getController();
                catalogueController.setClient(this);
                catalogueController.setUserName(username);
                catalogueController.setEntries(books);
                catalogueController.setLoginInfo(loginInfo);
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Library");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Catalogue accessed");
        //create a new thread for the catalogue
        Thread writerThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                //delay so that the catalogue controller is loaded
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("running");
                catalogueController = loader.getController();
                    while (!socket.isClosed()) {
                        catalogueController = loader.getController();
                        if (catalogueController.buttonPressed.equals("CheckOut")) {
                            if (catalogueController != null && catalogueController.getCheckOutList().length() != 0) {
                                String message = "CHECKOUT`" + username + "`" + catalogueController.getCheckOutList();
                                try {
                                    sendToServer(message);
                                    catalogueController.setCheckOutList(new ArrayList<>());
                                    catalogueController.buttonPressed = "";
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        else if(catalogueController.buttonPressed.equals("Return")){
                            if (catalogueController != null && catalogueController.getReturnList().length() != 0) {
                                String message = "RETURN`" + username + "`" + catalogueController.getReturnList();
                                try {
                                    sendToServer(message);
                                    catalogueController.setReturnList(new ArrayList<>());
                                    catalogueController.buttonPressed = "";
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        else if(catalogueController.buttonPressed.equals("Exit")){
                            try {
                                sendToServer("LOGOUT");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                stage.setOnCloseRequest(EventListener -> {
                    try {
                        sendToServer("LOGOUT");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        System.out.println("Starting writer thread");
        writerThread2.start();
    }

    public void accessAdminPage(){
        writerThread.interrupt();
        Platform.runLater(() -> {
            try {
                loader = new FXMLLoader(getClass().getResource("AdminGUI.fxml"));
                root = loader.load();
                adminController = loader.getController();
                adminController.setClient(this);
                adminController.setUserName(username);
                adminController.setAdmin(admin);
                adminController.setLoginInfo(loginInfoHashMap);
                adminController.setEntries(books);
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Admin Page");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Admin page accessed");
        //create a new thread for the adnin page
        Thread writerThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                //delay so that the admin controller is loaded
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("running");
                adminController = loader.getController();
                while (!socket.isClosed()) {
                    adminController = loader.getController();
                    if (adminController.buttonPressed.equals("AddNewEntry")) {
                        if (adminController != null && !Objects.equals(adminController.AddAuthor.getText(), "") && !Objects.equals(adminController.AddTitle.getText(), "") && !Objects.equals(adminController.AddGenre.getText(), "") && !Objects.equals(adminController.AddType.getText(), "") && !Objects.equals(adminController.NewCount.getText(), "") && !Objects.equals(adminController.AddDescription.getText(), "") && !Objects.equals(adminController.AddURL.getText(), "")) {
                            String message = "ADDNEWENTRY`" + adminController.AddTitle.getText() + "`" + adminController.AddAuthor.getText() + "`" + adminController.AddGenre.getText() + "`" + adminController.AddType.getText() + "`" + adminController.NewCount.getText() + "`" + adminController.AddDescription.getText() + "`" + adminController.AddURL.getText();
                            try {
                                sendToServer(message);
                                adminController.setAddTitleText("");
                                adminController.setAddAuthorText("");
                                adminController.setAddGenreText("");
                                adminController.setAddTypeText("");
                                adminController.setNewCountText("");
                                adminController.setButtonPressed("");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else if(adminController.buttonPressed.equals("AddCurrentEntry")){
                        if(adminController != null && !Objects.equals(adminController.ExistingCount.getText(), "") && !Objects.equals(adminController.EntriesDropDown.getValue(), "")){
                            String message = "ADDCURRENTENTRY:" + adminController.ExistingCount.getText() + ":" + adminController.EntriesDropDown.getValue();
                            try {
                                sendToServer(message);
                                adminController.setExistingCountText("");
                                //reset the drop down menu
                                adminController.setButtonPressed("");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else if(adminController.buttonPressed.equals("RemoveEntry")){
                        if (adminController != null && !Objects.equals(adminController.getEntriesToBeRemoved(), "" ) ) {
                            String message = "REMOVE:" + adminController.getEntriesToBeRemoved();
                            try {
                                sendToServer(message);
                                adminController.setEntriesToBeRemoved("");
                                adminController.setButtonPressed("");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    else if(adminController.getbuttonPressed().equals("Exit")){
                        try {
                            sendToServer("LOGOUT");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                stage.setOnCloseRequest(EventListener -> {
                    try {
                        sendToServer("LOGOUT");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        System.out.println("Starting writer thread");
        writerThread2.start();
    }

    protected void sendToServer(String info) throws IOException {
            System.out.println("Sending to server: " + info);
            toServer.println(info);
            toServer.flush();
    }

    public void setController(LoginController controller) {
        this.loginController = controller;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        loader = new FXMLLoader(getClass().getResource("LoginGUI.fxml"));
        root = loader.load();
        loginController = loader.getController();
        loginController.setClient(this);
        try {
            setController(loginController);
            new Client().setUpNetworking();

        } catch (Exception e) {
            e.printStackTrace();
        }
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}