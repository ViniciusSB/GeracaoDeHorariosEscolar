package com.unitins.springneo4j.repository;

import com.unitins.springneo4j.model.Horario;
import com.unitins.springneo4j.util.Autorizacao;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
