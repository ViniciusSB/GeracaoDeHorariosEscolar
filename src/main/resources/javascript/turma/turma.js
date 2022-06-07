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
            <td hidden>${turmas[i].codigo}</td>
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
        ativar_loading();
        let nome = document.getElementById("inputNome");
        let id = document.getElementById("inputId");
        let parametros = {
            "nome": nome.value
        }

        //incluir
        if (nome.value.length > 0 && id.value === "") {
            let request = await fetch("http://localhost:8080/turmas", {
                method: "POST",
                mode: "cors",
                body: nome.value
            })
            let response = await request.text();
            let turma = JSON.parse(response);
            addUmaTurmaNaTabela(turma);
            closeModal();
            retirar_loading();
        } else if (nome.value.length > 0 && id.value != "") { //Atualizar
            parametros.codigo = id.value;
            let request = await fetch(`http://localhost:8080/turmas`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                mode: "cors",
                body: JSON.stringify(parametros)
            });
            let response = await request.text();
            let professor = JSON.parse(response);
            //Atualizando o elemento na tabela
            let trs = document.querySelectorAll("#tabela > tr");
            trs.forEach(tr => {
                if (parseInt(tr.children[0].textContent) === parseInt(id.value)) {
                    tr.children[1].textContent = professor.nome;
                }
            });
            closeModal();
            retirar_loading();
        }

    }

}

function addUmaTurmaNaTabela(turma) {
    var tabela = document.getElementById("tabela");

    let tr = document.createElement("tr");

    tr.innerHTML = `
        <td hidden>${turma.codigo}</td>
        <td>${turma.nome}</td> 
        <td>
            <button type="button" class="button green" onclick="javascript:editarRegistro(${turma.codigo})">editar</button>
            <button type="button" class="button red" onclick="javascript:excluirRegistro(${turma.codigo})">excluir</button>
        </td>   
    `;
    tabela.appendChild(tr);
}

function updateTabela(){
    deletarTabela();
    criarTabela();
    carregarTurmas();
}

function editarRegistro(codigo) {
    ativar_loading();
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
    retirar_loading();
}

async function excluirRegistro(codigo) {
    ativar_loading();
    var resposta = window.confirm("Deseja realmente excluir esse registro?");
    if (resposta) {
        let url = `http://localhost:8080/turmas/${codigo}`;
        let request = await fetch(url, {
            method: "DELETE",
            headers: {'Content-Type': 'application/json'},
        }).then(function (response) {
            let trs = document.querySelectorAll("#tabela > tr");
            trs.forEach(tr => {
                if (parseInt(tr.children[0].textContent) === codigo) {
                    tr.remove();
                }
            })
        }).catch(function (err) {
            console.log(err);
        })
    }
    retirar_loading();
}

function pesquisarTurma(campo) {
    let url = `http://localhost:8080/turmas/pesquisar?nome=${campo}`
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