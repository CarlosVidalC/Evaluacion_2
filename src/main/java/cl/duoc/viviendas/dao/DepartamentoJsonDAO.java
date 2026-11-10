package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Departamento;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class DepartamentoJsonDAO implements ViviendaDAO<Departamento> {

    private static final String ARCHIVO = "departamentos.json";


    private static class DepartamentoDatos {
        String codigo;
        double superficie;
        int habitaciones;
        int piso;
        boolean gastoComunAlDia;
        boolean estacionamiento;
    }

    private final Path ruta;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DepartamentoJsonDAO() {
        this(Path.of(ARCHIVO));
    }

    public DepartamentoJsonDAO(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public List<Departamento> listar() throws IOException {
        List<Departamento> resultado = new ArrayList<>();
        if (!Files.exists(ruta)) {
            return resultado;
        }
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            DepartamentoDatos[] datos = gson.fromJson(reader, DepartamentoDatos[].class);
            if (datos == null) {
                return resultado;
            }
            for (DepartamentoDatos d : datos) {
                Departamento dep = new Departamento(d.codigo, d.superficie, d.habitaciones,
                        d.piso, d.gastoComunAlDia);
                if (d.estacionamiento) {
                    dep.asignarEstacionamiento();
                }
                resultado.add(dep);
            }
        } catch (JsonParseException | IllegalArgumentException | NullPointerException e) {
            throw new IOException("el archivo " + ruta.getFileName()
                    + " no tiene un formato válido (" + e.getMessage() + ")", e);
        }
        return resultado;
    }

    @Override
    public void guardarTodos(List<Departamento> departamentos) throws IOException {
        List<DepartamentoDatos> datos = new ArrayList<>();
        for (Departamento dep : departamentos) {
            DepartamentoDatos d = new DepartamentoDatos();
            d.codigo = dep.getCodigo();
            d.superficie = dep.getSuperficie();
            d.habitaciones = dep.getHabitaciones();
            d.piso = dep.getPiso();
            d.gastoComunAlDia = dep.isGastoComunAlDia();
            d.estacionamiento = dep.tieneEstacionamientoAsignado();
            datos.add(d);
        }
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            gson.toJson(datos, writer);
        }
    }
}