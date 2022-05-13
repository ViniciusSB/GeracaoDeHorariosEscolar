package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.repository.ProfessorRepository;
import org.neo4j.driver.Record;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ProfessorService {

    ProfessorRepository repository = new ProfessorRepository();
    DisciplinaService disciplinaService = new DisciplinaService();

    public List<Professor> getAll() {
        List<Record> records = repository.buscarTodos();
        List<Professor> professores = recordToProfessores(records);
        return professores;
    }

    public void insert(String nome) {
        Integer codigo = repository.retornarMaiorCodigo();
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        parametros.put("nome", nome);
        repository.inserir(parametros);
    }

    public Professor getById(Long codigo) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        Record record = repository.buscarPorCodigo(parametros);
        return recordToProfessor(record);
    }

    public void deleteById(Long codigo) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        repository.deletarPorCodigo(parametros);
    }

    public void update(Professor professor) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", professor.getCodigo());
        parametros.put("nome", professor.getNome());
        repository.atualizarPorCodigo(parametros);
    }

    public List<Professor> searchByName(String nome) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("nome", nome);
        List<Record> records = repository.buscarPorNome(parametros);
        return recordToProfessores(records);
    }

    public List<Professor> recordToProfessores(List<Record> r) {
        List<Professor> professores = new ArrayList<>();
        for (Record record: r) {
            Professor p = recordToProfessor(record);
            professores.add(p);
        }
        return professores;
    }

    public Professor recordToProfessor(Record record) {
        Professor p = new Professor();
        p.setId(record.get(0).asNode().id());
        p.setNome(record.get(0).asNode().get("nome").toString().substring(1, record.get(0).asNode().get("nome").toString().length() -1));
        p.setCodigo(Long.parseLong(record.get(0).asNode().get("codigo").toString()));
        List<Record> re = repository.buscarDisciplinaDeUmProfessor(p.getCodigo());
        if (re.size() > 0) {
            List<Disciplina> disciplinas = disciplinaService.recordToDisciplinas(re);
            p.setDisciplinas(disciplinas);
        } else {
            p.setDisciplinas(new ArrayList<>());
        }
        return p;
    }
}
