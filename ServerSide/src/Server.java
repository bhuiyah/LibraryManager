import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
//import gson
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Server extends Observable {
    Entry[] books;
    HashMap<String, LoginInfo> loginInfo;
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
            loginInfo = new HashMap<>();
            for (LoginInfo info : loginInfos) {
                loginInfo.put(info.getUserName(), info);
            }
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
//        for (LoginInfo login : loginInfo) {
//            if (login.getUserName().equals(username)) {
//                clientHandler.sendToClient("ALREADY REGISTERED " + username);
//                return;
//            }
//        }
        //look through the hashmap to see if the username is already in use
        if (loginInfo.containsKey(username)) {
            clientHandler.sendToClient("ALREADY REGISTERED " + username);
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(info);
        //add to loginInfo array
        loginInfo.put(username, info);
        clientHandler.sendToClient("REGISTRATION INFORMATION+" + json);
        clientHandler.sendToClient("REGISTERED: " + username);
    }

    public void processLogin(String username, String password, ClientHandler clientHandler) {
        LoginInfo info = new LoginInfo(username, password, new ArrayList<>());
        //check if info is in database
        //look through the hashmap to see if the username is already in use
        if (loginInfo.containsKey(username)) {
            //check if the password is correct
            if (loginInfo.get(username).getPassword().equals(password)) {
                //send the login info to the client
                Gson gson = new Gson();
                String json = gson.toJson(loginInfo.get(username));
                //add a code along with the json
                clientHandler.sendToClient("LOGIN INFORMATION+" + json);
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
        //remove the client from the observers
        this.deleteObserver(clientHandler);
        clientHandler.getClientSocket().close();
    }

    public void processCheckout(String username, String issuedItem, ClientHandler clientHandler) throws IOException {
        //check if book is in database
        //split issueditem into name, issued date, and due date
        //issuedItem is coming like this: The Grapes of Wrath,2023-04-15,2023-04-29;Pride and Prejudice,2023-04-15,2023-04-29;The Catcher in the Rye,2023-04-15,2023-04-29;
        String[] items = issuedItem.split(";");
        //loop through the items
        for (String item : items) {
            //split the item into name, issued date, and due date
            String[] itemInfo = item.split(",");
            //check if the book is in the database
            for (Entry book : books) {
                if (book.getTitle().equals(itemInfo[0])) {
                    //check if the book is available
                    if (book.getAvailable().equals("Yes")) {
                        //set the book to unavailable
                        if(book.getCount() - 1 == 0) {
                            book.setAvailable("No");
                        }
                        book.setCount(book.getCount() - 1);
                        loginInfo.get(username).getIssuedItems().add(new LoginInfo.IssuedItem(itemInfo[0], itemInfo[1], itemInfo[2]));
                    } else {
                        //send the book is not available message to the client
                        clientHandler.sendToClient("BOOK NOT AVAILABLE: " + itemInfo[0]);
                    }
                }
            }
        }
        //send the updated books to the client
        //get the loginInfo from the username passed in
        Gson gson = new Gson();
        String json = gson.toJson(loginInfo.get(username));
        clientHandler.sendToClient("CHECKEDOUTUSERNAME+" + json);
        json = gson.toJson(books);
        clientHandler.sendToClient("CHECKEDOUT+" + json);
        //have all the clients update their books using Observable
        setChanged();
        notifyObservers(books);
    }
}