'use strict'

window.onload=function(){
    document.getElementById('cadastrarDisciplina').addEventListener('click', openModal);

    document.getElementById('modalClose').addEventListener('click', closeModal);

    document.getElementById('btnCancelar').addEventListener('click', closeModal);

    document.getElementById('btnSalvar').addEventListener('click', cadastrarDisciplina);

    document.getElementById('inputPesquisar').addEventListener('input', (e) => {
        if (e.currentTarget.value == "") {
            updateTabela();
        } else {
            pesquisarDisciplina(e.currentTarget.value);
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
    document.getElementById("inputNome").value = "";
    document.getElementById("inputId").value = "";
    document.getElementById("inputCargaHoraria").value = "";
    document.getElementById("inputAulaSemanal").value = "";
}

function carregarDisciplinas() {
    fetch("http://localhost:8080/disciplinas", {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
        // body: JSON.stringify(parametros)
    }).then(function (response) {
        response.text().then(function (result){
            var disciplinas = JSON.parse(result);
            console.log(disciplinas);
            if (disciplinas.length > 0) {
                addDisciplinasTabela(disciplinas);
            }
        })
    }).catch(function (err) {
        console.log(err);
    })

}

function addDisciplinasTabela(disciplinas) {
    var tabela = document.getElementById("tabela");

    for (let i = 0; i<disciplinas.length;i++) {
        let tr = document.createElement("tr");

        tr.innerHTML = `
            <td hidden>${disciplinas[i].id}</td>
            <td>${disciplinas[i].nome}</td>
            <td>${disciplinas[i].aulaSemanal}</td> 
            <td>${disciplinas[i].cargaHoraria}</td> 
            <td>
                <button type="button" class="button green" onclick="javascript:editarRegistro(${disciplinas[i].codigo})">editar</button>
                <button type="button" class="button red" onclick="javascript:excluirRegistro(${disciplinas[i].codigo})">excluir</button>
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
            <th>Aulas por semanal</th>
            <th>Carga horaria</th>
            <th>Ação</th>
        </tr>
    `;
    main.appendChild(table);
}

function deletarTabela() {
    var tabela = document.getElementById("tabela");
    tabela.remove();
}

async function cadastrarDisciplina() {
    let formModal = document.getElementById("formModal");
    if (formModal.reportValidity()) {
        let nome = document.getElementById("inputNome");
        let ch = document.getElementById("inputCargaHoraria");
        let aulasemanal = document.getElementById("inputAulaSemanal");
        let id = document.getElementById("inputId");
        let parametros = {
            "nome": nome.value,
            "ch": ch.value,
            "aulasemanal": aulasemanal.value
        }

        //incluir
        if (nome.value.length > 0 && id.value == "") {
            let request = new XMLHttpRequest();
            request.open("POST", "http://localhost:8080/disciplinas");
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            request.send(JSON.stringify(parametros));
            await delay(0.6);
        } else if (nome.value.length > 0 && id.value != "") { //Atualizar (o input id armazena o codigo do objeto)
            parametros.codigo = id.value;
            fetch(`http://localhost:8080/disciplinas`, {
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
    carregarDisciplinas();
}

function editarRegistro(codigo) {
    openModal();
    let url = `http://localhost:8080/disciplinas/${codigo}`;
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(function (response) {
        response.text().then(function (result){
            let disciplina = JSON.parse(result);
            console.log(disciplina);
            document.getElementById("inputNome").value = disciplina.nome;
            document.getElementById("inputAulaSemanal").value = disciplina.aulaSemanal;
            document.getElementById("inputCargaHoraria").value = disciplina.cargaHoraria;
            document.getElementById("inputId").value = disciplina.codigo;
        })
    }).catch(function (err) {
        console.log(err);
    })
}

function excluirRegistro(codigo) {
    var resposta = window.confirm("Deseja realmente excluir esse registro?");
    if (resposta) {
        let url = `http://localhost:8080/disciplinas/${codigo}`;
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

function pesquisarDisciplina(campo) {
    let url = `http://localhost:8080/disciplinas/pesquisar?nome=${campo}`;
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(function (response) {
        response.text().then(function (result) {
            let dados = JSON.parse(result);
            console.log(dados);
            deletarTabela();
            criarTabela();
            addDisciplinasTabela(dados);
        })
    }).catch(function (err) {
        console.log(err);
    })
}