package com.unitins.springneo4j.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Horario;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.model.Turma;
import com.unitins.springneo4j.repository.HorarioRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class HorarioService {

    HorarioRepository repository = new HorarioRepository();
    DisciplinaService disciplinaService = new DisciplinaService();

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

    public List<Horario> obterGradeDeHorarios() {
        List<Record> records = repository.obterGradeDeHorarios();
        List<Horario> horarios = new ArrayList<>();
        if (!records.isEmpty()) {
            for (int i=0;i<records.size();i++) {
                horarios.add(recordToHorarioCompleto(records.get(i)));
            }
        }
        return horarios;
    }

    public List<Horario> gerarGradeDeHorarios() {
        HashMap<String, Object> parametros = new HashMap<>();

        List<Disciplina> disciplinas = disciplinaService.getAll();
        List<Horario> horarios = new ArrayList<>();
        disciplinas.forEach(disciplina -> {
            for (int i=0;i<disciplina.getAulaSemanal(); i++) {
                parametros.put("codigoDisciplina", disciplina.getCodigo());
                Record record = repository.gerarGradeDeHorario(parametros);
                if (record != null) {
                    horarios.add(recordToHorarioCompleto(record));
                }
            }
        });

        return horarios;
    }

    public void deletarRelacionamentoHorario(ObjectNode objectNode) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigoProfessor", objectNode.get("codigoProfessor").asInt());
        parametros.put("codigoHorario", objectNode.get("codigoHorario").asInt());
        parametros.put("codigoDisciplina", objectNode.get("codigoDisciplina").asInt());
        parametros.put("codigoTurma", objectNode.get("codigoTurma").asInt());
        repository.deletarRelacionamentosHorario(parametros);
    }

    public void deletarTodosOsRelacionamentos() {
        repository.deletarTodosOsRelacionamentosHorario();
    }

    public Horario recordToHorarioCompleto(Record record) {
        ProfessorService professorService = new ProfessorService();
        TurmaService turmaService = new TurmaService();

        Node nodeProfessor = record.get(4).asNode();
        Node nodeDisciplina = record.get(0).asNode();
        Node nodeHorario = record.get(1).asNode();
        Node nodeTurma = record.get(5).asNode();
        Horario horario = nodeToHorario(nodeHorario);
        Professor professor = professorService.nodeToProfessor(nodeProfessor);
        Turma turma = turmaService.nodeToTurma(nodeTurma);
        Disciplina disciplina = disciplinaService.nodeToDisciplina(nodeDisciplina);
        List<Professor> professores = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();
        List<Turma> turmas = new ArrayList<>();
        professores.add(professor);
        disciplinas.add(disciplina);
        turmas.add(turma);
        horario.setProfessores(professores);
        horario.setTurmas(turmas);
        horario.setDisciplinas(disciplinas);
        return horario;
    }

    public Horario nodeToHorario(Node node) {
        Horario h = new Horario();
        h.setId(node.id());
        h.setDescricao(node.get("descricao").toString().substring(1, node.get("descricao").toString().length() -1));
        h.setCodigo(Long.parseLong(node.get("codigo").toString()));
        h.setInicio(Integer.parseInt(node.get("inicio").toString()));
        h.setFim(Integer.parseInt(node.get("fim").toString()));
        h.setOrdem(Integer.parseInt(node.get("ordem").toString()));
        return h;
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
