package org.example.javalottery;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sockets.client.SocketTCPClient;
import sockets.server.SocketTCPServer;

public class HelloApplication extends Application {

    private SocketTCPClient client;

    @Override
    public void init() {
        // 1) Arrancar servidor en segundo plano (accept() bloquea)
        // tu servidor con bucle 1 conexión
        Thread serverThread = new Thread(() -> {
            try {
                SocketTCPServer server = new SocketTCPServer();
                server.runSingleClientLoop(); // tu servidor con bucle 1 conexión
            } catch (Exception e) {
                System.out.println("Servidor error: " + e.getMessage());
            }
        }, "server-thread");

        serverThread.setDaemon(true);
        serverThread.start();

        // 2) Preparar cliente (conectamos después)
        client = new SocketTCPClient("localhost", 2021);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("lottery-view.fxml"));
        Scene scene = new Scene(loader.load(), 520, 260);

        // Inyectar cliente al controller
        LotteryController controller = loader.getController();
        controller.setClient(client);
        controller.connectClientAsync();

        stage.setTitle("Java Lottery");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Cierre limpio
        try {
            if (client != null) {
                // Opcional: si implementaste sendRaw("QUIT") en el cliente:
                // client.sendRaw("QUIT");
                client.stop();
            }
        } catch (Exception ignored) {}
    }
}