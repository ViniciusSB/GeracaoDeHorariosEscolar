package com.unitins.springneo4j.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Horario;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RestController
@RequestMapping("/professores")
public class ProfessorController implements WebMvcConfigurer {

    @Autowired
    ProfessorService service;

    @GetMapping
    public List<Professor> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Professor pesquisarProfessorPorId(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/pesquisar")
    public List<Professor> pesquisarProfessorPorNome(@RequestParam String nome) {
        return service.searchByName(nome);
    }

    @GetMapping("/{id}/disciplinas")
    public List<Disciplina> pesquisarDisciplinasProfessorById(@PathVariable Long id) {
        return service.disciplinasProfessorById(id);
    }

    @GetMapping("/{id}/horarios")
    public List<Horario> pesquisarHorariosRestricaoProfessorById(@PathVariable Long id) {
        return service.horariosRestricaoProfessorById(id);
    }

    @PostMapping
    public Professor cadastrarProfessor(@RequestBody String nome) {
        return service.insert(nome);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Professor atualizarProfessor(@RequestBody ObjectNode objectNode) {
        Professor p = new Professor();
        p.setNome(objectNode.get("nome").asText());
        p.setCodigo(objectNode.get("codigo").asLong());
        return service.update(p);
    }

    @PostMapping("/horarios")
    public List<Horario> inserirRestricaoHorarioProfessor(@RequestParam Long codigoProfessor, @RequestParam List<Long> codigoHorarios) {
        return service.inserirRestricaoHorario(codigoProfessor, codigoHorarios);
    }

    @DeleteMapping("/{id}")
    public void deletarProfessor(@PathVariable Long id) {
        service.deleteById(id);
    }

    @DeleteMapping("/disciplina/{codigoDisciplina}")
    public void deletarRelacionamentoDisciplinaProfessor(@PathVariable Long codigoDisciplina) {
        service.deleteRelationShipDisciplinaProfessor(codigoDisciplina);
    }

    @DeleteMapping("/deleteHorario")
    public void deletarRelacionamentoHorarioProfessor(@RequestParam Long codigoHorario, @RequestParam Long codigoProfessor) {
        service.deleteRelationShipHorarioProfessor(codigoHorario, codigoProfessor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}
