import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.time.LocalDate;
//import gson
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.*;

class Server extends Observable {
    static HashMap<String, Entry> books;
    static HashMap<String, LoginInfo> loginInfo;
    static HashMap<String, Admin> admins;
    int clientCounter = 0;
    Set<Socket> sockets;
    static Set<ClientHandler> observers;
    ObjectOutputStream out;
    static MongoDatabase database;
    static Type type;
    MongoClient mongoClient;
    MongoCollection<Document> entryCollection;
    MongoCollection<Document> loginInfoCollection;
    MongoCollection<Document> adminCollection;

    public static void main(String[] args) {
        books = new HashMap<>();
        loginInfo = new HashMap<>();
        admins = new HashMap<>();
        observers = new HashSet<>();
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
            entryCollection = database.getCollection("Entries");
            MongoCursor<Document> cursor = entryCollection.find().iterator();
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
            loginInfoCollection = database.getCollection("LoginInfo");
            MongoCursor<Document> cursor1 = loginInfoCollection.find().iterator();
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
            adminCollection = database.getCollection("Admins");
            MongoCursor<Document> cursor2 = adminCollection.find().iterator();
            try {
                while (cursor2.hasNext()) {
                    //convert each document to json string
                    String json = cursor2.next().toJson();
                    //convert json string to Admin object
                    Admin admin = new Gson().fromJson(json, Admin.class);
                    //add the entry to the hashmap
                    admins.put(admin.getUsername(), admin);
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
                        double fee = Double.parseDouble(item.getFee().substring(1));
                        fee += (5 * (today.getDayOfYear() - dueDate.getDayOfYear())) - fee;
                        item.setFee("$" + fee);
                    }
                }
            }
            for (LoginInfo info : loginInfo.values()) {
                ArrayList<Document> items = new ArrayList<>();
                for (LoginInfo.IssuedItem item : info.getIssuedItems()) {
                    items.add(new Document("item", item.getItem()).append("issuedDate", item.getIssuedDate()).append("dueDate", item.getDueDate()).append("Late", item.getLate()).append("Fee", item.getFee()));
                }
                loginInfoCollection.updateOne(Filters.eq("UserName", info.getUserName()), new Document("$set", new Document("issuedItems", items)));
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
                observers.add(handler);
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
        //check if the password is good enough
        if (password.length() < 8) {
            clientHandler.sendToClient("PASSWORD TOO SHORT+");
            return;
        }
        //check if the password has a number
        boolean hasNumber = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                hasNumber = true;
                break;
            }
        }
        if (!hasNumber) {
            clientHandler.sendToClient("PASSWORD MUST HAVE A NUMBER+");
            return;
        }
        //check if the password has a special character
        boolean hasSpecial = false;
        for(int i = 0; i < password.length(); i++) {
            if(!Character.isLetterOrDigit(password.charAt(i))) {
                hasSpecial = true;
                break;
            }
        }
        if(!hasSpecial) {
            clientHandler.sendToClient("PASSWORD MUST HAVE A SPECIAL CHARACTER+");
            return;
        }
        //check if the password has an uppercase letter
        boolean hasUpper = false;
        for(int i = 0; i < password.length(); i++) {
            if(Character.isUpperCase(password.charAt(i))) {
                hasUpper = true;
                break;
            }
        }
        if(!hasUpper) {
            clientHandler.sendToClient("PASSWORD MUST HAVE AN UPPERCASE LETTER+");
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(info);
        //add to loginInfo array
        loginInfo.put(username, info);
        clientHandler.sendToClient("REGISTRATION INFORMATION+" + json);
        clientHandler.sendToClient("REGISTERED: " + username);
        //add to database
        Document doc = Document.parse(json);
        loginInfoCollection.insertOne(doc);
    }

    public void processLogin(String username, String password, ClientHandler clientHandler) {
        LoginInfo info = new LoginInfo(username, password, new ArrayList<>());
        //check if info is in database
        //look through the hashmap to see if the username is already in use
        //check the observers to see if the user is already logged in
        for (ClientHandler handler : observers) {
            if (handler.getUsername().equals(username)) {
                clientHandler.sendToClient("ALREADY LOGGED IN+ " + username);
                return;
            }
        }
        if (loginInfo.containsKey(username)) {
            //check if the password is correct

            if (loginInfo.get(username).getPassword().equals(password)) {
                //send the login info to the client
                Gson gson = new Gson();
                String json = gson.toJson(loginInfo.get(username));
                //add a code along with the json
                clientHandler.sendToClient("LOGIN INFORMATION+" + json);
                clientHandler.sendToClient("LOGGED IN: " + username);
                clientHandler.setUsername(username);
                return;
            }
        }
        clientHandler.sendToClient("INVALID LOGIN: " + username);
    }

    public void processClientLogOff(ClientHandler clientHandler) throws IOException {
        clientCounter--;
        sockets.remove(clientHandler.getClientSocket());
        clientHandler.sendToClient("LOGOUT");
        observers.remove(clientHandler);
        //remove the client from the observers
        this.deleteObserver(clientHandler);
        clientHandler.getClientSocket().close();
    }

    public void processCheckout(String username, String issuedItem, ClientHandler clientHandler) throws IOException {
        //check if book is in database
        //split issueditem into name, issued date, and due date
        //issuedItem is coming like this: The Grapes of Wrath,2023-04-15,2023-04-29;Pride and Prejudice,2023-04-15,2023-04-29;The Catcher in the Rye,2023-04-15,2023-04-29;
        //issuedItem is a json string of an array of issued items
        Gson gson = new Gson();
        LoginInfo.IssuedItem[] items = gson.fromJson(issuedItem, LoginInfo.IssuedItem[].class);
        for (LoginInfo.IssuedItem item : items) {
            //check if the book is in the database
            if (books.containsKey(item.getItem())) {
                //check if the book is available
                if (books.get(item.getItem()).getAvailable().equals("Yes")) {
                    //set the book to unavailable
                    if (books.get(item.getItem()).getCount() - 1 == 0) {
                        books.get(item.getItem()).setAvailable("No");
                    }
                    books.get(item.getItem()).setCount(books.get(item.getItem()).getCount() - 1);
                    loginInfo.get(username).getIssuedItems().add(new LoginInfo.IssuedItem(item.getItem(), item.getIssuedDate(), item.getDueDate()));
                    //update loginInfo in database
                    ArrayList<Document> items1 = new ArrayList<>();
                    for (LoginInfo.IssuedItem item1 : loginInfo.get(username).getIssuedItems()) {
                        items1.add(new Document("item", item1.getItem()).append("issuedDate", item1.getIssuedDate()).append("dueDate", item1.getDueDate()).append("Late", item1.getLate()).append("Fee", item1.getFee()));
                    }
                    loginInfoCollection.updateOne(Filters.eq("UserName", username), new Document("$set", new Document("issuedItems", items1)));
                    Document doc = new Document("title", books.get(item.getItem()).getTitle()).append("genre", books.get(item.getItem()).getGenre()).append("author", books.get(item.getItem()).getAuthor()).append("available", books.get(item.getItem()).getAvailable()).append("media_type", books.get(item.getItem()).getMedia_type()).append("count", books.get(item.getItem()).getCount());
                    entryCollection.updateOne(Filters.eq("title", books.get(item.getItem()).getTitle()), new Document("$set", doc));
                } else {
                    //send the book is not available message to the client
                    clientHandler.sendToClient("BOOK NOT AVAILABLE: " + item.getItem());
                }
            }
        }
        //send the updated books to the client
        //get the loginInfo from the username passed in
        gson = new Gson();
        String json = gson.toJson(loginInfo.get(username));
        clientHandler.sendToClient("CHECKEDOUTUSERNAME+" + json);
        json = gson.toJson(books);
        clientHandler.sendToClient("CHECKEDOUT+" + json);
        //have all the clients update their books using Observable
        setChanged();
        notifyObservers("books+" + json);

        //update the books in the database
    }

    public void processReturn(String username, String issuedItem, ClientHandler clientHandler ){
        //issuedItem is a json string of an array of issued items
        Gson gson = new Gson();
        LoginInfo.IssuedItem[] items = gson.fromJson(issuedItem, LoginInfo.IssuedItem[].class);
        //loop through the items
        for (LoginInfo.IssuedItem item : items) {
            //split the item into name, issued date, and due date
            //check if the book is in the database
            if (books.containsKey(item.getItem())) {
                //set the book to available
                books.get(item.getItem()).setAvailable("Yes");
                books.get(item.getItem()).setCount(books.get(item.getItem()).getCount() + 1);
                //remove the book from the issued items
                loginInfo.get(username).getIssuedItems().removeIf(issued -> issued.getItem().equals(item.getItem()));
                //update loginInfo in database
                ArrayList<Document> items1 = new ArrayList<>();
                for (LoginInfo.IssuedItem item1 : loginInfo.get(username).getIssuedItems()) {
                    items1.add(new Document("item", item1.getItem()).append("issuedDate", item1.getIssuedDate()).append("dueDate", item1.getDueDate()).append("Late", item1.getLate()).append("Fee", item1.getFee()));
                }
                loginInfoCollection.updateOne(Filters.eq("UserName", username), new Document("$set", new Document("issuedItems", items1)));
                Document doc = new Document("title", books.get(item.getItem()).getTitle()).append("genre", books.get(item.getItem()).getGenre()).append("author", books.get(item.getItem()).getAuthor()).append("available", books.get(item.getItem()).getAvailable()).append("media_type", books.get(item.getItem()).getMedia_type()).append("count", books.get(item.getItem()).getCount());
                entryCollection.updateOne(Filters.eq("title", books.get(item.getItem()).getTitle()), new Document("$set", doc));
            }
        }
        //send the updated books to the client
        //get the loginInfo from the username passed in
        gson = new Gson();
        String json = gson.toJson(loginInfo.get(username));
        clientHandler.sendToClient("RETURNEDUSERNAME+" + json);
        json = gson.toJson(books);
        clientHandler.sendToClient("RETURNED+" + json);
        //have all the clients update their books using Observable
        setChanged();
        notifyObservers("books+" + json);
        notifyObservers("loginInfo+" + json);
    }

    public void processAdminLogin(String username, String password, String ID, ClientHandler handler){
        //check if the username and password are in the database
        if (admins.containsKey(username)) {
            if (admins.get(username).getPassword().equals(password) && admins.get(username).getID().equals(ID)){
                Gson gson = new Gson();
                String json = gson.toJson(admins.get(username));
                handler.sendToClient("ADMININFO+" + json);
                json = gson.toJson(loginInfo);
                handler.sendToClient("ADMINUSERMANAGEMENT+" + json);
                handler.sendToClient("ADMINLOGGEDIN+" + username);
            }
        }
        else{
            handler.sendToClient("INVALIDADMINLOGIN+" + username);
        }
    }

    public void processAddNewEntry(String title, String author, String genre, String type, int count, String description, String URL, ClientHandler clientHandler) {
        //check if the book is already in the database
        if (books.containsKey(title)) {
            clientHandler.sendToClient("BOOKALREADYEXISTS+" + title);
            //still add the count to the current number of count
            books.get(title).setCount(books.get(title).getCount() + count);
            Document doc = new Document("title", title).append("genre", genre).append("author", author).append("available", "Yes").append("media_type", type).append("count", books.get(title).getCount()).append("description", description).append("url", URL);
            entryCollection.updateOne(Filters.eq("title", title), new Document("$set", doc));
            //send the updated books to the client
            Gson gson = new Gson();
            String json = gson.toJson(books);
            //have all the clients update their books using Observable
            setChanged();
            notifyObservers("books+" + json);
        } else {
            //add the book to the database
            books.put(title, new Entry(title, author, genre,"Yes",  type, count, description, URL));
            Document doc = new Document("title", title).append("genre", genre).append("author", author).append("available", "Yes").append("media_type", type).append("count", count).append("description", description).append("url", URL);
            entryCollection.insertOne(doc);
            //send the updated books to the client
            Gson gson = new Gson();
            String json = gson.toJson(books);
            //have all the clients update their books using Observable
            setChanged();
            notifyObservers("books+" + json);
            clientHandler.sendToClient("NEWBOOKADDED+" + title);
        }
    }

    public void processAddCurrentEntry(String title, int count, ClientHandler clientHandler) {
        //check if the book is already in the database
        if (books.containsKey(title)) {
            //add the book to the database
            books.get(title).setCount(books.get(title).getCount() + count);
            //set the book to available
            books.get(title).setAvailable("Yes");
            //update this particular book in the database
            //book has a title, genre, author, available, media_type, count, url, and description
            Document doc = new Document("title", title).append("genre", books.get(title).getGenre()).append("author", books.get(title).getAuthor()).append("available", books.get(title).getAvailable()).append("media_type", books.get(title).getMedia_type()).append("count", books.get(title).getCount()).append("url", books.get(title).getUrl()).append("description", books.get(title).getDescription());
            entryCollection.updateOne(Filters.eq("title", books.get(title).getTitle()), new Document("$set", doc));
            //send the updated books to the client
            Gson gson = new Gson();
            String json = gson.toJson(books);
            //have all the clients update their books using Observable
            setChanged();
            notifyObservers("books+" + json);
            clientHandler.sendToClient("CURRENTBOOKADDED+" + title);
        } else {
            clientHandler.sendToClient("BOOKDOESNOTEXIST+" + title);
        }
    }

    public void processRemove(String title, ClientHandler clientHandler) {
        //check if the book is already in the database
        if (books.containsKey(title)) {
            //remove the book from the database
            books.remove(title);
            entryCollection.deleteOne(Filters.eq("title", title));
            //send the updated books to the client
            Gson gson = new Gson();
            String json = gson.toJson(books);
            //have all the clients update their books using Observable
            setChanged();
            notifyObservers("books+" + json);
            //check if any users have the book issued
            //remove the item from the issued items of the user
            for (String username : loginInfo.keySet()) {
                loginInfo.get(username).getIssuedItems().removeIf(issued -> issued.getItem().equals(title));
                //update loginInfo in database
                ArrayList<Document> items1 = new ArrayList<>();
                for (LoginInfo.IssuedItem item1 : loginInfo.get(username).getIssuedItems()) {
                    items1.add(new Document("item", item1.getItem()).append("issuedDate", item1.getIssuedDate()).append("dueDate", item1.getDueDate()).append("Late", item1.getLate()).append("Fee", item1.getFee()));
                }
                loginInfoCollection.updateOne(Filters.eq("UserName", username), new Document("$set", new Document("issuedItems", items1)));
            }
            //convert loginInfo to json
            gson = new Gson();
            json = gson.toJson(loginInfo);
            setChanged();
            notifyObservers("loginInfo+" + json);
            clientHandler.sendToClient("BOOKREMOVED+" + title);
        } else {
            clientHandler.sendToClient("BOOKDOESNOTEXIST+" + title);
        }
    }
}