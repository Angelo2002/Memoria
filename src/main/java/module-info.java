module com.mycompany.memoria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    opens com.mycompany.memoria to javafx.fxml;
    exports com.mycompany.memoria;




}
