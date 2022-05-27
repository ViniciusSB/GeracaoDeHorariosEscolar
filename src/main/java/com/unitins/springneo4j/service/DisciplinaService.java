package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.repository.DisciplinaRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class DisciplinaService {

    DisciplinaRepository repository = new DisciplinaRepository();

    public List<Disciplina> getAll() {
        List<Record> records = repository.buscarTodos();
        return recordToDisciplinas(records);
    }

    public List<Disciplina> getAllWithoutRelationshipWithTurma() {
        List<Record> records = repository.buscarDisciplinasSemRelacionamentoComATurma();
        return recordToDisciplinas(records);
    }

    public List<Disciplina> getAllWithoutRelationshipWithProfessor() {
        List<Record> records = repository.buscarDisciplinasSemRelacionamentoComOProfessor();
        return recordToDisciplinas(records);
    }

    public void insert(Disciplina disciplina) {
        Integer codigo = retornarMaiorCodigo();

        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        parametros.put("nome", disciplina.getNome());
        parametros.put("aulasemanal", disciplina.getAulaSemanal());
        parametros.put("ch", disciplina.getCargaHoraria());
        repository.inserir(parametros);
    }

    public void insertDisciplinaTurma(Integer codigoDisciplina, Integer codigoTurma) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigoDisciplina", codigoDisciplina);
        parametros.put("codigoTurma", codigoTurma);
        repository.inserirDiscipinaNaTurma(parametros);
    }

    public void insertDisciplinaProfessor(Integer codigoDisciplina, Integer codigoProfessor) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigoDisciplina", codigoDisciplina);
        parametros.put("codigoProfessor", codigoProfessor);
        repository.inserirDiscipinaParaOProfessor(parametros);
    }

    public Integer retornarMaiorCodigo() {
        return repository.retornarMaiorCodigo();
    }

    public Disciplina getById(Long codigo) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);

        Record record = repository.buscarPorId(parametros);
        return recordToDisciplina(record);
    }

    public void deleteById(Long codigo) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        repository.deletarPorId(parametros);
    }

    public void update(Disciplina disciplina) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("nome", disciplina.getNome());
        parametros.put("codigo", disciplina.getCodigo());
        parametros.put("aulasemanal", disciplina.getAulaSemanal());
        parametros.put("ch", disciplina.getCargaHoraria());
        repository.update(parametros);
    }

    public List<Disciplina> searchByName(String nome) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("nome", nome);
        List<Record> records = repository.buscarPorNome(parametros);
        return recordToDisciplinas(records);
    }

    public List<Disciplina> recordToDisciplinas(List<Record> r) {
        List<Disciplina> disciplinas = new ArrayList<>();
        for (Record record: r) {
            Disciplina d = recordToDisciplina(record);
            disciplinas.add(d);
        }
        return disciplinas;
    }

    public Disciplina recordToDisciplina(Record record) {
        Disciplina d = new Disciplina();
        d.setId(record.get(0).asNode().id());
        d.setNome(record.get(0).asNode().get("nome").toString().substring(1, record.get(0).asNode().get("nome").toString().length() -1));
        d.setCodigo(Long.parseLong(record.get(0).asNode().get("codigo").toString()));
        d.setCargaHoraria(Integer.parseInt(record.get(0).asNode().get("ch").toString()));
        d.setAulaSemanal(Integer.parseInt(record.get(0).asNode().get("aulasemanal").toString()));
        return d;
    }

    public Disciplina nodeToDisciplina(Node node) {
        Disciplina d = new Disciplina();
        d.setId(node.id());
        d.setNome(node.get("nome").toString().substring(1, node.get("nome").toString().length() -1));
        d.setCodigo(Long.parseLong(node.get("codigo").toString()));
        d.setCargaHoraria(Integer.parseInt(node.get("ch").toString()));
        d.setAulaSemanal(Integer.parseInt(node.get("aulasemanal").toString()));
        return d;
    }
}
