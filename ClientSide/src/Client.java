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

    private static String host = "127.0.0.1";
    private BufferedReader fromServer;
    private PrintWriter toServer;
    static Parent root;
    static FXMLLoader loader;
    public static Scene scene;
    public static Stage stage;
    public volatile LoginController loginController;
    public volatile CatalogueController catalogueController;
    private Thread writerThread;
    Socket socket;
    HashMap<String, Entry> books;
    String username = "";
    LoginInfo loginInfo;
    static Type type;

    public static void main(String[] args) {
        type = new TypeToken<HashMap<String, Entry>>(){}.getType();
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
                                loginController.setRegisterError("Invalid username or password");
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
//                        else if(input.startsWith("ALREADY LOGGED IN")){
//                            loginController.setLoginError("Already Logged In");
//                        }
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
                            books = gson.fromJson(lib, type);
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
                            catalogueController.populateCurrentlyIssuedList();
                        } else if (input.startsWith("UPDATELIBRARY+")) {
                            String[] split = input.split("\\+");
                            String lib = split[1];
                            //convert books to a set of entries
                            Gson gson = new Gson();
                            //expecting Collections<Entry>
                            books = gson.fromJson(lib, type);

                            //books should have all the keys of bookMap
                            //update the catalogue
                            Platform.runLater(() -> {
                                catalogueController.setEntries(books);
                            });
                        } else if (input.startsWith("RETURNEDUSERNAME+")) {
                            String[] split = input.split("\\+");
                            String log = split[1];
                            //convert log to a loginInfo object
                            Gson gson = new Gson();
                            loginInfo = gson.fromJson(log, LoginInfo.class);
                            catalogueController.setLoginInfo(loginInfo);
                            catalogueController.populateCurrentlyIssuedList();
                        } else if (input.startsWith("RETURNED+")) {
                            String[] split = input.split("\\+");
                            String lib = split[1];
                            //convert books to a set of entries
                            Gson gson = new Gson();
                            //expecting Collections<Entry>
                            books = gson.fromJson(lib, type);
                            //update the catalogue
                            Platform.runLater(() -> {
                                catalogueController.setEntries(books);
                                catalogueController.returnComplete();
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
                catalogueController.setTopBar();
                catalogueController.setEntries(books);
                catalogueController.setLoginInfo(loginInfo);
                catalogueController.populateCurrentlyIssuedList();
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
                                String message = "CHECKOUT:" + username + ":" + catalogueController.getCheckOutList();
                                try {
                                    sendToServer(message);
                                    catalogueController.setCheckOutList(new ArrayList<>());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        else if(catalogueController.buttonPressed.equals("Return")){
                            if (catalogueController != null && catalogueController.getReturnList().length() != 0) {
                                String message = "RETURN:" + username + ":" + catalogueController.getReturnList();
                                try {
                                    sendToServer(message);
                                    catalogueController.setReturnList(new ArrayList<>());
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