package sockets.server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketTCPServer {
    private final ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /*
    El número premiado se carga desde el fichero en memoria
    cada vez que se ejecute la aplicación, es util
    porque mejora la eficiencia, ya que solo se va a evaluar un número
     */
    private final String winningNumber;

    private static final int PORT = 2021;
    private static final String SERVER_IP = "localhost";

    public SocketTCPServer() throws IOException {
        // Cargar número premiado
        this.winningNumber = loadWinningNumberFromResources();

        // Preparar servidor
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(SERVER_IP, PORT));
    }

    private String loadWinningNumberFromResources() throws IOException {
        InputStream is = getClass().getResourceAsStream("/files/num_premiado.txt");
        if (is == null) {
            throw new FileNotFoundException("No se encontró el recurso: " + "/files/num_premiado.txt");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                throw new IOException("El fichero de premio está vacío.");
            }
            return line.trim(); // un solo número se eliminan espacios
        }
    }

    public void start() throws IOException {
        System.out.println("(Servidor) Esperando conexión...");
        clientSocket = serverSocket.accept(); // 1 conexión

        // Enviar y recibir datos desde el cliente

        out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
        in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

        System.out.println("(Servidor) Conexión establecida. Premio cargado: " + winningNumber);
    }

    // bucle: misma conexión, múltiples consultas
    public void runSingleClientLoop() throws IOException {
        start();

        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                msg = msg.trim();

                if (msg.equalsIgnoreCase("QUIT")) {
                    out.println("BYE");
                    break;
                }

                if (!msg.matches("\\d{5}")) {
                    out.println("ERROR Número inválido. Debe tener 5 dígitos.");
                    continue;
                }

                if (msg.equals(winningNumber)) {
                    out.println("WIN ¡Felicidades! El número " + msg + " ha sido premiado");
                } else {
                    out.println("LOSE Lo sentimos, el número " + msg + " no ha sido premiado");
                }
            }
        } finally {
            stop();
        }
    }

    public void stop() throws IOException {
        System.out.println("(Servidor) Cerrando...");
        if (in != null) in.close();
        if (out != null) out.close();
        if (clientSocket != null) clientSocket.close();
        if (!serverSocket.isClosed()) serverSocket.close();
        System.out.println("(Servidor) Cerrado");
    }

    public static void main(String[] args) {
        try {
            new SocketTCPServer().runSingleClientLoop();
        } catch (IOException e) {
            System.out.println("Error servidor: " + e.getMessage());
        }
    }
}