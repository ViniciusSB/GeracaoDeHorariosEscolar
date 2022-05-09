package com.unitins.springneo4j.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.ws.rs.GET;
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

    @PostMapping()
    public void cadastrarProfessor(@RequestBody ObjectNode objectNode) {
        service.insert(objectNode.get("nome").asText());
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void atualizarProfessor(@RequestBody ObjectNode objectNode) {
        Professor p = new Professor();
        p.setNome(objectNode.get("nome").asText());
        p.setCodigo(objectNode.get("codigo").asLong());
        service.update(p);
    }

    @GetMapping("/{id}")
    public Professor updateProfessor(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deletarProfessor(@PathVariable Long id) {
        service.deleteById(id);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}
