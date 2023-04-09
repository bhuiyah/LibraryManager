
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Observer;
import java.util.Observable;

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
    this.sendToClient((String) arg);
  }
}