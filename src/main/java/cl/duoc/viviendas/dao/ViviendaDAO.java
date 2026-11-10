package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Vivienda;

import java.io.IOException;
import java.util.List;


public interface ViviendaDAO<T extends Vivienda> {


    List<T> listar() throws IOException;


    void guardarTodos(List<T> viviendas) throws IOException;
}