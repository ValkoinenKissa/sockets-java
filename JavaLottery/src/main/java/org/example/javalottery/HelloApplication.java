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
        // Arrancar servidor en segundo plano (accept() bloquea)
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

        // Preparar cliente
        client = new SocketTCPClient("localhost", 2021);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("lottery-view.fxml"));
        Scene scene = new Scene(loader.load());

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
        // Cierre
        try {
            if (client != null) {
                client.stop();
            }
        } catch (Exception ignored) {}
    }
}