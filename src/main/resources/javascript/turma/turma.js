'use strict'

window.onload=function(){
    document.getElementById('cadastrarTurma').addEventListener('click', openModal);

    document.getElementById('modalClose').addEventListener('click', closeModal);

    document.getElementById('btnCancelar').addEventListener('click', closeModal);

    document.getElementById('btnSalvar').addEventListener('click', cadastrarTurma);

    document.getElementById('inputPesquisar').addEventListener('input', (e) => {
        if (e.currentTarget.value == "") {
            updateTabela();
        } else {
            pesquisarTurma(e.currentTarget.value);
        }
    })

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
            }
        })
    }).catch(function (err) {
        console.log(err);
    })

}

function addTurmasTabela(turmas) {
    var tabela = document.getElementById("tabela");

    for (let i = 0; i<turmas.length;i++) {
        let tr = document.createElement("tr");

        tr.innerHTML = `
            <td hidden>${turmas[i].id}</td>
            <td>${turmas[i].nome}</td> 
            <td>
                <button type="button" class="button green" onclick="javascript:editarRegistro(${turmas[i].codigo})">editar</button>
                <button type="button" class="button red" onclick="javascript:excluirRegistro(${turmas[i].codigo})">excluir</button>
            </td>   
        `;
        tabela.appendChild(tr);
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
            <th>Nome</th>
            <th>Ação</th>
        </tr>
    `;
    main.appendChild(table);
}

function deletarTabela() {
    var tabela = document.getElementById("tabela");
    tabela.remove();
}

async function cadastrarTurma() {
    let formModal = document.getElementById("formModal");
    if (formModal.reportValidity()) {
        let nome = document.getElementById("inputNome");
        let id = document.getElementById("inputId");
        let parametros = {
            "nome": nome.value
        }

        //incluir
        if (nome.value.length > 0 && id.value == "") {
            let request = new XMLHttpRequest();
            request.open("POST", "http://localhost:8080/turmas");
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            request.send(JSON.stringify(parametros));
            await delay(0.6);
        } else if (nome.value.length > 0 && id.value != "") { //Atualizar
            parametros.codigo = id.value;
            fetch(`http://localhost:8080/turmas`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json'},
                body: JSON.stringify(parametros)
            });
            await delay(0.6);
        }

        limparCampos();
        updateTabela();
        closeModal();
        console.log(id.value);
    }

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

function editarRegistro(codigo) {
    openModal();
    let url = `http://localhost:8080/turmas/${codigo}`;
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(function (response) {
        response.text().then(function (result){
            let turma = JSON.parse(result);
            console.log(turma);
            document.getElementById("inputNome").value = turma.nome;
            document.getElementById("inputId").value = turma.codigo;
        })
    }).catch(function (err) {
        console.log(err);
    })
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

function pesquisarTurma(campo) {
    let url = `http://localhost:8080/turmas/pesquisar/${campo}`
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(function (response) {
        response.text().then(function (result) {
            let dados = JSON.parse(result);
            console.log(dados);
            deletarTabela();
            criarTabela();
            addTurmasTabela(dados);
        })
    }).catch(function (err) {
        console.log(err);
    })
}