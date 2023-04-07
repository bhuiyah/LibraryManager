import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
//import gson
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Server extends Observable {
    Entry[] books;
    public static void main(String[] args) {
        new Server().runServer();
    }

    private void runServer() {
        try {
            Gson gson = new Gson();
            books = gson.fromJson(new FileReader("src/Entries.json"), Entry[].class);
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
        String output = "Error";
        Gson gson = new Gson();
//        Message message = gson.fromJson(input, Message.class);
//        try {
//            String temp = "";
//            switch (message.type) {
//                case "upper":
//                    temp = message.input.toUpperCase();
//                    break;
//                case "lower":
//                    temp = message.input.toLowerCase();
//                    break;
//                case "strip":
//                    temp = message.input.replace(" ", "");
//                    break;
//            }
//            output = "";
//            for (int i = 0; i < message.number; i++) {
//                output += temp;
//                output += " ";
//            }
//            this.setChanged();
//            this.notifyObservers(output);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}