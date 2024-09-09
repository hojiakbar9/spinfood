module spinfood {
    requires com.opencsv;
    requires java.logging;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.compiler;
    requires jdk.jfr;

    // Open packages for reflection to JavaFX FXML
    opens controller to javafx.fxml;
    opens models.data to javafx.base;
    opens models.enums to javafx.base;
    opens views to javafx.fxml;
    opens models.service to javafx.base, javafx.fxml;
    opens controller.command to javafx.fxml;

    // Export packages for public use
    exports controller;
    exports models.data;
    exports models.enums;
    exports models.service;
    exports views;
    exports controller.command;
    exports models.service.algorithms;
    opens models.service.algorithms to javafx.base, javafx.fxml;
}
