'use strict'

window.onload=async function(){
    document.getElementById('gerarGrade').addEventListener('click', funcGerarGradeDeHorarios);

    document.getElementById("excluirGrade").addEventListener("click", deletarGrade);

    document.getElementById('inputPesquisar').addEventListener('input', async (e) => {
        if (e.currentTarget.value == "") {
            ativar_loading();
            await updateTabela();
            retirar_loading();
        } else {
            pesquisarHorario(e.currentTarget.value);
        }
    })

    await updateTabela();
    retirar_loading();
}

function ativar_loading() {
    let loader = document.getElementById("loader");
    loader.className = "loader";
    console.log("loader chamado");
}

function retirar_loading() {
    let loader = document.getElementById("loader");
    loader.className += " hidden";
    console.log("loader retirado");
}

async function listarGradeHorarios() {
    let request = await fetch("http://localhost:8080/horarios/obterGradeDeHorarios", {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
    });
    let response = await request.text();
    let grade = JSON.parse(response);
    return grade;
}

async function deletarGrade() {
    if (window.confirm("Apagar toda a grade existente?")) {
        ativar_loading();
        await deletarGradeDeHorarios();
        deletarTabela();
        let main = document.getElementById("main");
        let h3 = document.createElement("h3");
        h3.textContent = "Nenhuma grade gerada";
        main.appendChild(h3);
        retirar_loading();
    }
}

async function listarGradeHorariosPorTurma(codigoTurma) {
    let request = await fetch(`http://localhost:8080/horarios/obterGradeDeHorariosPorTurma/${codigoTurma}`, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
    });
    let response = await request.text();
    return JSON.parse(response);
}

async function listarTurmas() {
    let request = await fetch("http://localhost:8080/turmas", {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
        // body: JSON.stringify(parametros)
    })
    let response = await request.text();
    return JSON.parse(response);
}

async function gerarGradeHorarios() {
    let url = "http://localhost:8080/horarios/gerarGrade";
    let request = await fetch(url, {
        mode: "cors",
        method: "POST"
    })
    let response = await request.text();
    return JSON.parse(response);
}

async function deletarGradeDeHorarios() {
    let url = "http://localhost:8080/horarios/todos";
    let request = await fetch(url, {
        method: "DELETE",
        mode: "cors"
    })
}

function deletarTabela() {
    let tabelas = document.querySelectorAll("#main > table");
    let h3s = document.querySelectorAll("#main > h3");
    tabelas.forEach(t => t.remove());
    h3s.forEach(h => h.remove())
}

async function funcGerarGradeDeHorarios() {
    ativar_loading();
    let gradeAtual = await listarGradeHorarios();
    if (gradeAtual.length > 0) {
        retirar_loading();
        alert("Já existe uma grade gerada, para gerar uma nova exclua a atual");
    } else {
        var resultados = await gerarGradeHorarios();
        let grade = resultados.gradeDeHorarios;
        let relatorio = resultados.relatorio;
        console.log(relatorio);
        let turmas = await listarTurmas();
        deletarTabela();
        await addGradeNaTabela(grade, turmas);
        let string = "";
        relatorio.forEach(rel => {
            string = string.concat(`${rel} \n`);
        })
        retirar_loading();
        alert(string);
    }

}

function criarTdGradeDeUmaTurma(horario) {
    let codigoTurma = horario.turmas[0].codigo;
    let nomeTurma = horario.turmas[0].nome;
    let nomeProfessor = horario.professores[0].nome;
    let nomeDisciplina = horario.disciplinas[0].nome;
    let nomeHorario = horario.descricao;
    let horInicio = horario.inicio;
    if (horInicio === 19) {
        let tds = document.querySelectorAll(`#${nomeTurma}Horario1 > td`);
        if (horario.descricao.toLowerCase().match("seg")) {
            let td = tds[0];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <label>${nomeDisciplina}</label>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("ter")) {
            let td = tds[1];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("qua")) {
            let td = tds[2];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("qui")) {
            let td = tds[3];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("sex")) {
            let td = tds[4];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
    }

    if (horInicio === 20) {
        let tds = document.querySelectorAll(`#${nomeTurma}Horario2 > td`);
        if (horario.descricao.toLowerCase().match("seg")) {
            let td = tds[0];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("ter")) {
            let td = tds[1];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("qua")) {
            let td = tds[2];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("qui")) {
            let td = tds[3];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("sex")) {
            let td = tds[4];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
    }

    if (horInicio === 21) {
        let tds = document.querySelectorAll(`#${nomeTurma}Horario3 > td`);
        if (horario.descricao.toLowerCase().match("seg")) {
            let td = tds[0];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("ter")) {
            let td = tds[1];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("qua")) {
            let td = tds[2];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("qui")) {
            let td = tds[3];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("sex")) {
            let td = tds[4];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
    }

    if (horInicio === 22) {
        let tds = document.querySelectorAll(`#${nomeTurma}Horario4 > td`);
        if (horario.descricao.toLowerCase().match("seg")) {
            let td = tds[0];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("ter")) {
            let td = tds[1];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("qua")) {
            let td = tds[2];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("qui")) {
            let td = tds[3];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
        if (horario.descricao.toLowerCase().match("sex")) {
            let td = tds[4];
            td.innerHTML = `
                <label>${nomeProfessor}</label>
                <br>
                <label>${nomeDisciplina}</label>
                <br>
                <label>${nomeHorario}</label>
            `;
        }
    }
}

async function addGradeNaTabela(grade, turmas) {
    let main = document.getElementById("main");

    for (let i = 0; i < turmas.length; i++) {
        let h3 = document.createElement("h3");
        h3.textContent = turmas[i].nome;
        let tabela = document.createElement("table");
        tabela.id = `tabelaTurma${turmas[i].codigo}`;
        tabela.className = "records";
        tabela.innerHTML = `
                <tr id="tituloTabela${turmas[i].codigo}">
                    <th>Segunda</th>
                    <th>Terça</th>
                    <th>Quarta</th>
                    <th>Quinta</th>
                    <th>Sexta</th>
                </tr>
                
                <tr id="${turmas[i].nome}Horario1">
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                
                <tr id="${turmas[i].nome}Horario2">
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                
                <tr id="${turmas[i].nome}Horario3">
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                
                <tr id="${turmas[i].nome}Horario4">
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
        `;
        main.appendChild(h3);
        main.appendChild(tabela);
    }

    for (let i = 0; i < turmas.length; i++) {

        let horarios = await listarGradeHorariosPorTurma(turmas[i].codigo);
        for (let i=0; i<horarios.length; i++) {
            criarTdGradeDeUmaTurma(horarios[i]);
        }
    }
}

async function updateTabela(){
    deletarTabela();
    let grade = await listarGradeHorarios();
    if (grade.length > 0) {
        let turmas = await listarTurmas();
        await addGradeNaTabela(grade, turmas);
    } else {
        let main = document.getElementById("main");
        let h3 = document.createElement("h3");
        h3.textContent = "Nenhuma grade gerada";
        main.appendChild(h3);
    }
}