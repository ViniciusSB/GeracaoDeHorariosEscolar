package com.unitins.springneo4j.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node
public class Professor {

    @Id
    private Long id;
    private Long codigo;
    private String nome;

    @Relationship(type = "ProfessorDisciplina", direction = Relationship.Direction.OUTGOING)
    private List<Disciplina> disciplinas;

    @Relationship(type = "RprofessorHorario", direction = Relationship.Direction.OUTGOING)
    private List<Horario> restricaoHorario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    public List<Horario> getRestricaoHorario() {
        return restricaoHorario;
    }

    public void setRestricaoHorario(List<Horario> restricaoHorario) {
        this.restricaoHorario = restricaoHorario;
    }
}
