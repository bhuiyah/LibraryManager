
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Observable;
import com.google.gson.Gson;

class ClientHandler implements Runnable, Observer {

  private Server server;
  private Socket clientSocket;
  private BufferedReader fromClient;
  private PrintWriter toClient;

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
    //arg is going to be Entry[] array, convert to gson string and send to client
    Gson gson = new Gson();
    String json = gson.toJson(arg);
    sendToClient("UPDATELIBRARY+" + json);

  }
}