package com.unitins.springneo4j.repository;

import com.unitins.springneo4j.model.Professor;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Collection;
import java.util.List;

public interface ProfessorRepository extends Neo4jRepository<Professor, Long> {

    @Query("match(p:Professor) return p;")
    List<Professor> getAllProfessores();
}
