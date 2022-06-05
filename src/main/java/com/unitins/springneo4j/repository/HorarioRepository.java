package com.unitins.springneo4j.repository;

import com.unitins.springneo4j.util.Autorizacao;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.exceptions.ResultConsumedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HorarioRepository {

    Autorizacao autorizacao = new Autorizacao();

    public List<Record> listarTodos() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (h:Horario) RETURN h ORDER BY h.ordem ASC");
            List<Record> records = result.list();
            return records;
        }
    }

    public void inserir(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "CREATE (h:Horario {codigo: $codigo, descricao: $descricao, inicio: $inicio, fim: $fim, ordem: $ordem})";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }
    public List<Record> obterGradeDeHorarios(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor)<-[:HorarioAula]-(h:Horario)" +
                    " MATCH (t:Turma)<-[:HorarioAula]-(h:Horario)" +
                    " MATCH (d:Disciplina)<-[:HorarioAula]-(h:Horario)" +
                    " WHERE d.codigo = $codigoDisciplina and (d)<-[:ProfessorDisciplina]-(p) " +
                    " AND (d)<-[:TurmaDisciplina]-(t) " +
                    " RETURN d,t,p,h " +
                    " ORDER BY h.codigo";
            Result result = session.run(query, parametros);
            return result.list();
        }
    }

    public List<Record> obterGradeDeHorariosPorTurma(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor)<-[:HorarioAula]-(h:Horario)" +
                    " MATCH (t:Turma)<-[:HorarioAula]-(h:Horario)" +
                    " MATCH (d:Disciplina)<-[:HorarioAula]-(h:Horario)" +
                    " WHERE t.codigo = $codigoTurma and (d)<-[:ProfessorDisciplina]-(p) " +
                    " AND (d)<-[:TurmaDisciplina]-(t) " +
                    " RETURN d,t,p,h " +
                    " ORDER BY h.ordem";
            Result result = session.run(query, parametros);
            return result.list();
        }
    }

    public void inserirNaGradeManualmente(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String url = " MATCH (d:Disciplina) WHERE d.codigo = $codigoDisciplina " +
                    " MATCH (t:Turma) WHERE t.codigo = $codigoTurma " +
                    " MATCH (p:Professor) WHERE p.codigo = $codigoProfessor " +
                    " MATCH (h:Horario) WHERE h.codigo = $codigoHorario " +
                    " CREATE(h)-[:HorarioAula]->(p) " +
                    " CREATE(h)-[:HorarioAula]->(t) " +
                    " CREATE(h)-[:HorarioAula]->(d)" +
                    " RETURN d;";
            Result result = session.run(url, parametros);
            Record record = result.single();
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public Integer verificarRelHorario(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String url = " MATCH (d:Disciplina)<-[:HorarioAula]-(h:Horario) " +
                    " WHERE d.codigo = $codigoDisciplina " +
                    " WITH d.aulasemanal as qtdAula, count(h) as relac " +
                    " WHERE relac < qtdAula " +
                    " RETURN qtdAula - relac";
            Result result = session.run(url, parametros);
            try {
                Record record = result.single();
                autorizacao.retornarAutorizacao().close();
                session.close();
                return record.get(0).asInt();
            } catch (NoSuchRecordException err) {
                return 0;
            }
        }
    }

    public List<Record> retornarDisciplinasSemNenhumRelacionamento() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String url = " MATCH (d:Disciplina)<-[:HorarioAula]-(h:Horario) " +
                    " WITH COLLECT(d) as discRel " +
                    " match (disc:Disciplina) " +
                    " where not disc in discRel " +
                    " RETURN disc;";
            Result result = session.run(url);
            try {
                List<Record> records = result.list();
                autorizacao.retornarAutorizacao().close();
                session.close();
                return records;
            } catch (NoSuchRecordException err) {
                return new ArrayList<>();
            }
        }
    }

    public List<Record> retornarHorariosDispTurma(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String url = " MATCH (t:Turma)<-[:HorarioAula]-(h:Horario) " +
                    " WHERE t.codigo = $codigoTurma " +
                    " WITH COLLECT(h) as horTurma " +
                    " MATCH (hor:Horario) " +
                    " WHERE NOT hor in horTurma " +
                    " RETURN hor;";
            Result result = session.run(url, parametros);
            try {
                List<Record> records = result.list();
                autorizacao.retornarAutorizacao().close();
                session.close();
                return records;
            } catch (NoSuchRecordException ex) {
                return null;
            }
        }
    }

    public List<Record> retornarHorariosOcupadosProfessor(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String url = " MATCH (p:Professor)<-[:HorarioAula]-(h:Horario) " +
                    " WHERE p.codigo = $codigoProfessor " +
                    " RETURN h;";
            Result result = session.run(url, parametros);
            try {
                List<Record> records = result.list();
                autorizacao.retornarAutorizacao().close();
                session.close();
                return records;
            } catch (NoSuchRecordException ex) {
                return null;
            }
        }
    }

    public Record descobrirQualProfMinistraUmaDisc(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String url = " MATCH (p:Professor)-[:ProfessorDisciplina]->(d:Disciplina) " +
                    " WHERE d.codigo = $codigoDisciplina " +
                    " RETURN p;";
            Result result = session.run(url, parametros);
            try {
                Record record = result.single();
                autorizacao.retornarAutorizacao().close();
                session.close();
                return record;
            } catch (NoSuchRecordException ex) {
                return null;
            }
        }
    }

    public Record descobrirQualTurmaDaDisc(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String url = " MATCH (d:Disciplina)<-[:TurmaDisciplina]-(t:Turma) " +
                    " WHERE d.codigo = $codigoDisciplina " +
                    " RETURN t;";
            Result result = session.run(url, parametros);
            try {
                Record record = result.single();
                autorizacao.retornarAutorizacao().close();
                session.close();
                return record;
            } catch (NoSuchRecordException ex) {
                return null;
            }
        }
    }

    public Record gerarGradeDeHorario(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH(t:Turma)-[:TurmaDisciplina]->(d:Disciplina {codigo: $codigoDisciplina})<-[:ProfessorDisciplina]-(p:Professor)" +
                    " MATCH(h:Horario) WHERE NOT (h)-[:HorarioAula]->(p) AND NOT (h)-[:HorarioAula]->(t) AND NOT (p)-[:RprofessorHorario]->(h)" +
                    " WITH d, t, h, p LIMIT 1" +
                    " OPTIONAL MATCH(d)<-[had:HorarioAula]-(:Horario)" +
                    " WITH d, t, h, p, d.aulasemanal AS n_aula, COUNT(had) as had" +
                    " WHERE had < n_aula" +
                    " CREATE(h)-[:HorarioAula]->(p)" +
                    " CREATE(h)-[:HorarioAula]->(t)" +
                    " CREATE(h)-[:HorarioAula]->(d)" +
                    " RETURN *";
            Result result = session.run(query, parametros);
            try {
                Record record = result.single();
                autorizacao.retornarAutorizacao().close();
                session.close();
                return record;
            } catch (NoSuchRecordException ex) {
                return null;
            }
        }
    }

    public void deletarRelacionamentosHorario(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String url = "MATCH (h:Horario)-[hap:HorarioAula]->(p:Professor) " +
                    " MATCH (h:Horario)-[hat:HorarioAula]->(t:Turma) " +
                    " MATCH (h:Horario)-[had:HorarioAula]->(d:Disciplina) " +
                    " WHERE h.codigo = $codigoHorario AND p.codigo = $codigoProfessor " +
                    " AND t.codigo = $codigoTurma AND d.codigo = $codigoDisciplina " +
                    " DETACH DELETE hap, hat, had;";
            Result result = session.run(url, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void deletarTodosOsRelacionamentosHorario() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (h:Horario)-[hap:HorarioAula]->(p:Professor) " +
                    " MATCH (h:Horario)-[hat:HorarioAula]->(t:Turma) " +
                    " MATCH (h:Horario)-[had:HorarioAula]->(d:Disciplina) " +
                    " DETACH DELETE hap, hat, had;");
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public Integer buscarMaiorCodigo() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (h:Horaraio) RETURN h ORDER BY h.codigo DESC LIMIT 1");
            try {
                Record record = result.single();
                autorizacao.retornarAutorizacao().close();
                session.close();
                return Integer.parseInt(record.get(0).asNode().get("codigo").toString()) + 1;
            } catch (NoSuchRecordException err) {
                return 1;
            }
        }
    }

    public Record buscarPorId(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (h:Horario) WHERE h.codigo = $codigo RETURN h";
            Result result = session.run(query, parametros);
            Record record = result.single();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return record;
        }
    }

    public void deletarPorId(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (h:Horario) WHERE h.codigo = $codigo DETACH DELETE h;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void atualizar(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (h:Horario) WHERE h.codigo = $codigo SET h.descricao = $descricao, h.inicio = $inicio, h.fim = $fim, h.ordem = $ordem;";
            session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public List<Record> buscarPorNome(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (h:Horario) WHERE h.descricao contains '"+parametros.get("descricao").toString()+"' return h;";
            Result result = session.run(query, parametros);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }
}
