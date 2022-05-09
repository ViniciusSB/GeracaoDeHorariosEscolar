package com.unitins.springneo4j.service;

import com.unitins.springneo4j.model.Turma;
import com.unitins.springneo4j.repository.ProfessorRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.unitins.springneo4j.util.Autorizacao;

import java.util.ArrayList;
import java.util.List;

@Service
public class TurmaService {

    @Autowired
    ProfessorRepository repository;

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
}
