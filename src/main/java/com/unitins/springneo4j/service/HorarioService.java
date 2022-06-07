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

import java.util.*;

@Service
public class HorarioService {

    HorarioRepository repository = new HorarioRepository();
    DisciplinaService disciplinaService = new DisciplinaService();
    public Integer numTentativasGrade = 0;

    public List<Horario> getAll() {
        List<Record> records = repository.listarTodos();
        return recordToHorarios(records);
    }

    public Horario insert(Horario horario) {
        Integer codigo = retornarMaiorCodigo();

        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigo", codigo);
        parametros.put("descricao", horario.getDescricao());
        parametros.put("inicio", horario.getInicio());
        parametros.put("fim", horario.getFim());
        parametros.put("ordem", horario.getOrdem());
        Record record = repository.inserir(parametros);
        return recordToHorario(record);
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
        TurmaService turmaService = new TurmaService();
        List<Turma> turmas = turmaService.getAll();
        List<Horario> horarios = new ArrayList<>();
        for (Turma t: turmas) {
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("codigoDisciplina", t.getCodigo());
            List<Record> records = repository.obterGradeDeHorarios(parametros);
            for (Record r: records) {
                Horario horario = recordToHorarioCompleto(r);
                horarios.add(horario);
            }
        }

//        addDisciplinasSemGradeNaGrade(disciplinas);

        return horarios;
    }

    public List<Horario> obterGradeDeHorariosPorTurma(Long codigoTurma) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigoTurma", codigoTurma);
        List<Horario> horarios = new ArrayList<>();
        List<Record> records = repository.obterGradeDeHorariosPorTurma(parametros);
        for (Record r: records) {
            Horario horario = recordToHorarioCompleto(r);
            horarios.add(horario);
        }
        return horarios;
    }

    public void addDisciplinasSemGradeNaGrade(List<Disciplina> disciplinas) {
        TurmaService turmaService = new TurmaService();
        ProfessorService professorService = new ProfessorService();

        for (Disciplina d : disciplinas) {
            int relFaltantes = verificarQtdHorarioRestantes(d.getCodigo());
            Turma turma = turmaService.descobrirTurmaPelaDisciplina(d.getCodigo());
            Professor professor = professorService.buscarProfessorPelaDisciplina(d.getCodigo());
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("codigoTurma", turma.getCodigo());
            List<Record> records = repository.retornarHorariosDispTurma(parametros);
            List<Horario> horar = recordToHorarios(records);
            if (relFaltantes != 0 && relFaltantes <= horar.size()) {
                for (Horario ho: horar) {
                    if (!professorService.verificarRestricaoAPartirDoHorario(professor.getCodigo(), ho.getCodigo())) {
                        HashMap<String, Object> para = new HashMap<>();
                        para.put("codigoTurma", turma.getCodigo());
                        para.put("codigoProfessor", professor.getCodigo());
                        para.put("codigoDisciplina", d.getCodigo());
                        para.put("codigoHorario", ho.getCodigo());
                        repository.inserirNaGradeManualmente(para);
                        relFaltantes--;
                    }
                    if (relFaltantes == 0){
                        break;
                    }
                }
            }
        }
    }



    public HashMap<String, Object> gerarGradeDeHorarios() {
        numTentativasGrade += 1;
        HashMap<String, Object> parametros = new HashMap<>();
        TurmaService turmaService = new TurmaService();
        ProfessorService professorService = new ProfessorService();
        List<Disciplina> disciplinas = disciplinaService.getAll();
        List<Horario> horarios = new ArrayList<>();
        //Deixar a lista com ordem aleatoria
        Collections.shuffle(disciplinas);
        disciplinas.forEach(disciplina -> {
            for (int i=0;i<disciplina.getAulaSemanal(); i++) {
                parametros.put("codigoDisciplina", disciplina.getCodigo());
                Record record = repository.gerarGradeDeHorario(parametros);
                if (record != null) {
                    horarios.add(recordToHorarioCompleto(record));
                }
            }
        });

        List<String> relatorio = new ArrayList<>();
        for (Disciplina d : disciplinas) {
            int relFaltantes = verificarQtdHorarioRestantes(d.getCodigo());
            if (relFaltantes != 0) {
                String observacao = "A disciplina " + d.getNome() + " ficou faltando " + relFaltantes + " horarios dos " + d.getAulaSemanal() + " disponiveis";
                relatorio.add(observacao);
            }
        }

        List<Record> records = repository.retornarDisciplinasSemNenhumRelacionamento();
        for (Record r : records) {
            Disciplina disciplina = disciplinaService.recordToDisciplina(r);
            HashMap<String, Object> param = new HashMap<>();
            param.put("codigoDisciplina", disciplina.getCodigo());
            Record recProfessor = repository.descobrirQualProfMinistraUmaDisc(param);
            Record recTurma = repository.descobrirQualTurmaDaDisc(param);
            Professor p = professorService.recordToProfessor(recProfessor);
            Turma t = turmaService.recordToTurma(recTurma);
            param.put("codigoTurma", t.getCodigo());
            param.put("codigoProfessor", p.getCodigo());
            List<Record> recHorariosDispTurma = repository.retornarHorariosDispTurma(param);
            List<Record> recHorariosOcupadosProfessor = repository.retornarHorariosOcupadosProfessor(param);
            List<Horario> horarioDispTurma = recordToHorarios(recHorariosDispTurma);
            List<Horario> horarioOcupadosProfessor = recordToHorarios(recHorariosOcupadosProfessor);

            forHtr: for (Horario htr: horarioDispTurma) {
                for (Horario h: horarioOcupadosProfessor) {
                    if (Objects.equals(htr.getCodigo(), h.getCodigo())) {
                        String obs = "Horarios para a disciplina " +disciplina.getNome()+" não gerados, o professor "+p.getNome()+" não pode estar em dois lugares ao mesmo tempo. Turma da disciplina: " + disciplina.getNome();
                        relatorio.add(obs);
                        break forHtr;
                    }
                }
            }
        }

        if (relatorio.size() > 0) {
            relatorio = new ArrayList<>();
            regerarGrade();
        }

        relatorio.add("Grade gerada com sucesso");
        relatorio.add("Número de tentativas: " +numTentativasGrade);
        HashMap<String, Object> resultados = new HashMap<>();
        resultados.put("relatorio", relatorio);
        resultados.put("gradeDeHorarios", horarios);
//        addDisciplinasSemGradeNaGrade(disciplinas);

        return resultados;
    }

    public void regerarGrade() {
        deletarTodosOsRelacionamentos();
        gerarGradeDeHorarios();
    }

    public int verificarQtdHorarioRestantes(Long disc) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("codigoDisciplina", disc);
        return repository.verificarRelHorario(parametros);
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

        Node nodeProfessor = (Node) record.asMap().get("p");
        Node nodeDisciplina = (Node) record.asMap().get("d");
        Node nodeHorario = (Node) record.asMap().get("h");
        Node nodeTurma = (Node) record.asMap().get("t");
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
