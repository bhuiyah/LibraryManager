import java.io.*;
import java.util.Scanner;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Client {

    private static String host = "127.0.0.1";
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private Scanner consoleInput = new Scanner(System.in);

    Entry[] books;
    public static void main(String[] args) {
        try {
            new Client().setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        Socket socket = new Socket(host, 4242);
        System.out.println("Connecting to... " + socket);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer = new PrintWriter(socket.getOutputStream());
        while(true) {
            try {
                books = (Entry[]) new ObjectInputStream(socket.getInputStream()).readObject();
                break;
            } catch (EOFException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Books received");
        for (Entry book : books) {
            System.out.println(book);
        }

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String input;
                try {
                    while ((input = fromServer.readLine()) != null) {
                        System.out.println("From server: " + input);
                        processRequest(input);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
//                    String input = consoleInput.nextLine();
//                    String[] variables = input.split(",");
//                    Message request = new Message(variables[0], variables[1], Integer.valueOf(variables[2]));
//                    GsonBuilder builder = new GsonBuilder();
//                    Gson gson = builder.create();
//                    sendToServer(gson.toJson(request));
                }
            }
        });
        readerThread.start();
        writerThread.start();
    }

    protected void processRequest(String input) {
        return;
    }

    protected void sendToServer(String string) {
        System.out.println("Sending to server: " + string);
        toServer.println(string);
        toServer.flush();
    }

}