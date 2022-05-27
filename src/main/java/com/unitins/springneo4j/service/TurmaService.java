package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Turma;
import com.unitins.springneo4j.repository.TurmaRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TurmaService {

    TurmaRepository repository = new TurmaRepository();

    public List<Turma> getAll() {
        List<Record> records = repository.buscarTodos();
        return recordsToTurmas(records);

    }

    public void insert(String nome) {
        Integer codigo = repository.retornarMaiorCodigo();
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        parametros.put("nome", nome);
        repository.inserir(parametros);
    }



    public Turma getById(Long codigo) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        Record record = repository.buscarPorId(parametros);
        return recordToTurma(record);
    }

    public void deleteById(Long codigo) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        repository.deletarPorId(parametros);
    }

    public void update(Turma turma) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", turma.getCodigo());
        parametros.put("nome", turma.getNome());
        repository.atualizar(parametros);
    }

    public List<Turma> searchByName(String nome) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("nome", nome);
        List<Record> records = repository.buscarPorNome(parametros);
        return recordsToTurmas(records);
    }

    public List<Disciplina> disciplinasTurmaByIdTurma(Long codigoTurma) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigoTurma);
        List<Record> records = repository.buscarDisciplinasPorTurma(parametros);
        DisciplinaService disciplinaService = new DisciplinaService();
        return disciplinaService.recordToDisciplinas(records);
    }

    public void deleteRelationShipDisciplinaTurma(Long codigoDisciplina) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigoDisciplina);
        repository.deletarRelacionamentoDisciplinaTurma(parametros);
    }

    public List<Turma> recordsToTurmas(List<Record> records) {
        List<Turma> turmas = new ArrayList<>();
        for (Record r : records) {
            turmas.add(recordToTurma(r));
        }
        return turmas;
    }

    public Turma recordToTurma(Record r) {
        Turma t = new Turma();
        t.setId(r.get(0).asNode().id());
        t.setCodigo(Long.parseLong(r.get(0).asNode().get("codigo").toString()));
        t.setNome(r.get(0).asNode().get("nome").toString().substring(1, r.get(0).asNode().get("nome").toString().length() - 1));
        return t;
    }

    public Turma nodeToTurma(Node node) {
        Turma t = new Turma();
        t.setId(node.id());
        t.setCodigo(Long.parseLong(node.get("codigo").toString()));
        t.setNome(node.get("nome").toString().substring(1, node.get("nome").toString().length() - 1));
        return t;
    }
}
