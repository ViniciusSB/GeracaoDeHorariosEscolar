package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.model.Turma;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.unitins.springneo4j.util.Autorizacao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TurmaService {

    Autorizacao autorizacao = new Autorizacao();

    public List<Turma> getAll() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (t:Turma) RETURN t ORDER BY t.codigo ASC");
            List<Record> records = result.list();
            List<Turma> turmas = new ArrayList<>();
            for (Record r : records) {
                Turma t = new Turma();
                t.setId(r.get(0).asNode().id());
                t.setCodigo(Long.parseLong(r.get(0).asNode().get("codigo").toString()));
                t.setNome(r.get(0).asNode().get("nome").toString().substring(1, r.get(0).asNode().get("nome").toString().length() -1));
                turmas.add(t);
            }
            return turmas;
        }
    }

    public void insert(String nome) {

        Integer codigo = retornarMaiorCodigo();

        try (Session session = autorizacao.retornarAutorizacao().session()) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            parametros.put("nome", nome);

            String query = "CREATE (t:Turma {codigo: $codigo, nome: $nome})";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public Integer retornarMaiorCodigo() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (t:Turma) RETURN t ORDER BY t.codigo DESC LIMIT 1");
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

    public Turma getById(Long codigo) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            String query = "MATCH (t:Turma) WHERE t.codigo = $codigo RETURN t";
            Result result = session.run(query, parametros);
            Record record = result.single();
            Turma t = new Turma();
            t.setId(record.get(0).asNode().id());
            t.setNome(record.get(0).asNode().get("nome").toString().substring(1, record.get(0).asNode().get("nome").toString().length() -1));
            t.setCodigo(Long.parseLong(record.get(0).asNode().get("codigo").toString()));

            autorizacao.retornarAutorizacao().close();
            session.close();
            return t;
        }
    }

    public void deleteById(Long codigo) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            String query = "MATCH (t:Turma) WHERE t.codigo = $codigo DETACH DELETE t;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void update(Turma turma) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", turma.getCodigo());
            parametros.put("nome", turma.getNome());
            String query = "MATCH (t:Turma) WHERE t.codigo = $codigo SET t.nome = $nome;";
            session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public List<Turma> searchByName(String nome) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("nome", nome);
            String query = "MATCH (t:Turma) WHERE t.nome contains '"+nome+"' return t;";
            Result result = session.run(query, parametros);
            List<Record> record = result.list();
            List<Turma> turmas = new ArrayList<>();
            for (Record r: record) {
                Turma t = new Turma();
                t.setId(r.get(0).asNode().id());
                t.setNome(r.get(0).asNode().get("nome").toString().substring(1, r.get(0).asNode().get("nome").toString().length() -1));
                t.setCodigo(Long.parseLong(r.get(0).asNode().get("codigo").toString()));
                turmas.add(t);
            }
            autorizacao.retornarAutorizacao().close();
            session.close();
            return turmas;
        }
    }
}
