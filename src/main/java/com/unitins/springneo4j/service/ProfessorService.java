package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.repository.ProfessorRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.Autorizacao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            return professores;
        }
    }
}
