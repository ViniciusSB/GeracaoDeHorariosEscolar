package com.unitins.springneo4j.controller;

import com.unitins.springneo4j.model.Professor;
import com.unitins.springneo4j.service.ProfessorService;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/professores")
public class ProfessorController {

    @Autowired
    ProfessorService service;

    @GetMapping
    public List<Professor> getAll() {
        return service.getAll();
    }
}
