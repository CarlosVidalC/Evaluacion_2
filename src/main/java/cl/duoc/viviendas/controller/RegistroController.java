package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.dao.ViviendaRepository;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.service.ViviendaService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistroController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtSuperficie;
    @FXML private TextField txtHabitaciones;
    @FXML private TextField txtPiso;
    @FXML private CheckBox chkGastoComun;
    @FXML private CheckBox chkEstacionamiento;
    @FXML private CheckBox chkPatio;
    @FXML private Button btnRegistrar;
    @FXML private Button btnCancelar;
    @FXML private Label lblMensaje;

    private final ViviendaService servicio = new ViviendaService();
    private ViviendaRepository repositorio;
    private int registradas = 0;


    public void setRepositorio(ViviendaRepository repositorio) {
        this.repositorio = repositorio;
    }


    public int getRegistradas() {
        return registradas;
    }

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(ViviendaService.TIPO_DEPARTAMENTO, ViviendaService.TIPO_CASA);
        cmbTipo.setPromptText("Seleccione un tipo");
        cmbTipo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? cmbTipo.getPromptText() : item);
            }
        });
        cmbTipo.valueProperty().addListener((obs, anterior, nuevo) -> actualizarCamposEspecificos());
        actualizarCamposEspecificos();

        btnRegistrar.setOnAction(e -> registrar());
        btnCancelar.setOnAction(e -> ((Stage) btnCancelar.getScene().getWindow()).close());
    }

    private void registrar() {
        try {
            Vivienda nueva = servicio.crear(cmbTipo.getValue(), txtCodigo.getText(),
                    txtSuperficie.getText(), txtHabitaciones.getText(), txtPiso.getText(),
                    chkGastoComun.isSelected(), chkEstacionamiento.isSelected(), chkPatio.isSelected());
            repositorio.agregar(nueva);
            registradas++;
            mostrarExito(nueva.getCodigo() + " (" + nueva.getTipo() + ") registrada correctamente.");
            limpiarFormulario();
        } catch (IllegalArgumentException e) {
            // Dato inválido: se informa y el formulario conserva los valores para corregirlos
            mostrarError(e.getMessage());
        } catch (IOException e) {
            mostrarError("Error al guardar los archivos: " + e.getMessage());
        }
    }

    private void actualizarCamposEspecificos() {
        String tipo = cmbTipo.getValue();
        boolean esDepartamento = ViviendaService.TIPO_DEPARTAMENTO.equals(tipo);
        boolean esCasa = ViviendaService.TIPO_CASA.equals(tipo);

        txtPiso.setDisable(!esDepartamento);
        chkGastoComun.setDisable(!esDepartamento);
        chkEstacionamiento.setDisable(!esDepartamento);
        chkPatio.setDisable(!esCasa);
    }

    private void limpiarFormulario() {
        cmbTipo.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtSuperficie.clear();
        txtHabitaciones.clear();
        txtPiso.clear();
        chkGastoComun.setSelected(false);
        chkEstacionamiento.setSelected(false);
        chkPatio.setSelected(false);
    }

    private void mostrarExito(String texto) {
        lblMensaje.setStyle("-fx-text-fill: #1b7f3b; -fx-font-weight: bold;");
        lblMensaje.setText(texto);
    }

    private void mostrarError(String texto) {
        lblMensaje.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
        lblMensaje.setText(texto);
    }
}
