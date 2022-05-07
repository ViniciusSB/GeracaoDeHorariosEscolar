package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.repository.ProfessorRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.Autorizacao;

import java.util.ArrayList;
import java.util.List;

@Service
public class DisciplinaService {

    Autorizacao autorizacao = new Autorizacao();

    public List<Disciplina> getAll() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (d:Disciplina) RETURN d ORDER BY d.codigo ASC");
            List<Record> records = result.list();
            List<Disciplina> disciplinas = new ArrayList<>();
            for (Record r : records) {
                Disciplina d = new Disciplina();
                d.setId(r.get(0).asNode().id());
                d.setCodigo(Long.parseLong(r.get(0).asNode().get("codigo").toString()));
                d.setNome(r.get(0).asNode().get("nome").toString().substring(1, r.get(0).asNode().get("nome").toString().length() -1));
                d.setCargaHoraria(Integer.parseInt(r.get(0).asNode().get("ch").toString()));
                d.setAulaSemanal(Integer.parseInt(r.get(0).asNode().get("aulasemanal").toString()));
                disciplinas.add(d);
            }
            return disciplinas;
        }
    }
}
