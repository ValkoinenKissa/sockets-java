package sockets.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketTCPClient {
    private final String serverIP;
    private final int serverPort;

    // Atributos de conexión e intercambio de información con el server
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Constructor
    public SocketTCPClient(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void start() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(serverIP, serverPort));

        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    // Metodo para comprobar el número (lado del cliente)
    public String checkNumber(String lotteryNumber) throws IOException {
        if (lotteryNumber == null || !lotteryNumber.matches("\\d{5}")) {
            return "ERROR_CLIENT: número inválido (5 dígitos)";
        }
        out.println(lotteryNumber);
        return in.readLine();
    }

    // útil para QUIT o otros comandos internos
    public String sendRaw(String line) throws IOException {
        out.println(line);
        return in.readLine();
    }

    public void stop() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }
}