package cl.duoc.viviendas.service;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;

/**
 * Reglas de validación y creación de viviendas a partir de los datos del formulario.
 * No conoce nada de JavaFX: el controlador solo le entrega texto y muestra el resultado.
 */
public class ViviendaService {

    public static final String TIPO_DEPARTAMENTO = "Departamento";
    public static final String TIPO_CASA = "Casa";

    /**
     * Valida los datos en el orden de la pauta y crea la vivienda.
     *
     * @throws IllegalArgumentException con un mensaje que indica el campo y el motivo
     */
    public Vivienda crear(String tipo, String codigo, String superficieTexto,
                          String habitacionesTexto, String pisoTexto,
                          boolean gastoComunAlDia, boolean estacionamiento, boolean patio) {
        if (!TIPO_DEPARTAMENTO.equals(tipo) && !TIPO_CASA.equals(tipo)) {
            throw new IllegalArgumentException("Tipo: debe seleccionar Departamento o Casa.");
        }

        String codigoLimpio = codigo == null ? "" : codigo.trim();
        if (codigoLimpio.isEmpty()) {
            throw new IllegalArgumentException(
                    "Código de propiedad: no puede estar vacío ni contener solo espacios.");
        }

        double superficie = leerSuperficie(superficieTexto);
        int habitaciones = leerEnteroPositivo(habitacionesTexto, "Habitaciones");

        if (TIPO_DEPARTAMENTO.equals(tipo)) {
            int piso = leerEnteroPositivo(pisoTexto, "Piso");
            Departamento departamento = new Departamento(codigoLimpio, superficie, habitaciones,
                    piso, gastoComunAlDia);
            if (estacionamiento) {
                departamento.asignarEstacionamiento();
            }
            return departamento;
        }
        return new Casa(codigoLimpio, superficie, habitaciones, patio);
    }

    /** Criterio del buscador: el texto puede estar en el código, el tipo, las habitaciones o la superficie. */
    public boolean coincideConBusqueda(Vivienda vivienda, String texto) {
        if (texto == null || texto.isBlank()) {
            return true;
        }
        String criterio = texto.trim().toLowerCase();
        return vivienda.getCodigo().toLowerCase().contains(criterio)
                || vivienda.getTipo().toLowerCase().contains(criterio)
                || String.valueOf(vivienda.getHabitaciones()).contains(criterio)
                || String.valueOf(vivienda.getSuperficie()).contains(criterio);
    }

    private double leerSuperficie(String texto) {
        double valor;
        try {
            valor = Double.parseDouble((texto == null ? "" : texto.trim()).replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Superficie: debe ser un número (se aceptan decimales).");
        }
        if (!Double.isFinite(valor) || valor < 20 || valor > 500) {
            throw new IllegalArgumentException("Superficie: debe estar entre 20 y 500 metros cuadrados.");
        }
        return valor;
    }

    private int leerEnteroPositivo(String texto, String campo) {
        int valor;
        try {
            valor = Integer.parseInt(texto == null ? "" : texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + ": debe ser un número entero mayor que cero.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + ": debe ser mayor que cero.");
        }
        return valor;
    }
}
