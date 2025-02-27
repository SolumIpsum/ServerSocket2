import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Server {
    private static Map<String, ClientHandler> activeClients = new HashMap<>();

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(6666)) {
            System.out.println("Ожидание подключения...");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Подключён: " + clientSocket.getInetAddress());

                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    System.out.println("Ошибка при подключении клиента: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public static void registerClient(String username, ClientHandler clientHandler) {
        activeClients.put(username, clientHandler);
    }

    public static void unregisterClient(String username) {
        activeClients.remove(username);
    }

    public static void sendMessageToUser(String sender, String recipient, String message) {
        ClientHandler recipientHandler = activeClients.get(recipient);
        if (recipientHandler != null) {
            recipientHandler.receiveMessage(sender + ": " + message);
        }
    }

    public static void listActiveUsers(ClientHandler clientHandler) {
        StringBuilder userList = new StringBuilder("Active users: ");
        for (String username : activeClients.keySet()) {
            userList.append(username).append(" ");
        }
        clientHandler.receiveMessage(userList.toString());
    }

    public static boolean userExists(String name) {
        return activeClients.containsKey(name);
    }
}
