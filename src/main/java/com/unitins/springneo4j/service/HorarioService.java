package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Horario;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.repository.HorarioRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.springframework.stereotype.Service;
import com.unitins.springneo4j.util.Autorizacao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HorarioService {

    HorarioRepository repository = new HorarioRepository();

    public List<Horario> getAll() {
        List<Record> records = repository.listarTodos();
        return recordToHorarios(records);
    }

    public void insert(Horario horario) {
        Integer codigo = retornarMaiorCodigo();

        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        parametros.put("descricao", horario.getDescricao());
        parametros.put("inicio", horario.getInicio());
        parametros.put("fim", horario.getFim());
        parametros.put("ordem", horario.getOrdem());
        repository.inserir(parametros);
    }

    public Integer retornarMaiorCodigo() {
        return repository.buscarMaiorCodigo();
    }

    public Horario getById(Long codigo) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        Record record = repository.buscarPorId(parametros);
        return recordToHorario(record);
    }

    public void deleteById(Long codigo) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        repository.deletarPorId(parametros);
    }

    public void update(Horario horario) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("descricao", horario.getDescricao());
        parametros.put("codigo", horario.getCodigo());
        parametros.put("inicio", horario.getInicio());
        parametros.put("fim", horario.getFim());
        parametros.put("ordem", horario.getOrdem());
        repository.atualizar(parametros);
    }

    public List<Horario> searchByName(String descricao) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("descricao", descricao);
        List<Record> records = repository.buscarPorNome(parametros);
        return recordToHorarios(records);
    }

    public Horario recordToHorario(Record r) {
        Horario h = new Horario();
        h.setId(r.get(0).asNode().id());
        h.setDescricao(r.get(0).asNode().get("descricao").toString().substring(1, r.get(0).asNode().get("descricao").toString().length() -1));
        h.setCodigo(Long.parseLong(r.get(0).asNode().get("codigo").toString()));
        h.setInicio(Integer.parseInt(r.get(0).asNode().get("inicio").toString()));
        h.setFim(Integer.parseInt(r.get(0).asNode().get("fim").toString()));
        h.setOrdem(Integer.parseInt(r.get(0).asNode().get("ordem").toString()));
        return h;
    }

    public List<Horario> recordToHorarios(List<Record> records) {
        List<Horario> horarios = new ArrayList<>();
        for (Record r: records) {
            Horario horario = recordToHorario(r);
            horarios.add(horario);
        }
        return horarios;
    }
}
