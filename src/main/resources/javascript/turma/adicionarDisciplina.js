'use strict'

window.onload=function(){

    carregarTurmas();

    document.getElementById("selectTurma").addEventListener('change', (e) => {
        console.log(e.currentTarget.value);
        if (e.currentTarget.value !== "0") {
            updateTabela();
            carregarDisciplinaTurma(e.currentTarget.value);
        } else {
            limparCampos()
        }
    })

    document.getElementById('modalClose').addEventListener('click', closeModal);
}

const openModal = () => document.getElementById('modal')
    .classList.add('active');

const closeModal = () => {
    document.getElementById('modal').classList.remove('active');
    removerElementosModal();
}


function carregarTurmas() {
    fetch("http://localhost:8080/turmas", {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
        // body: JSON.stringify(parametros)
    }).then(function (response) {
        response.text().then(function (result){
            var turmas = JSON.parse(result);
            console.log(turmas);
            if (document.getElementById("selectTurma").childNodes.length === 3) {
                addTurmasSelect(turmas);
            }
        })
    }).catch(function (err) {
        console.log(err);
    })

}

function addTurmasSelect(turmas) {
    let select = document.getElementById("selectTurma");

    for (let i = 0; i<turmas.length;i++) {
        let option = document.createElement("option");
        option.value = turmas[i].codigo;
        option.label = turmas[i].nome
        select.appendChild(option);
    }
}

function criarTabela() {
    var main = document.getElementById("main");
    var table = document.createElement("table");
    table.className = "records";
    table.id = "tabela";
    table.innerHTML = `
        <tr>
            <th hidden>Id</th>
            <th>Nome da disciplina</th>
            <th>Ação</th>
        </tr>
    `;
    main.appendChild(table);
}

function deletarTabela() {
    var tabela = document.getElementById("tabela");
    tabela.remove();
}

function delay(n){
    return new Promise(function(resolve){
        setTimeout(resolve,n*1000);
    });
}

function carregarDisciplinaTurma(idTurma) {
    addBotaoAddDisciplina();
    addDisciplinasTabela(idTurma);
}

function addDisciplinasTabela(idTurma) {
    fetch(`http://localhost:8080/turmas/${idTurma}/disciplinas`, {
        method: "GET",
        mode: "cors"
    }).then(function (response) {
        response.text().then(function (result){
            let disciplinas = JSON.parse(result);
            console.log(disciplinas);
            adicionarDisciplinasTabela(disciplinas);
        })
    }).catch(function (err) {
        console.log(err);
    })
}

function addBotaoAddDisciplina() {
    if (document.getElementById("adicionarDisciplina") == null) {
        let tabela = document.getElementById("tabela");
        let botao = document.createElement("button");
        botao.type = "button";
        botao.className = "button blue mobile";
        botao.id = "adicionarDisciplina";
        botao.textContent = "Adicionar disciplina";
        tabela.parentElement.insertBefore(botao, tabela);
        botao.addEventListener('click', abrirBtnAddDisciplina);
    }
}

function adicionarDisciplinasTabela(disciplinas) {
    var tabela = document.getElementById("tabela");
    for (let i=0; i<disciplinas.length; i++) {
        let tr = document.createElement("tr");
        tr.innerHTML = `
            <td hidden>${disciplinas[i].id}</td>
            <td>${disciplinas[i].nome}</td>
            <td><button type="button" class="button red" onclick="excluirDisciplina(${disciplinas[i].codigo})">remover</button></td>
        `;
        tabela.appendChild(tr);
    }
}

async function excluirDisciplina(codigo) {
    if (window.confirm("Deseja realmente excluir esse registro?") === true) {
        fetch(`http://localhost:8080/turmas/disciplina/${codigo}`, {
            method: "DELETE",
            headers: {'Content-Type': 'application/json'},
        }).then((response) => {
            console.log(response.text());
        }).catch(function (err) {
            console.log(err);
        })
        await delay(0.6);
        updateTabela()
        addDisciplinasTabela(document.getElementById("selectTurma").value);
    }
}

function limparCampos() {
    removerBotaoAddDisciplina();
    updateTabela();
}

function removerBotaoAddDisciplina() {
    document.getElementById("adicionarDisciplina").remove();
}

function updateTabela(){
    deletarTabela();
    criarTabela();
    carregarTurmas();
}

function addElementosNoModal() {
    let form = document.getElementById("formModal");
    let label = document.createElement("label");
    label.id = "labelModel";

    let url = 'http://localhost:8080/disciplinas/semrelacionamento';
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
    }).then(function (response) {
        response.text().then(function (result){
            var disciplinas = JSON.parse(result);
            console.log(disciplinas);
        })
    }).catch(function (err) {
        console.log(err);
    })

    let disciplinas = 0;

    if (0 === 0) {
        label.innerText = "Nenhuma disciplina disponível";
        form.appendChild(label);
    } else {
        label.className = "input-select-custom";
        label.innerText = "Selecione uma disciplina";
        form.appendChild(label);
        let select = document.createElement("select");
        select.id = "selectDisciplina";
        label.appendChild(select);
        let optionNull = document.createElement("option");
        optionNull.value = "0";
        optionNull.label = "-----";
        select.appendChild(optionNull);
        for (let i=0; i<disciplinas.length; i++) {
            let option = document.createElement("option");
            option.value = disciplinas[i].codigo;
            option.label = disciplinas[i].nome;
            select.appendChild(option);
        }

        let botao = document.createElement("button");
        botao.type = "button";
        botao.className = "button blue mobile";
        botao.id = "addDisciplinaNaTurma";
        botao.textContent = "Adicionar disciplina";
        botao.addEventListener('click', btnAddDisciplinaNaTurma);
    }
}

function removerElementosModal() {
    let label = document.getElementById("labelModel");
    label.remove();
}

function abrirBtnAddDisciplina() {
    addElementosNoModal();
    openModal();
}

function btnAddDisciplinaNaTurma() {
    let disciplina = document.getElementById("selectDisciplina");
    if (disciplina.value === "0") {
        alert("Selecione uma disciplina disponível");
    } else {

    }
}

function listarDisciplinasSemRelacionamento() {
    let url = 'http://localhost:8080/disciplinas/semrelacionamento';
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
    }).then(function (response) {
        response.text().then(function (result){
            var disciplinas = JSON.parse(result);
            console.log(disciplinas);
        })
    }).catch(function (err) {
        console.log(err);
    })
}

function adionarDisciplinaNaTurma(idDisciplina, idTurma) {
    let url = 'http://localhost:8080/disciplinas/disciplinaTurma';
    let parametros = {
        "codigoDisciplina": idDisciplina,
        "codigoTurma": idTurma
    }
    fetch(url, {
        method: "POST",
        body: JSON.stringify(parametros),
        headers: {'Content-Type': 'application/json'}
    }).then(function (response) {
        response.text().then(function (result) {
            console.log(JSON.parse(result));
        })
    }).catch(function (err) {
        console.log(err);
    })
}






