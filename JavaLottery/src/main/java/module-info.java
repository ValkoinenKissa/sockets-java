module org.example.javalottery {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires annotations;

    opens org.example.javalottery to javafx.fxml;
    exports org.example.javalottery;

    exports sockets.client;
    exports sockets.server;
}