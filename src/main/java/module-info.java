module com.example.memory {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.memory to javafx.fxml;
    exports com.example.memory;
}