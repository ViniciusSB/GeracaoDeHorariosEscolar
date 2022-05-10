package com.unitins.springneo4j.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Horario;
import com.unitins.springneo4j.service.DisciplinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController implements WebMvcConfigurer {

    @Autowired
    DisciplinaService service;

    @GetMapping
    public List<Disciplina> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Disciplina pesquisarDisciplinaById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/pesquisar/{nome}")
    public List<Disciplina> pesquisarDisciplinaPorNome(@PathVariable String nome) {
        return service.searchByName(nome);
    }

    @PostMapping()
    public void cadastrarDisciplina(@RequestBody ObjectNode objectNode) {
        Disciplina d = new Disciplina();
        d.setNome(objectNode.get("nome").asText());
        d.setAulaSemanal(objectNode.get("aulasemanal").asInt());
        d.setCargaHoraria(objectNode.get("ch").asInt());
        service.insert(d);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void atualizarDisciplina(@RequestBody ObjectNode objectNode) {
        Disciplina d = new Disciplina();
        d.setNome(objectNode.get("nome").asText());
        d.setCodigo(objectNode.get("codigo").asLong());
        d.setAulaSemanal(objectNode.get("aulasemanal").asInt());
        d.setCargaHoraria(objectNode.get("ch").asInt());
        service.update(d);
    }

    @DeleteMapping("/{id}")
    public void deletarDisciplina(@PathVariable Long id) {
        service.deleteById(id);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}
