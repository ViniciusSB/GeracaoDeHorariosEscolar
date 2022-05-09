package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Horario;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;
import com.unitins.springneo4j.util.Autorizacao;

import java.util.ArrayList;
import java.util.List;

@Service
public class HorarioService {

    Autorizacao autorizacao = new Autorizacao();

    public List<Horario> getAll() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (h:Horario) RETURN h ORDER BY h.codigo ASC");
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
}
