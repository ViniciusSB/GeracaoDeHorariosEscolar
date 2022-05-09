package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.repository.ProfessorRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.unitins.springneo4j.util.Autorizacao;
import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProfessorService {

    @Autowired
    ProfessorRepository repository;

    Autorizacao autorizacao = new Autorizacao();

    public List<Professor> getAll() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (p:Professor) RETURN p ORDER BY p.nome ASC");
            List<Record> records = result.list();
            List<Professor> professores = new ArrayList<>();
            for (Record r : records) {
                Professor p = new Professor();
                p.setId(r.get(0).asNode().id());
                p.setCodigo(Long.parseLong(r.get(0).asNode().get("codigo").toString()));
                p.setNome(r.get(0).asNode().get("nome").toString().substring(1, r.get(0).asNode().get("nome").toString().length() -1));
                professores.add(p);
            }
            autorizacao.retornarAutorizacao().close();
            session.close();
            return professores;
        }
    }

    public void insert(String nome) {

        Integer codigo = retornarMaiorCodigo();

        try (Session session = autorizacao.retornarAutorizacao().session()) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            parametros.put("nome", nome);

            String query = "CREATE (p:Professor {codigo: $codigo, nome: $nome})";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public Integer retornarMaiorCodigo() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (p:Professor) RETURN p ORDER BY p.codigo DESC LIMIT 1");
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

    public Professor getById(Long codigo) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            String query = "MATCH (p:Professor) WHERE p.codigo = $codigo RETURN p";
            Result result = session.run(query, parametros);
            Record record = result.single();
            Professor p = new Professor();
            p.setId(record.get(0).asNode().id());
            p.setNome(record.get(0).asNode().get("nome").toString().substring(1, record.get(0).asNode().get("nome").toString().length() -1));
            p.setCodigo(Long.parseLong(record.get(0).asNode().get("codigo").toString()));

            autorizacao.retornarAutorizacao().close();
            session.close();
            return p;
        }
    }

    public void deleteById(Long codigo) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            String query = "MATCH (p:Professor) WHERE p.codigo = $codigo DETACH DELETE p;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void update(Professor professor) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", professor.getCodigo());
            parametros.put("nome", professor.getNome());
            String query = "MATCH (p:Professor) WHERE p.codigo = $codigo SET p.nome = $nome;";
            session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }
}
