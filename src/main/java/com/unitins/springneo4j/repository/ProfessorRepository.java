package com.unitins.springneo4j.repository;

import com.unitins.springneo4j.util.Autorizacao;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfessorRepository {

    Autorizacao autorizacao = new Autorizacao();

    public List<Record> buscarTodos() {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Result result = session.run("MATCH (p:Professor) RETURN p ORDER BY p.nome ASC");
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }

    public List<Record> buscarDisciplinaDeUmProfessor(Long codigo) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);

            String query = "MATCH (p:Professor)-[pd:ProfessorDisciplina]->(d:Disciplina) WHERE p.codigo = $codigo RETURN d;";
            Result result = session.run(query, parametros);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }

    public void inserir(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "CREATE (p:Professor {codigo: $codigo, nome: $nome})";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public List<Record> inserirHorariosProfessor(HashMap<String, Object> parametros, List<Long> codigos) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH(p1:Professor) WHERE p1.codigo = $codigoProfessor ");
            //MATC
            for (int i = 0; i<codigos.size(); i++) {
                String h = "h" + i;
                query.append("MATCH("+h+":Horario) WHERE "+h+".codigo = "+codigos.get(i).toString()+" ");
            }
            //CREATE
            for (int i = 0; i<codigos.size(); i++) {
                String h = "h" + i;
                query.append("CREATE(p1)-[:RprofessorHorario]->("+h+") ");
            }
            //RETURN
            query.append("RETURN ");
            for (int i = 0; i<codigos.size(); i++) {
                String h = "h" + i;
                if (i != codigos.size()-1) {
                    query.append(""+h+", ");
                    continue;
                }
                query.append(""+h+"; ");
            }
            Result result = session.run(query.toString(), parametros);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
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

    public Record buscarPorCodigo(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor) WHERE p.codigo = $codigo RETURN p";
            Result result = session.run(query, parametros);
            Record record = result.single();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return record;
        }
    }

    public void deletarPorCodigo(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor) WHERE p.codigo = $codigo DETACH DELETE p;";
            session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void atualizarPorCodigo(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor) WHERE p.codigo = $codigo SET p.nome = $nome;";
            session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public List<Record> buscarPorNome(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor) WHERE toLower(p.nome) contains '"+parametros.get("nome").toString().toLowerCase()+"' RETURN p;";
            Result result = session.run(query, parametros);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }

    public List<Record> buscarDisciplinasDoProfessor(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor)-[:ProfessorDisciplina]->(d:Disciplina) WHERE p.codigo = $codigo RETURN d;";
            Result result = session.run(query, parametros);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }

    public List<Record> buscarHorariosRestricaoDoProfessor(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor)-[:RprofessorHorario]->(h:Horario) WHERE p.codigo = $codigo RETURN h;";
            Result result = session.run(query, parametros);
            List<Record> records = result.list();
            autorizacao.retornarAutorizacao().close();
            session.close();
            return records;
        }
    }

    public void deletarRelacionamentoDisciplinaProfessor(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (d:Disciplina)<-[pd:ProfessorDisciplina]-(p:Professor) WHERE d.codigo = $codigo DETACH DELETE pd;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void deletarRelacionamentoHorarioProfessor(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor)-[rph:RprofessorHorario]->(h:Horario) WHERE p.codigo = $codigoProfessor AND h.codigo = $codigoHorario DETACH DELETE rph;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }

    public void deletarAllRelacionamentosHorarioProfessor(HashMap<String, Object> parametros) {
        try (Session session = autorizacao.retornarAutorizacao().session()) {
            String query = "MATCH (p:Professor)-[rph:RprofessorHorario]->(h:Horario) WHERE p.codigo = $codigoProfessor DETACH DELETE rph;";
            Result result = session.run(query, parametros);
            autorizacao.retornarAutorizacao().close();
            session.close();
        }
    }
}
