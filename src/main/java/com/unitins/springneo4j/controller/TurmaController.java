package com.unitins.springneo4j.controller;

import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.model.Turma;
import com.unitins.springneo4j.service.ProfessorService;
import com.unitins.springneo4j.service.TurmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    @Autowired
    TurmaService service;

    @GetMapping
    public List<Turma> getAll() {
        return service.getAll();
    }
}
