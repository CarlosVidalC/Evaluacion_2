package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
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


public class CasaJsonDAO implements ViviendaDAO<Casa> {

    private static final String ARCHIVO = "casas.json";

    private static class CasaDatos {
        String codigo;
        double superficie;
        int habitaciones;
        boolean tienePatio;
    }

    private final Path ruta;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public CasaJsonDAO() {
        this(Path.of(ARCHIVO));
    }

    public CasaJsonDAO(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public List<Casa> listar() throws IOException {
        List<Casa> resultado = new ArrayList<>();
        if (!Files.exists(ruta)) {
            return resultado;
        }
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            CasaDatos[] datos = gson.fromJson(reader, CasaDatos[].class);
            if (datos == null) {
                return resultado;
            }
            for (CasaDatos d : datos) {
                resultado.add(new Casa(d.codigo, d.superficie, d.habitaciones, d.tienePatio));
            }
        } catch (JsonParseException | IllegalArgumentException | NullPointerException e) {
            throw new IOException("el archivo " + ruta.getFileName()
                    + " no tiene un formato válido (" + e.getMessage() + ")", e);
        }
        return resultado;
    }

    @Override
    public void guardarTodos(List<Casa> casas) throws IOException {
        List<CasaDatos> datos = new ArrayList<>();
        for (Casa casa : casas) {
            CasaDatos d = new CasaDatos();
            d.codigo = casa.getCodigo();
            d.superficie = casa.getSuperficie();
            d.habitaciones = casa.getHabitaciones();
            d.tienePatio = casa.isTienePatio();
            datos.add(d);
        }
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            gson.toJson(datos, writer);
        }
    }
}
