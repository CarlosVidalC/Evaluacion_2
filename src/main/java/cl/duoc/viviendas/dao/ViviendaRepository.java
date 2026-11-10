package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ViviendaRepository {

    private final ViviendaDAO<Departamento> departamentoDAO;
    private final ViviendaDAO<Casa> casaDAO;
    private final ObservableList<Vivienda> viviendas = FXCollections.observableArrayList();

    public ViviendaRepository(ViviendaDAO<Departamento> departamentoDAO, ViviendaDAO<Casa> casaDAO) {
        this.departamentoDAO = departamentoDAO;
        this.casaDAO = casaDAO;
    }


    public ObservableList<Vivienda> getViviendas() {
        return viviendas;
    }


    public void cargar() throws IOException {
        List<Vivienda> cargadas = new ArrayList<>();
        cargadas.addAll(departamentoDAO.listar());
        cargadas.addAll(casaDAO.listar());
        viviendas.setAll(cargadas);
    }


    public void agregar(Vivienda vivienda) throws IOException {
        viviendas.add(vivienda);
        try {
            guardar();
        } catch (IOException e) {
            viviendas.remove(viviendas.size() - 1);
            throw e;
        }
    }


    public void eliminar(Vivienda vivienda) throws IOException {
        int indice = -1;
        for (int i = 0; i < viviendas.size(); i++) {
            if (viviendas.get(i) == vivienda) {
                indice = i;
                break;
            }
        }
        if (indice < 0) {
            return;
        }
        viviendas.remove(indice);
        try {
            guardar();
        } catch (IOException e) {
            viviendas.add(indice, vivienda);
            throw e;
        }
    }

    private void guardar() throws IOException {
        List<Departamento> departamentos = new ArrayList<>();
        List<Casa> casas = new ArrayList<>();
        for (Vivienda v : viviendas) {
            if (v instanceof Departamento d) {
                departamentos.add(d);
            } else if (v instanceof Casa c) {
                casas.add(c);
            }
        }
        departamentoDAO.guardarTodos(departamentos);
        casaDAO.guardarTodos(casas);
    }
}
