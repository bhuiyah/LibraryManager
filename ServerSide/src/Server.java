import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
//import gson
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Server extends Observable {
    Entry[] books;
    Set<LoginInfo> loginInfo;
    int clientCounter = 0;
    Set<Socket> sockets;
    ObjectOutputStream out;
    public static void main(String[] args) {
        new Server().runServer();
    }

    private void runServer() {
        try {
            Gson gson = new Gson();
            books = gson.fromJson(new FileReader("src/Entries.json"), Entry[].class);
            //get the LoginInfo data from loginInfo.json and store it in loginInfo
            LoginInfo[] loginInfos = gson.fromJson(new FileReader("src/loginInfo.json"), LoginInfo[].class);
            loginInfo = new HashSet(Arrays.asList(loginInfos));
            sockets = new HashSet<>();
            setUpNetworking();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        ServerSocket serverSock = new ServerSocket(4242);
        while (true) {
            Socket clientSocket = serverSock.accept();
            System.out.println("Connecting to... " + clientSocket);
            clientCounter++;
            sockets.add(clientSocket);
            ClientHandler handler = new ClientHandler(this, clientSocket);
            this.addObserver(handler);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            //send books to client
            out.writeObject(books);
            Thread t = new Thread(handler);
            t.start();
        }
    }

    public void processRegistration(String username, String password, ClientHandler clientHandler) {
        //make a new loginInfo object
        LoginInfo info = new LoginInfo(username, password, new ArrayList<>());
        //check if info is in database
        for (LoginInfo login : loginInfo) {
            if (login.getUserName().equals(username)) {
                clientHandler.sendToClient("ALREADY REGISTERED " + username);
                return;
            }
        }
        Gson gson = new Gson();
        String json = gson.toJson(loginInfo);
        //add to loginInfo array
        loginInfo.add(info);
        clientHandler.sendToClient("REGISTERED: " + username);
    }

    public void processLogin(String username, String password, ClientHandler clientHandler) {
        LoginInfo info = new LoginInfo(username, password, new ArrayList<>());
        //check if info is in database
        for (LoginInfo login : loginInfo) {
            if (login.getUserName().equals(username) && login.getPassword().equals(password)){
                //send the login info to the client
                Gson gson = new Gson();
                String json = gson.toJson(login);
                //add a code along with the json
                clientHandler.sendToClient("LOGIN INFORMATION " + json);
                clientHandler.sendToClient("LOGGED IN: " + username);
                return;
            }
        }
        clientHandler.sendToClient("INVALID LOGIN: " + username);
    }

    public void processClientLogOff(ClientHandler clientHandler) throws IOException {
        clientCounter--;
        sockets.remove(clientHandler.getClientSocket());
        clientHandler.sendToClient("LOGOUT");
        clientHandler.getClientSocket().close();
    }

    public void processCheckout(String username, String issuedItem, ClientHandler clientHandler) throws IOException {
        //check if book is in database
        //split issueditem into name, issued date, and due date
        String[] split = issuedItem.split(",");
        String bookName = split[0];
        String issuedDate = split[1];
        String dueDate = split[2];
        for (Entry book : books) {
            if (book.getTitle().equals(bookName)) {
                //check if book is available
                if (Objects.equals(book.getAvailable(), "Yes")) {
                    //check if user has already checked out the book
                    for (LoginInfo login : loginInfo) {
                        if (login.getUserName().equals(username)) {
                            for (LoginInfo.IssuedItem bookName2 : login.getIssuedItems()) {
                                if (bookName2.getItem().equals(bookName)) {
                                    clientHandler.sendToClient("ALREADY CHECKED OUT " + issuedItem);
                                }
                            }
                            login.getIssuedItems().add(new LoginInfo.IssuedItem(bookName, issuedDate, dueDate));
                            book.setAvailable("No");
                            clientHandler.sendToClient("CHECKED OUT " + issuedItem);
                            out = new ObjectOutputStream(clientHandler.getClientSocket().getOutputStream());
                            //send books to client
                            out.writeObject(books);
                            //have all the clients update their books using Observable and Observer
//                            setChanged();
//                            notifyObservers(books);
                        }
                    }
                }
                else {
                    clientHandler.sendToClient("NOT AVAILABLE " + bookName);
                }
            }
        }
        clientHandler.sendToClient("NOT FOUND " + bookName);
    }
}