package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.dao.CasaJsonDAO;
import cl.duoc.viviendas.dao.DepartamentoJsonDAO;
import cl.duoc.viviendas.dao.ViviendaRepository;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.service.ViviendaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class MainController {

    private static final String VISTA_REGISTRO = "/cl/duoc/viviendas/view/registro-view.fxml";
    private static final Locale CHILE = Locale.forLanguageTag("es-CL");

    @FXML private TextField txtBuscar;
    @FXML private TableView<Vivienda> tblViviendas;
    @FXML private TableColumn<Vivienda, String> colTipo;
    @FXML private TableColumn<Vivienda, String> colCodigo;
    @FXML private TableColumn<Vivienda, String> colSuperficie;
    @FXML private TableColumn<Vivienda, String> colHabitaciones;
    @FXML private TableColumn<Vivienda, String> colCosto;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    private final ViviendaService servicio = new ViviendaService();
    private ViviendaRepository repositorio;
    private FilteredList<Vivienda> viviendasFiltradas;

    @FXML
    private void initialize() {
        // Datos: el Repository carga los archivos y entrega la colección observable
        repositorio = new ViviendaRepository(new DepartamentoJsonDAO(), new CasaJsonDAO());
        try {
            repositorio.cargar();
        } catch (IOException e) {
            mostrarError("Error al leer los archivos: " + e.getMessage());
        }

        viviendasFiltradas = new FilteredList<>(repositorio.getViviendas(), v -> true);
        tblViviendas.setItems(viviendasFiltradas);

        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colSuperficie.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getSuperficie())));
        colHabitaciones.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getHabitaciones())));
        colCosto.setCellValueFactory(c ->
                new SimpleStringProperty(formatearCosto(c.getValue().calcularCostoArriendo())));

        // Eventos
        txtBuscar.textProperty().addListener((obs, anterior, texto) ->
                viviendasFiltradas.setPredicate(v -> servicio.coincideConBusqueda(v, texto)));
        btnNuevo.setOnAction(e -> abrirRegistro());
        btnEliminar.setOnAction(e -> eliminarSeleccionada());
    }

    private void abrirRegistro() {
        URL vista = getClass().getResource(VISTA_REGISTRO);
        if (vista == null) {
            mostrarError("No se encontró la vista de registro: " + VISTA_REGISTRO);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(vista);
            Parent raiz = loader.load();
            RegistroController controlador = loader.getController();
            controlador.setRepositorio(repositorio);

            Stage ventana = new Stage();
            ventana.setTitle("Registrar vivienda");
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.initOwner(btnNuevo.getScene().getWindow());
            ventana.setScene(new Scene(raiz));
            ventana.showAndWait();


            int registradas = controlador.getRegistradas();
            if (registradas > 0) {
                mostrarExito(registradas + " vivienda(s) registrada(s) correctamente.");
            }
        } catch (IOException e) {
            mostrarError("No se pudo abrir la vista de registro: " + e.getMessage());
        }
    }

    private void eliminarSeleccionada() {
        Vivienda seleccionada = tblViviendas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Debe seleccionar una vivienda de la tabla para eliminarla.");
            return;
        }
        try {
            repositorio.eliminar(seleccionada);
            mostrarExito(seleccionada.getCodigo() + " (" + seleccionada.getTipo()
                    + ") eliminada correctamente.");
        } catch (IOException e) {
            mostrarError("Error al guardar los archivos: " + e.getMessage());
        }
    }

    private String formatearCosto(double costo) {
        return String.format(CHILE, "$%,.0f", costo);
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
