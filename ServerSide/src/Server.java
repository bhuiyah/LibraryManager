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
    public static void main(String[] args) {
        new Server().runServer();
    }

    private void runServer() {
        try {
            Gson gson = new Gson();
            books = gson.fromJson(new FileReader("src/Entries.json"), Entry[].class);
            LoginInfo[] loginArray = gson.fromJson(new FileReader("src/LoginInfo.json"), LoginInfo[].class);
            loginInfo = new HashSet<>(Arrays.asList(loginArray));
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
            ClientHandler handler = new ClientHandler(this, clientSocket);
            this.addObserver(handler);
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            //send books to client
            out.writeObject(books);
            Thread t = new Thread(handler);
            t.start();
        }
    }

    protected void processRequest(String input) {
//        if (input.contains("REGISTER:")) {
//            try {
//                String parsedWord = input.replace("REGISTER:", "");
//                String[] parsedWords = parsedWord.split(",");
//                String username = parsedWords[0];
//                String password = parsedWords[1];
//                LoginInfo info = new LoginInfo(username, password, new ArrayList<String>());
//                //check if info is in database
//                for (LoginInfo login : loginInfo) {
//                    if (login.getUserName().equals(username)) {
//                        this.setChanged();
//                        this.notifyObservers("USER: " + username + " ALREADY EXISTS");
//                        return;
//                    }
//                }
//                Gson gson = new Gson();
//                String json = gson.toJson(loginInfo);
//                //add to database
//                try {
//                    FileWriter file = new FileWriter("src/LoginInfo.json", true);
//                    file.write(json);
//                    file.write("\r \n");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                this.setChanged();
//                this.notifyObservers("NEW USER: " + username + " REGISTERED");
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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
                clientHandler.sendToClient("LOGGED IN: " + username);
                return;
            }
        }
        clientHandler.sendToClient("INVALID LOGIN: " + username);
    }
}