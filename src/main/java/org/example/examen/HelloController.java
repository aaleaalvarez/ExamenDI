package org.example.examen;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;


import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TextField pesoField;
    @FXML
    private TextField tallaField;
    @FXML
    private Button btnGuardar;
    @FXML
    private TextField edadField;
    @FXML
    private TextArea observacionesField;
    @FXML
    private TextField nombreField;
    @FXML
    private Label info;
    @FXML
    private ChoiceBox<String> actividadBox;
    @FXML
    private ChoiceBox<String> sexoBox;
    @FXML
    private Button btnDescargar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actividadBox.setItems(FXCollections.observableArrayList(
                "Sedentario", "Moderado", "Activo", "Muy activo"
        ));
        sexoBox.setItems(FXCollections.observableArrayList(
                "Hombre", "Mujer"
        ));
    }

    @FXML
    public void calcular(ActionEvent actionEvent) {
        String nombre = nombreField.getText();
        String sexo = sexoBox.getValue();
        String peso = pesoField.getText();
        String edad = edadField.getText();
        String talla = tallaField.getText();
        String actividad = actividadBox.getValue();
        String obvervacion = observacionesField.getText();

        if (nombre.isEmpty() || sexo == null || peso.isEmpty() || edad.isEmpty() || talla.isEmpty() || actividad == null) {
            info.setText("Faltan datos en el formulario");
            return;
        }
        try {
            double datoPeso = Double.parseDouble(peso);
            int datoEdad = Integer.parseInt(edad);
            double datoTalla = Double.parseDouble(talla);
            double datoActividad = obtenerFactorActividad(sexo, actividad);
            double ger;
            double get;

            if (sexo.equalsIgnoreCase("Hombre")) {
                ger = (88.362 + (13.397 * datoPeso) + (4.799 * datoTalla) - (5.677 * datoEdad));
            } else if (sexo.equalsIgnoreCase("Mujer")) {
                ger = (447.593 + (9.247 * datoPeso) + (3.098 * datoTalla) - (4.330 * datoEdad));
            } else {
                info.setText("El sexo introducido no es válido");
                return;
            }

            get = ger * datoActividad;

            info.setText(String.format("El cliente %s tiene un GER de %.0f y un GET de %.0f", nombre, ger, get));

        } catch (NumberFormatException e) {
            info.setText("Por favor, introduzca valores numéricos en peso, edad, talla.");
        }
    }

    private double obtenerFactorActividad(String sexo, String actividad) {
        switch (actividad) {
            case "Sedentario":
                return 1.3;
            case "Moderado":
                return sexo.equalsIgnoreCase("Hombre") ? 1.6 : 1.5;
            case "Activo":
                return sexo.equalsIgnoreCase("Hombre") ? 1.7 : 1.6;
            case "Muy activo":
                return sexo.equalsIgnoreCase("Hombre") ? 2.1 : 1.9;
            default:
                return 1;
        }
    }

    @FXML
    public void descargar(ActionEvent actionEvent) throws SQLException, JRException {
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/ExamenDI", "root", "04112003");
        JasperPrint jasperPrint = JasperFillManager.fillReport("ListadoClientes.jasper", null, c);

        JasperViewer.viewReport(jasperPrint, false);

        JRPdfExporter exp = new JRPdfExporter();
        exp.setExporterInput(new SimpleExporterInput(jasperPrint));
        exp.setExporterOutput(new SimpleOutputStreamExporterOutput("ListadoClientes.pdf"));
        exp.setConfiguration(new SimplePdfExporterConfiguration());
        exp.exportReport();
    }
}