import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.time.LocalDate;
//import gson
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.sun.xml.internal.bind.v2.util.TypeCast;
import org.bson.*;
import org.bson.conversions.Bson;

class Server extends Observable {
    static HashMap<String, Entry> books;
    static HashMap<String, LoginInfo> loginInfo;
    int clientCounter = 0;
    Set<Socket> sockets;
    ObjectOutputStream out;
    static MongoDatabase database;
    static Type type;
    MongoClient mongoClient;

    public static void main(String[] args) {
        books = new HashMap<>();
        loginInfo = new HashMap<>();
        new Server().runServer();
        type = new TypeToken<HashMap<String, LoginInfo>>(){}.getType();
    }

    private void runServer() {
        try {
            sockets = new HashSet<>();
            String connectionString = "mongodb+srv://bhuiyansajid8:AcNBnJPLeQ6Cg8mm@finalproject.hd3t08o.mongodb.net/?retryWrites=true&w=majority";
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .retryWrites(true)
                    .serverApi(serverApi)
                    .build();
            // Create a new client and connect to the server
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("Library");
            MongoCollection<Document> collection = database.getCollection("Entries");
            MongoCursor<Document> cursor = collection.find().iterator();
            try {
                while (cursor.hasNext()) {
                    //convert each document to json string
                    String json = cursor.next().toJson();
                    //convert json string to Entry object
                    Entry entry = new Gson().fromJson(json, Entry.class);
                    //add the entry to the hashmap
                    books.put(entry.getTitle(), entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            MongoCollection<Document> collection1 = database.getCollection("LoginInfo");
            MongoCursor<Document> cursor1 = collection1.find().iterator();
            try {
                while (cursor1.hasNext()) {
                    //convert each document to json string
                    String json = cursor1.next().toJson();
                    //convert json string to LoginInfo object
                    LoginInfo info = new Gson().fromJson(json, LoginInfo.class);
                    //add the entry to the hashmap
                    loginInfo.put(info.getUserName(), info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (LoginInfo info : loginInfo.values()) {
                for (LoginInfo.IssuedItem item : info.getIssuedItems()) {
                    LocalDate dueDate = LocalDate.parse(item.getDueDate());
                    LocalDate today = LocalDate.now();
                    //for everyday late, add 5 to the fee
                    if (today.isAfter(dueDate)) {
                        item.setLate("Yes");
                        //for everyday after due date, add 5 to the fee
                        //Fee is in the form $5.00
                        double fee = Double.parseDouble(item.getFee().substring(1));
                        fee += 0.5 * (today.getDayOfYear() - dueDate.getDayOfYear());
                        item.setFee("$" + fee + ".00");
                    }
                }
            }
            MongoCollection<Document> collection2 = database.getCollection("LoginInfo");
            for (LoginInfo info : loginInfo.values()) {
                ArrayList<Document> items = new ArrayList<>();
                for (LoginInfo.IssuedItem item : info.getIssuedItems()) {
                    items.add(new Document("item", item.getItem()).append("issuedDate", item.getIssuedDate()).append("dueDate", item.getDueDate()).append("Late", item.getLate()).append("Fee", item.getFee()));
                }
                collection2.updateOne(Filters.eq("UserName", info.getUserName()), new Document("$set", new Document("issuedItems", items)));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try{
            setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        private void setUpNetworking () throws Exception {
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
                out.writeObject((books));
                Thread t = new Thread(handler);
                t.start();
            }
        }

    public void processRegistration(String username, String password, ClientHandler clientHandler) {
        //make a new loginInfo object
        LoginInfo info = new LoginInfo(username, password, new ArrayList<>());
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
            if (books.containsKey(itemInfo[0])) {
                //check if the book is available
                if (books.get(itemInfo[0]).getAvailable().equals("Yes")) {
                    //set the book to unavailable
                    if (books.get(itemInfo[0]).getCount() - 1 == 0) {
                        books.get(itemInfo[0]).setAvailable("No");
                    }
                    books.get(itemInfo[0]).setCount(books.get(itemInfo[0]).getCount() - 1);
                    loginInfo.get(username).getIssuedItems().add(new LoginInfo.IssuedItem(itemInfo[0], itemInfo[1], itemInfo[2]));
                } else {
                    //send the book is not available message to the client
                    clientHandler.sendToClient("BOOK NOT AVAILABLE: " + itemInfo[0]);
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

    public void processReturn(String username, String issuedItem, ClientHandler clientHandler ){
        String[] items = issuedItem.split(";");
        //loop through the items
        for (String item : items) {
            //split the item into name, issued date, and due date
            String[] itemInfo = item.split(",");
            //check if the book is in the database
            if (books.containsKey(itemInfo[0])) {
                //set the book to available
                books.get(itemInfo[0]).setAvailable("Yes");
                books.get(itemInfo[0]).setCount(books.get(itemInfo[0]).getCount() + 1);
                //remove the book from the issued items
//                for (LoginInfo.IssuedItem issued : loginInfo.get(username).getIssuedItems()) {
//                    if (issued.getItem().equals(itemInfo[0])) {
//                        loginInfo.get(username).getIssuedItems().remove(issued);
//                    }
//                }
                loginInfo.get(username).getIssuedItems().removeIf(issued -> issued.getItem().equals(itemInfo[0]));
            }
        }
        //send the updated books to the client
        //get the loginInfo from the username passed in
        Gson gson = new Gson();
        String json = gson.toJson(loginInfo.get(username));
        clientHandler.sendToClient("RETURNEDUSERNAME+" + json);
        json = gson.toJson(books);
        clientHandler.sendToClient("RETURNED+" + json);
        //have all the clients update their books using Observable
        setChanged();
        notifyObservers(books);

    }

}