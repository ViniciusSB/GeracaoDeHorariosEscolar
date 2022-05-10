package com.unitins.springneo4j.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.model.Turma;
import com.unitins.springneo4j.service.ProfessorService;
import com.unitins.springneo4j.service.TurmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaController implements WebMvcConfigurer {

    @Autowired
    TurmaService service;

    @GetMapping
    public List<Turma> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Turma pesquisarTurmaById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/pesquisar/{nome}")
    public List<Turma> pesquisarTurmaPorNome(@PathVariable String nome) {
        return service.searchByName(nome);
    }

    @PostMapping()
    public void cadastrarTurma(@RequestBody ObjectNode objectNode) {
        service.insert(objectNode.get("nome").asText());
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void atualizarTurma(@RequestBody ObjectNode objectNode) {
        Turma t = new Turma();
        t.setNome(objectNode.get("nome").asText());
        t.setCodigo(objectNode.get("codigo").asLong());
        service.update(t);
    }

    @DeleteMapping("/{id}")
    public void deletarTurma(@PathVariable Long id) {
        service.deleteById(id);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}
