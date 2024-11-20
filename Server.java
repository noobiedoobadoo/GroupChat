import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.InputStreamReader;

public class Server implements Runnable{

    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;
    Object lock;

    public Server(){
        connections = new ArrayList<>();
        done = false;
        lock = new Object();
    }

    @Override
    public void run(){
        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while (!done){
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }
        } catch (Exception e) {
            shutdown();
        }
    }

    public void broadcast(String message){
        for (ConnectionHandler ch : connections){
            if (ch != null)
                ch.sendMessage(message);
        }
    }

    public void shutdown(){
        try {
            done = true;
            pool.shutdown();
            if (!server.isClosed()){
                server.close();
            }
            for (ConnectionHandler ch : connections){
                ch.shutdown();
            }
        } catch (IOException e){
            //ignore
        }

    }

    class ConnectionHandler implements Runnable{

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket client){
            this.client = client;
        }

        @Override
        public void run(){
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                boolean duplicate = false;
                String tempName;
                do {
                    out.println("Please enter an alias: ");
                    tempName = in.readLine();
//                    nickname = in.readLine();
                    synchronized (lock){
                        duplicate = false;
                        for (ConnectionHandler c : connections){
//                            if (c == this)  continue;
                            if (c.nickname != null && c.nickname.equals(tempName)){
                                duplicate = true;
                                out.println();
                                out.println("this alias is already in use.");
                                break;
                            }
                        }
                        if (!duplicate){
                            this.nickname = tempName;
                        }
                    }
                }while(duplicate);
//                this.nickname = tempName;


                if (duplicate){
                    out.println("this alias is already in use.");
                }
                else{
                    System.out.println(nickname + " connected!");
                    broadcast(nickname + " joined the chat!");
                    String message;
                    while ((message = in.readLine()) != null){
                        if (false){
                            //ignore
                        } else if (message.startsWith("/quit")){
                            broadcast(nickname + " left the chat!");
                            shutdown();
                        } else {
                            broadcast(nickname + ": " + message);
                        }
                    }
                }


            } catch (IOException e) {
                shutdown();
            }
        }

        public void sendMessage(String message){
            out.println(message);
        }

        public void shutdown(){
            try {
                in.close();
                out.close();
                if (!client.isClosed()){
                    client.close();
                }
            } catch (IOException e){
                //ignore
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
