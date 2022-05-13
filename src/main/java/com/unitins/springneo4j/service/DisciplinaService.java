package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Horario;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.springframework.stereotype.Service;
import com.unitins.springneo4j.util.Autorizacao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DisciplinaService {

    Autorizacao autorizacao = new Autorizacao();

    public List<Disciplina> getAll() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina) RETURN d ORDER BY d.codigo ASC";
            Result result = session.run(query);
            List<Record> records = result.list();
            List<Disciplina> disciplinas = recordToDisciplinas(records);
            return disciplinas;
        }
    }

    public void insert(Disciplina disciplina) {

        Integer codigo = retornarMaiorCodigo();

        try (Session session = autorizacao.retornarAutorizacao().session()) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            parametros.put("nome", disciplina.getNome());
            parametros.put("aulasemanal", disciplina.getAulaSemanal());
            parametros.put("ch", disciplina.getCargaHoraria());

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

    public Disciplina getById(Long codigo) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            String query = "MATCH (d:Disciplina) WHERE d.codigo = $codigo RETURN d";
            Result result = session.run(query, parametros);
            Record record = result.single();
            Disciplina d = recordToDisciplina(record);

            autorizacao.retornarAutorizacao().close();
            session.close();
            return d;
        }
    }

    public void deleteById(Long codigo) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            String query = "MATCH (d:Disciplina) WHERE d.codigo = $codigo DETACH DELETE d;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void update(Disciplina disciplina) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("nome", disciplina.getNome());
            parametros.put("codigo", disciplina.getCodigo());
            parametros.put("aulasemanal", disciplina.getAulaSemanal());
            parametros.put("ch", disciplina.getCargaHoraria());

            String query = "MATCH (d:Disciplina) WHERE d.codigo = $codigo SET d.nome = $nome, d.ch = $ch, d.aulasemanal = $aulasemanal;";
            session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public List<Disciplina> searchByName(String nome) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("nome", nome);
            String query = "MATCH (d:Disciplina) WHERE d.nome contains '"+nome+"' return d;";
            Result result = session.run(query, parametros);
            List<Record> r = result.list();
            List<Disciplina> disciplinas = new ArrayList<>();

            autorizacao.retornarAutorizacao().close();
            session.close();
            return disciplinas;
        }
    }

    public List<Disciplina> recordToDisciplinas(List<Record> r) {
        List<Disciplina> disciplinas = new ArrayList<>();
        for (Record record: r) {
            Disciplina d = recordToDisciplina(record);
            disciplinas.add(d);
        }
        return disciplinas;
    }

    public Disciplina recordToDisciplina(Record record) {
        Disciplina d = new Disciplina();
        d.setId(record.get(0).asNode().id());
        d.setNome(record.get(0).asNode().get("nome").toString().substring(1, record.get(0).asNode().get("nome").toString().length() -1));
        d.setCodigo(Long.parseLong(record.get(0).asNode().get("codigo").toString()));
        d.setCargaHoraria(Integer.parseInt(record.get(0).asNode().get("ch").toString()));
        d.setAulaSemanal(Integer.parseInt(record.get(0).asNode().get("aulasemanal").toString()));
        return d;
    }
}
