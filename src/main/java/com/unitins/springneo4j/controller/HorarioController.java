package com.unitins.springneo4j.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Horario;
import com.unitins.springneo4j.service.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
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

    @GetMapping("/obterGradeDeHorarios")
    public List<Horario> obterGradeDeHorarios() {
        return service.obterGradeDeHorarios();
    }

    @GetMapping("/obterGradeDeHorariosPorTurma/{codigo}")
    public List<Horario> obterGradeDeHorariosPorTurma(@PathVariable Long codigo) {
        return service.obterGradeDeHorariosPorTurma(codigo);
    }

    @GetMapping("/{codigo}")
    public Horario pesquisarHorarioPorId(@PathVariable Long codigo) {
        return service.getById(codigo);
    }

    @GetMapping("/pesquisar")
    public List<Horario> pesquisarHorarioPorNome(@RequestParam String nome) {
        return service.searchByName(nome);
    }

    @PostMapping()
    public Horario cadastrarHorario(@RequestBody ObjectNode objectNode) {
        Horario h = new Horario();
        h.setDescricao(objectNode.get("descricao").asText());
        h.setInicio(objectNode.get("inicio").asInt());
        h.setFim(objectNode.get("fim").asInt());
        h.setOrdem(objectNode.get("ordem").asInt());
        return service.insert(h);
    }

    @PostMapping("/gerarGrade")
    public HashMap<String, Object> gerarGradeDeHorarios() {
        long inicio = System.currentTimeMillis();
        service.numTentativasGrade = 0;
        HashMap<String, Object> grade = service.gerarGradeDeHorarios();
        long tempoTotal = System.currentTimeMillis() - inicio;
        List<String> observacoes = (List<String>) grade.get("relatorio");
        if (tempoTotal > 60000) {
            long ms = tempoTotal;
            long segundos = ms / 1000;
            long minutos = segundos / 60;
            segundos = segundos % 60;
            minutos = minutos % 60;
            observacoes.add("Tempo total: " + minutos + ": "+segundos+" minuto(s)");
        } else {
            long ms = tempoTotal;
            long segundos = ms / 1000;
            segundos = segundos % 60;
            observacoes.add("Tempo total: " + segundos + " segundo(s)");
        }
        grade.remove("relatorio");
        grade.put("relatorio", observacoes);
        return grade;
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


    @DeleteMapping("/todos")
    public void deletarTodosOsRelGrade() {
        service.deletarTodosOsRelacionamentos();
    }

    @DeleteMapping()
    public void deletarRelGrade(@RequestBody ObjectNode objectNode) {
        service.deletarRelacionamentoHorario(objectNode);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}
