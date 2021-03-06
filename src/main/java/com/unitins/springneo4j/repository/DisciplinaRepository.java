package com.unitins.springneo4j.repository;

import com.unitins.springneo4j.util.Autorizacao;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;

import java.util.HashMap;
import java.util.List;

public class DisciplinaRepository {

    Autorizacao autorizacao = new Autorizacao();

    public List<Record> buscarTodos() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina) RETURN d ORDER BY d.codigo ASC";
            Result result = session.run(query);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }

    public List<Record> buscarDisciplinasSemRelacionamentoComATurma() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina)<-[td:TurmaDisciplina]-(t:Turma) WITH COLLECT(d) AS discRel MATCH (disc:Disciplina) WHERE NOT disc in discRel RETURN disc;";
            Result result = session.run(query);
            List<Record> record = result.list();
            return record;
        }
    }

    public List<Record> buscarDisciplinasSemRelacionamentoComOProfessor() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina)<-[pd:ProfessorDisciplina]-(p:Professor) WITH COLLECT(d) AS discRel MATCH (disc:Disciplina) WHERE NOT disc in discRel RETURN disc;";
            Result result = session.run(query);
            List<Record> record = result.list();
            return record;
        }
    }


    public Record inserir(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "CREATE (d:Disciplina {codigo: $codigo, nome: $nome, ch: $ch, aulasemanal: $aulasemanal}) RETURN d;";
            Result result = session.run(query, parametros);
            Record record = result.single();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return record;
        }
    }

    public void inserirDiscipinaNaTurma(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina), (t:Turma) WHERE d.codigo = $codigoDisciplina AND t.codigo = $codigoTurma CREATE (d)<-[td:TurmaDisciplina]-(t) RETURN d,t;";
            Result result = session.run(query, parametros);
            List<Record> record = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void inserirDiscipinaParaOProfessor(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina), (p:Professor) WHERE d.codigo = $codigoDisciplina AND p.codigo = $codigoProfessor CREATE (d)<-[pd:ProfessorDisciplina]-(p) RETURN d,p;";
            Result result = session.run(query, parametros);
            List<Record> record = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public Integer retornarMaiorCodigo() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina) RETURN d ORDER BY d.codigo DESC LIMIT 1";
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
            String query = "MATCH (d:Disciplina) WHERE d.codigo = $codigo RETURN d";
            Result result = session.run(query, parametros);
            Record record = result.single();

            autorizacao.retornarAutorizacao().close();
            session.close();
            return record;
        }
    }

    public void deletarPorId(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina) WHERE d.codigo = $codigo DETACH DELETE d;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void update(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina) WHERE d.codigo = $codigo SET d.nome = $nome, d.ch = $ch, d.aulasemanal = $aulasemanal;";
            session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public List<Record> buscarPorNome(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina) WHERE d.nome contains '"+parametros.get("nome").toString()+"' return d;";
            Result result = session.run(query, parametros);
            List<Record> r = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return r;
        }
    }
}
