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

    public void inserir(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "CREATE (d:Disciplina {codigo: $codigo, nome: $nome, ch: $ch, aulasemanal: $aulasemanal})";
            Result result = session.run(query, parametros);
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
