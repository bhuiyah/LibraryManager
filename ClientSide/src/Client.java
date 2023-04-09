import java.io.*;
import java.util.Objects;
import java.util.Scanner;
import java.net.Socket;

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
    private Scanner consoleInput = new Scanner(System.in);
    static Parent root;
    static FXMLLoader loader;
    public static Scene scene;
    public static Stage stage;
    public volatile LoginController loginController;
    public volatile CatalogueController catalogueController;
    Entry[] books;
    String username = "";

    public static void main(String[] args) {
        launch(args);
    }

    public String gethost(){
        return host;
    }

    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        Socket socket = new Socket(host, 4242);
        System.out.println("Connecting to... " + socket);
        toServer = new PrintWriter(socket.getOutputStream());
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while(true) {
            try {
                books = (Entry[]) new ObjectInputStream(socket.getInputStream()).readObject();
                break;
            } catch (EOFException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        System.out.println("Books received");
//        for (Entry book : books) {
//            System.out.println(book);
//        }

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String input;
                try {
                    while ((input = fromServer.readLine()) != null) {
                        System.out.println("From server: " + input);
                        if(input.startsWith("REGISTERED")){
                            accessCatalogue(books);
                        }
                        else if(input.startsWith("LOGGED IN")){
                            accessCatalogue(books);
                        }
                        else if(input.startsWith("INVALID LOGIN")){
                            Platform.runLater(() -> {
                                loginController.setRegisterError("Invalid username or password");
                            });
                        }
//                        else if(input.startsWith("INVALID REGISTER")){
//                            loginController.setRegisterError("Invalid Register");
//                        }
                        else if(input.startsWith("ALREADY REGISTERED")){
                            Platform.runLater(() -> {
                                loginController.setRegisterError("Already Registered");
                            });
                        }
//                        else if(input.startsWith("ALREADY LOGGED IN")){
//                            loginController.setLoginError("Already Logged In");
//                        }
//                        else if(input.startsWith("LOGOUT")){
//                            loginController.setLoginError("Logged Out");
//                        }
//                        else if(input.startsWith("BOOKS")){
//                            String[] tokens = input.split(":");
//                            if (tokens.length == 2) {
//                                String books = tokens[1];
//                                catalogueController.setBooks(books);
//                            }
//                        }
//                        else if(input.startsWith("BOOK ADDED")){
//                            String[] tokens = input.split(":");
//                            if (tokens.length == 2) {
//                                String book = tokens[1];
//                                catalogueController.setBookAdded(book);
//                            }
//                        }
//                        else if(input.startsWith("BOOK REMOVED")){
//                            String[] tokens = input.split(":");
//                            if (tokens.length == 2) {
//                                String book = tokens[1];
//                                catalogueController.setBookRemoved(book);
//                            }
//                        }
//                        else if(input.startsWith("BOOK NOT FOUND")){
//                            catalogueController.setBookNotFound();
//                        }
//                        else if(input.startsWith("BOOK ALREADY EXISTS")){
//                            catalogueController.setBookAlreadyExists();
//                        }
//                        else if(input.startsWith("BOOK RENTED")){
//                            String[] tokens = input.split(":");
//                            if (tokens.length == 2) {
//                                String book = tokens[1];
//                                catalogueController.setBookRented(book);
//                            }
//                        }
//                        else if(input.startsWith("BOOK RETURNED")){
//                            String[] tokens = input.split(":");
//                            if (tokens.length == 2) {
//                                String book = tokens[1];
//                                catalogueController.setBookReturned(book);
//                            }
//                        }
//                        else if(input.startsWith("BOOK NOT RENTED")){
//                            catalogueController.setBookNotRented();
//                        }
//                        else if(input.startsWith("BOOK RENTED BY YOU")){
//                            catalogueController.setBookRentedByYou();
//                        }
//                        else if(input.startsWith("BOOK NOT RENTED BY YOU")){
//                            catalogueController.setBookNotRentedByYou();
//                        }
//                        else if(input.startsWith("BOOK RENTED BY OTHER")){
//                            catalogueController.setBookRentedByOther();
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thread writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(loader.getController().getClass().equals(LoginController.class)){
                        loginController = loader.getController();
                        if(loginController.buttonPressed.equals("Register")) {
                            if (loginController != null && !Objects.equals(loginController.getUserName(), "") && !Objects.equals(loginController.getPassword(), "")) {
                                String message = "REGISTER:" + loginController.getUserName() + ":" + loginController.getPassword();
                                try {
                                    sendToServer(message);
                                    username = loginController.getUserName();
                                    loginController.setUserName("");
                                    loginController.setPassword("");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        else if(loginController.buttonPressed.equals("Login")) {
                            loginController = loader.getController();
                            if (loginController != null && !Objects.equals(loginController.getUserName(), "") && !Objects.equals(loginController.getPassword(), "")) {
                                String message = "LOGIN:" + loginController.getUserName() + ":" + loginController.getPassword();
                                try {
                                    sendToServer(message);
                                    username = loginController.getUserName();
                                    loginController.setUserName("");
                                    loginController.setPassword("");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    else if(loader.getController().getClass().equals(CatalogueController.class)){
                        catalogueController = loader.getController();
                    }
                }
            }
        });
        writerThread.start();
        readerThread.start();
    }

    protected void processRequest(String input) {
        return;
    }

    protected void accessCatalogue(Entry[] books) {
        Platform.runLater(() -> {
            try {
                loader = new FXMLLoader(getClass().getResource("Catalogue.fxml"));
                root = loader.load();
                catalogueController = loader.getController();
                catalogueController.setUserName(username);
                catalogueController.setTopBar();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Library");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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