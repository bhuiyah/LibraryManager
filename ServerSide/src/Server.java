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
    public static void main(String[] args) {
        new Server().runServer();
    }

    private void runServer() {
        try {
            Gson gson = new Gson();
            books = gson.fromJson(new FileReader("src/Entries.json"), Entry[].class);
            LoginInfo[] loginArray = gson.fromJson(new FileReader("src/LoginInfo.json"), LoginInfo[].class);
            loginInfo = new HashSet<>(Arrays.asList(loginArray));
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
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            //send books to client
            out.writeObject(books);
            Thread t = new Thread(handler);
            t.start();
        }
    }

    public void processRegistration(String username, String password, ClientHandler clientHandler) {
        LoginInfo info = new LoginInfo(username, password, new ArrayList<String>());
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
        LoginInfo info = new LoginInfo(username, password, new ArrayList<String>());
        //check if info is in database
        for (LoginInfo login : loginInfo) {
            if (login.getUserName().equals(username) && login.getPassword().equals(password)) {
                //send the login info to the client
                Gson gson = new Gson();
                String json = gson.toJson(login);
                //add a code along with the json
                clientHandler.sendToClient("LOGIN INFORMATION: " + json);
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
}