package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Horario;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.repository.ProfessorRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ProfessorService {

    ProfessorRepository repository = new ProfessorRepository();
    HorarioService horarioService = new HorarioService();
    DisciplinaService disciplinaService = new DisciplinaService();

    public List<Professor> getAll() {
        List<Record> records = repository.buscarTodos();
        List<Professor> professores = recordToProfessores(records);
        return professores;
    }

    public Professor insert(String nome) {
        Integer codigo = repository.retornarMaiorCodigo();
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        parametros.put("nome", nome);
        Record record = repository.inserir(parametros);
        return recordToProfessor(record);
    }

    //Deleta todos os relacionamentos e insere os que foram selecionados
    public List<Horario> inserirRestricaoHorario(Long codigoProfessor, List<Long> codigoHorarios) {
        if (!codigoHorarios.isEmpty()) {
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("codigoProfessor", codigoProfessor);
            repository.deletarAllRelacionamentosHorarioProfessor(parametros);
            List<Record> records = repository.inserirHorariosProfessor(parametros, codigoHorarios);
            return horarioService.recordToHorarios(records);
        }
        return new ArrayList<>();
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

    public Professor update(Professor professor) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", professor.getCodigo());
        parametros.put("nome", professor.getNome());
        Record record = repository.atualizarPorCodigo(parametros);
        return recordToProfessor(record);
    }

    public List<Professor> searchByName(String nome) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("nome", nome);
        List<Record> records = repository.buscarPorNome(parametros);
        return recordToProfessores(records);
    }

    public List<Disciplina> disciplinasProfessorById(Long codigoProfessor) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigoProfessor);
        List<Record> records = repository.buscarDisciplinasDoProfessor(parametros);
        return disciplinaService.recordToDisciplinas(records);
    }

    public Professor buscarProfessorPelaDisciplina(Long disciplina) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigoDisciplina", disciplina);
        Record record = repository.buscarProfessorPelaDisciplina(parametros);
        return recordToProfessor(record);
    }

    public boolean verificarRestricaoAPartirDoHorario(Long codigoProfessor, Long codigoHorario) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigoProfessor", codigoProfessor);
        parametros.put("codigoHorario", codigoHorario);
        return repository.verificarRestricaoAPartirDoHorario(parametros);
    }

    public List<Horario> horariosRestricaoProfessorById(Long codigoProfessor) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigoProfessor);
        List<Record> records = repository.buscarHorariosRestricaoDoProfessor(parametros);
        return horarioService.recordToHorarios(records);
    }

    public void deleteRelationShipDisciplinaProfessor(Long codigoDisciplina) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigoDisciplina);
        repository.deletarRelacionamentoDisciplinaProfessor(parametros);
    }

    public void deleteRelationShipHorarioProfessor(Long codigoHorario, Long codigoProfessor) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigoHorario", codigoHorario);
        parametros.put("codigoProfessor", codigoProfessor);
        repository.deletarRelacionamentoHorarioProfessor(parametros);
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
        return p;
    }

    public Professor nodeToProfessor(Node node) {
        Professor p = new Professor();
        p.setId(node.id());
        p.setNome(node.get("nome").toString().substring(1, node.get("nome").toString().length() -1));
        p.setCodigo(Long.parseLong(node.get("codigo").toString()));
        return p;
    }
}
