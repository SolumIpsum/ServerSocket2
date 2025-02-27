import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String username;
    private String recipient;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Login:");
            username = in.readLine();
            recipient = "";

            Server.registerClient(username, this);
            out.println("ZDRAVSTVUJTIE, " + username + "!");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("/who")) {
                    Server.listActiveUsers(this);
                }
                else if (inputLine.startsWith("/go ")) {
                    if (inputLine.length() <= 4) {
                        out.println("You must enter name");
                        continue;
                    }
                    
                    String recipientName = inputLine.substring(4);
                    if (Server.userExists(recipientName)) {
                        out.println("Now you're talking to: " + recipientName);
                        this.recipient = recipientName;
                    }
                    else {
                        out.println("Takogo usera niet");
                    }
                }
                else if (recipient != "") {
                    Server.sendMessageToUser(username, recipient, inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при обработке клиента: " + e.getMessage());
        } finally {
            try {
                Server.unregisterClient(username);
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Ошибка при закрытии сокета: " + e.getMessage());
            }
        }
    }

    public void receiveMessage(String message) {
        out.println(message);
    }
}
