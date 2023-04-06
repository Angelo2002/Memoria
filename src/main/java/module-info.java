module com.mycompany.memoria {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.memoria to javafx.fxml;
    exports com.mycompany.memoria;
}
