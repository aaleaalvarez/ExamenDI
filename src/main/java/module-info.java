module org.example.examen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jasperreports;


    opens org.example.examen to javafx.fxml;
    exports org.example.examen;
}