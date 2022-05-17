'use strict'

window.onload=function(){

    carregarTurmas();

    document.getElementById("selectTurma").addEventListener('change', (e) => {
        if (e.currentTarget.value !== "0") {
            updateTabela();
            carregarDisciplinaTurma(e.currentTarget.value);
        } else {
            limparCampos()
        }
    })
}

const openModal = () => document.getElementById('modal')
    .classList.add('active');

const closeModal = () => {
    document.getElementById('modal').classList.remove('active');
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




