
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;
import java.util.Observable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class ClientHandler implements Runnable, Observer {

  private Server server;
  private Socket clientSocket;
  private BufferedReader fromClient;
  private PrintWriter toClient;
  static Type type;

  protected ClientHandler(Server server, Socket clientSocket) {
    System.out.println("connected");
    this.server = server;
    this.clientSocket = clientSocket;
    try {
      fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
      toClient = new PrintWriter(this.clientSocket.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void sendToClient(String string) {
    System.out.println("Sending to client: " + string);
    toClient.println(string);
    toClient.flush();
  }

  @Override
  public void run() {
    String input;
    try {
      while (!clientSocket.isClosed() && (input = fromClient.readLine()) != null) {
        System.out.println("From client: " + input);
        if (input.startsWith("REGISTER:")) {
          String[] tokens = input.split(":");
          if (tokens.length == 3) {
            String username = tokens[1];
            String password = tokens[2];
            // Send the username and password to the server for processing
            server.processRegistration(username, password, this);
          }
        }
        if(input.startsWith("LOGIN:")){
          String[] tokens = input.split(":");
          if (tokens.length == 3) {
            String username = tokens[1];
            String password = tokens[2];
            // Send the username and password to the server for processing
            server.processLogin(username, password, this);
          }
        }
        if(input.startsWith("LOGOUT")){
          server.processClientLogOff(this);
        }
        if(input.startsWith("CHECKOUT")){
          //CHECKOUT:user1:1984,2023-04-14,2023-04-28;The Catcher in the Rye,2023-04-14,2023-04-28; this is the format of the string
          //split the string by the semicolon
            String[] tokens = input.split(":");
            //get the username
            String username = tokens[1];
            //get the books
            String books = tokens[2];
            //split the books by the comma
            //change each book into a IssuedItem object
          ArrayList<LoginInfo.IssuedItem> issuedItems = new ArrayList<>();
            for(String book : books.split(";")){
              //split each book by the comma
              String[] bookInfo = book.split(",");
                //create a new IssuedItem object
              LoginInfo.IssuedItem item = new LoginInfo.IssuedItem(bookInfo[0], bookInfo[1], bookInfo[2]);
              issuedItems.add(item);
            }
            StringBuilder sb = new StringBuilder();
            for(LoginInfo.IssuedItem item : issuedItems){
              sb.append(item.toString() + ";");
            }
            //send the username and the IssuedItem object to the server
            server.processCheckout(username, String.valueOf(sb), this);
        }
        if(input.startsWith("RETURN")){
          //RETURN:user1:1984,2023-04-14,2023-04-28;The Catcher in the Rye,2023-04-14,2023-04-28; this is the format of the string
          //split the string by the semicolon
          String[] tokens = input.split(":");
          //get the username
          String username = tokens[1];
          //get the books
          String books = tokens[2];
          //split the books by the comma
          //change each book into a IssuedItem object
          ArrayList<LoginInfo.IssuedItem> issuedItems = new ArrayList<>();
          for(String book : books.split(";")){
            //split each book by the comma
            String[] bookInfo = book.split(",");
            //create a new IssuedItem object
            LoginInfo.IssuedItem item = new LoginInfo.IssuedItem(bookInfo[0], bookInfo[1], bookInfo[2]);
            issuedItems.add(item);
          }
          StringBuilder sb = new StringBuilder();
          for(LoginInfo.IssuedItem item : issuedItems){
            sb.append(item.toString() + ";");
          }
          //send the username and the IssuedItem object to the server
          server.processReturn(username, String.valueOf(sb), this);
        }
        else if(input.startsWith("ADMINLOGIN")){
          String[] tokens = input.split(":");
          String username = tokens[1];
          String password = tokens[2];
          String ID = tokens[3];
          server.processAdminLogin(username, password, ID, this);
        }
        else if(input.startsWith("ADDNEWENTRY")){
          //input will come in the form of ADDNEWENTRY:Title:Author:Genre:Type:Count
            String[] tokens = input.split(":");
            String title = tokens[1];
            String author = tokens[2];
            String genre = tokens[3];
            String type = tokens[4];
            int count = Integer.parseInt(tokens[5]);
            server.processAddNewEntry(title, author, genre, type, count, this);
        }
        else if(input.startsWith("ADDCURRENTENTRY")){
          //input will come in the form of ADDCURRENTENTRY:Title:Count
            String[] tokens = input.split(":");
            String title = tokens[1];
            int count = Integer.parseInt(tokens[2]);
            server.processAddCurrentEntry(title, count, this);
        }
        else if(input.startsWith("REMOVE")){
          //input will come in the form of REMOVE:Title
            String[] tokens = input.split(":");
            String title = tokens[1];
            server.processRemove(title, this);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Socket getClientSocket() {
    return clientSocket;
  }

  @Override
  public void update(Observable o, Object arg) {
//    Gson gson = new Gson();
//    String json = gson.toJson(arg);
//    //check if arg is an instance of HashMap<String, Entry>
//
//    sendToClient("UPDATELIBRARY+" + json);
//    sendToClient("UPDATEDLOGININFO+" + json);
    //check if arg is an instance of HashMap<String, LoginInfo>
    if(arg instanceof HashMap){
      try {
        HashMap<String, LoginInfo> loginInfo = (HashMap<String, LoginInfo>) arg;
        Gson gson = new Gson();
        String json = gson.toJson(loginInfo);
        sendToClient("UPDATEDLOGININFO+" + json);
      } catch (Exception ignored) {
      }
      try{
        HashMap<String, Entry> library = (HashMap<String, Entry>) arg;
        Gson gson = new Gson();
        String json = gson.toJson(library);
        sendToClient("UPDATEDLIBRARY+" + json);
      } catch (Exception ignored) {
      }
    }
  }
}