package org.example.javalottery;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import sockets.client.SocketTCPClient;

public class LotteryController {

    @FXML private TextField numberField;
    @FXML private Button checkButton;
    @FXML private Label statusLabel;
    @FXML private Label resultLabel;

    private SocketTCPClient client;

    /** Inyección del cliente desde la Application. */
    public void setClient(SocketTCPClient client) {
        this.client = client;
    }

    /** Lo llamaremos desde HelloApplication cuando la escena ya está cargada. */
    public void connectClientAsync() {
        checkButton.setDisable(true);
        statusLabel.setText("Conectando con el servidor...");

        Task<Void> connectTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                client.start(); // bloqueante, por eso va en Task
                return null;
            }
        };

        connectTask.setOnSucceeded(_ -> {
            statusLabel.setText("Conectado. Introduce un número y pulsa Comprobar.");
            resultLabel.setText("-");
            checkButton.setDisable(false);
        });

        connectTask.setOnFailed(_ -> {
            statusLabel.setText("No se pudo conectar: " + connectTask.getException().getMessage());
            resultLabel.setText("-");
            checkButton.setDisable(true);
        });

        new Thread(connectTask, "connect-task").start();
    }

    @FXML
    private void onCheckClick() {
        if (client == null) {
            statusLabel.setText("Cliente no inicializado.");
            return;
        }

        String num = numberField.getText();

        // UX: validación rápida en UI
        if (num == null || !num.matches("\\d{5}")) {
            resultLabel.setText("Número inválido. Debe tener 5 dígitos.");
            return;
        }

        checkButton.setDisable(true);
        statusLabel.setText("Consultando...");

        Task<String> checkTask = getStringTask(num);

        new Thread(checkTask, "check-task").start();
    }

    @NotNull
    private Task<String> getStringTask(String num) {
        Task<String> checkTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return client.checkNumber(num); // bloqueante → Task
            }
        };

        checkTask.setOnSucceeded(_ -> {
            String resp = checkTask.getValue();
            statusLabel.setText("Respuesta recibida.");
            resultLabel.setText(resp);
            checkButton.setDisable(false);
        });

        checkTask.setOnFailed(_ -> {
            statusLabel.setText("Error consultando: " + checkTask.getException().getMessage());
            resultLabel.setText("-");
            checkButton.setDisable(false);
        });
        return checkTask;
    }
}