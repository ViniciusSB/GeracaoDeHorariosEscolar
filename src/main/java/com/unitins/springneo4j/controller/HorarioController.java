package com.unitins.springneo4j.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitins.springneo4j.model.Horario;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.model.Turma;
import com.unitins.springneo4j.service.HorarioService;
import com.unitins.springneo4j.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RestController
@RequestMapping("/horarios")
public class HorarioController implements WebMvcConfigurer {

    @Autowired
    HorarioService service;

    @GetMapping
    public List<Horario> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Horario pesquisarHorarioById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/pesquisar/{nome}")
    public List<Horario> pesquisarHorarioPorNome(@PathVariable String nome) {
        return service.searchByName(nome);
    }

    @PostMapping()
    public void cadastrarHorario(@RequestBody ObjectNode objectNode) {
        Horario h = new Horario();
        h.setDescricao(objectNode.get("descricao").asText());
        h.setInicio(objectNode.get("inicio").asInt());
        h.setFim(objectNode.get("fim").asInt());
        h.setOrdem(objectNode.get("ordem").asInt());
        service.insert(h);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void atualizarHorario(@RequestBody ObjectNode objectNode) {
        Horario h = new Horario();
        h.setDescricao(objectNode.get("descricao").asText());
        h.setInicio(objectNode.get("inicio").asInt());
        h.setFim(objectNode.get("fim").asInt());
        h.setOrdem(objectNode.get("ordem").asInt());
        h.setCodigo(objectNode.get("codigo").asLong());
        service.update(h);
    }

    @DeleteMapping("/{id}")
    public void deletarHorario(@PathVariable Long id) {
        service.deleteById(id);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}
