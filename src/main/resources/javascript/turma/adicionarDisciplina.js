'use strict'

window.onload=function(){

    updateTabela();
}

const openModal = () => document.getElementById('modal')
    .classList.add('active');

const closeModal = () => {
    limparCampos();
    document.getElementById('modal').classList.remove('active');
}

function limparCampos() {
    let nome = document.getElementById("inputNome");
    document.getElementById("inputId").value = "";
    nome.value = "";
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
            if (turmas.length > 0) {
                addTurmasTabela(turmas);
                if (document.getElementById("selectTurma").childNodes.length === 1) {
                    addTurmasSelect(turmas);
                }
            }
        })
    }).catch(function (err) {
        console.log(err);
    })

}

function addTurmasTabela(turmas) {
    let tabela = document.getElementById("tabela");

    for (let i = 0; i<turmas.length;i++) {
        let tr = document.createElement("tr");

        tr.innerHTML = `
            <td hidden>${turmas[i].id}</td>
            <td>${turmas[i].nome}</td> 
            <td>
                <button type="button" class="button red" onclick="javascript:excluirRegistro(${turmas[i].codigo})">excluir</button>
            </td>   
        `;
        tabela.appendChild(tr);
    }
}

function addTurmasSelect(turmas) {
    let select = document.getElementById("selectTurma");
    let optionVazio = document.createElement("option");
    optionVazio.value = "0";
    optionVazio.label = "-----";
    select.appendChild(optionVazio);

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

function updateTabela(){
    deletarTabela();
    criarTabela();
    carregarTurmas();
}

function excluirRegistro(codigo) {
    var resposta = window.confirm("Deseja realmente excluir esse registro?");
    if (resposta) {
        let url = `http://localhost:8080/turmas/${codigo}`;
        fetch(url, {
            method: "DELETE",
            headers: {'Content-Type': 'application/json'},
        }).then(function (response) {
            updateTabela();
        }).catch(function (err) {
            console.log(err);
        })
    }
}


