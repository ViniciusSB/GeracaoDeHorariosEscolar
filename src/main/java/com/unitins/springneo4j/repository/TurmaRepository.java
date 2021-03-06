package com.unitins.springneo4j.repository;

import com.unitins.springneo4j.util.Autorizacao;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;

import java.util.HashMap;
import java.util.List;

public class TurmaRepository {

    Autorizacao autorizacao = new Autorizacao();

    public List<Record> buscarTodos() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (t:Turma) RETURN t ORDER BY t.codigo ASC";
            Result result = session.run(query);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }

    public Record inserir(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "CREATE (t:Turma {codigo: $codigo, nome: $nome}) return t;";
            Result result = session.run(query, parametros);
            Record record = result.single();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return record;
        }
    }

    public Integer retornarMaiorCodigo() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (t:Turma) RETURN t ORDER BY t.codigo DESC LIMIT 1";
            Result result = session.run(query);
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
            String query = "MATCH (t:Turma) WHERE t.codigo = $codigo RETURN t";
            Result result = session.run(query, parametros);
            Record record = result.single();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return record;
        }
    }

    public void deletarPorId(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (t:Turma) WHERE t.codigo = $codigo DETACH DELETE t;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void deletarRelacionamentoDisciplinaTurma(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina)<-[td:TurmaDisciplina]-(t:Turma) WHERE d.codigo = $codigo DETACH DELETE td;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public Record atualizar(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (t:Turma) WHERE t.codigo = $codigo SET t.nome = $nome RETURN t;";
            Result result = session.run(query, parametros);
            Record record = result.single();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return record;
        }
    }

    public List<Record> buscarPorNome(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (t:Turma) WHERE t.nome contains '"+parametros.get("nome").toString()+"' return t;";
            Result result = session.run(query, parametros);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }

    public List<Record> buscarDisciplinasPorTurma(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (t:Turma)-[:TurmaDisciplina]->(d:Disciplina) WHERE t.codigo = $codigo return d;";
            Result result = session.run(query, parametros);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }

    public Record descobrirTurmaPelaDisciplina(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (t:Turma)-[:TurmaDisciplina]->(d:Disciplina) WHERE d.codigo = $codigoDisciplina return t;";
            Result result = session.run(query, parametros);
            Record record = result.single();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return record;
        }
    }
}
