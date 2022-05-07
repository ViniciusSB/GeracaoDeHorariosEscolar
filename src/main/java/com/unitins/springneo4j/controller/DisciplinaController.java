package com.unitins.springneo4j.controller;

import com.unitins.springneo4j.model.Disciplina;
import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.service.DisciplinaService;
import com.unitins.springneo4j.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {

    @Autowired
    DisciplinaService service;

    @GetMapping
    public List<Disciplina> getAll() {
        return service.getAll();
    }
}
