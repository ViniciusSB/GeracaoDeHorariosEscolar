package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Horario;
import com.unitins.springneo4j.model.Professor;
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
public class HorarioService {

    Autorizacao autorizacao = new Autorizacao();

    public List<Horario> getAll() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (h:Horario) RETURN h ORDER BY h.ordem ASC");
            List<Record> records = result.list();
            List<Horario> horarios = new ArrayList<>();
            for (Record r : records) {
                Horario h = new Horario();
                h.setId(r.get(0).asNode().id());
                h.setCodigo(Long.parseLong(r.get(0).asNode().get("codigo").toString()));
                h.setDescricao(r.get(0).asNode().get("descricao").toString().substring(1, r.get(0).asNode().get("descricao").toString().length() -1));
                h.setInicio(Integer.parseInt(r.get(0).asNode().get("inicio").toString()));
                h.setFim(Integer.parseInt(r.get(0).asNode().get("fim").toString()));
                h.setOrdem(Integer.parseInt(r.get(0).asNode().get("ordem").toString()));
                horarios.add(h);
            }
            return horarios;
        }
    }

    public void insert(Horario horario) {

        Integer codigo = retornarMaiorCodigo();

        try (Session session = autorizacao.retornarAutorizacao().session()) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            parametros.put("descricao", horario.getDescricao());
            parametros.put("inicio", horario.getInicio());
            parametros.put("fim", horario.getFim());
            parametros.put("ordem", horario.getOrdem());

            String query = "CREATE (h:Horario {codigo: $codigo, descricao: $descricao, inicio: $inicio, fim: $fim, ordem: $ordem})";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public Integer retornarMaiorCodigo() {
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

    public Horario getById(Long codigo) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            String query = "MATCH (h:Horario) WHERE h.codigo = $codigo RETURN h";
            Result result = session.run(query, parametros);
            Record record = result.single();
            Horario h = new Horario();
            h.setId(record.get(0).asNode().id());
            h.setDescricao(record.get(0).asNode().get("descricao").toString().substring(1, record.get(0).asNode().get("descricao").toString().length() -1));
            h.setCodigo(Long.parseLong(record.get(0).asNode().get("codigo").toString()));
            h.setInicio(Integer.parseInt(record.get(0).asNode().get("inicio").toString()));
            h.setFim(Integer.parseInt(record.get(0).asNode().get("fim").toString()));
            h.setOrdem(Integer.parseInt(record.get(0).asNode().get("ordem").toString()));

            autorizacao.retornarAutorizacao().close();
            session.close();
            return h;
        }
    }

    public void deleteById(Long codigo) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            String query = "MATCH (h:Horario) WHERE h.codigo = $codigo DETACH DELETE h;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void update(Horario horario) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("descricao", horario.getDescricao());
            parametros.put("codigo", horario.getCodigo());
            parametros.put("inicio", horario.getInicio());
            parametros.put("fim", horario.getFim());
            parametros.put("ordem", horario.getOrdem());

            String query = "MATCH (h:Horario) WHERE h.codigo = $codigo SET h.descricao = $descricao, h.inicio = $inicio, h.fim = $fim, h.ordem = $ordem;";
            session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public List<Horario> searchByName(String descricao) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("descricao", descricao);
            String query = "MATCH (h:Horario) WHERE h.descricao contains '"+descricao+"' return h;";
            Result result = session.run(query, parametros);
            List<Record> record = result.list();
            List<Horario> horarios = new ArrayList<>();
            for (Record r: record) {
                Horario h = new Horario();
                h.setId(r.get(0).asNode().id());
                h.setDescricao(r.get(0).asNode().get("descricao").toString().substring(1, r.get(0).asNode().get("descricao").toString().length() -1));
                h.setCodigo(Long.parseLong(r.get(0).asNode().get("codigo").toString()));
                h.setInicio(Integer.parseInt(r.get(0).asNode().get("inicio").toString()));
                h.setFim(Integer.parseInt(r.get(0).asNode().get("fim").toString()));
                h.setOrdem(Integer.parseInt(r.get(0).asNode().get("ordem").toString()));
                horarios.add(h);
            }
            autorizacao.retornarAutorizacao().close();
            session.close();
            return horarios;
        }
    }
}
