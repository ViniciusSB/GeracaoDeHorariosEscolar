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
            <td hidden>${disciplinas[i].codigo}</td>
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

function addUmaDisciplinaNaTabela(disciplina) {
    let tabela = document.getElementById("tabela");

    let tr = document.createElement("tr");

    tr.innerHTML = `
            <td hidden>${disciplina.codigo}</td>
            <td>${disciplina.nome}</td>
            <td>${disciplina.aulaSemanal}</td> 
            <td>${disciplina.cargaHoraria}</td> 
            <td>
                <button type="button" class="button green" onclick="javascript:editarRegistro(${disciplina.codigo})">editar</button>
                <button type="button" class="button red" onclick="javascript:excluirRegistro(${disciplina.codigo})">excluir</button>
            </td>   
        `;
    tabela.appendChild(tr);
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
    ativar_loading();
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
        if (nome.value.length > 0 && id.value === "") {
            let request = await fetch("http://localhost:8080/disciplinas", {
                method: "POST",
                mode: "cors",
                headers: { 'Content-Type': 'application/json'},
                body: JSON.stringify(parametros)
            }).then(async function (reponse) {
                await reponse.text().then(function (text) {
                    let disciplina = JSON.parse(text);
                    addUmaDisciplinaNaTabela(disciplina);
                    closeModal();
                    retirar_loading();
                })
            })
        } else if (nome.value.length > 0 && id.value != "") { //Atualizar (o input id armazena o codigo do objeto)
            parametros.codigo = id.value;
            let request = await fetch(`http://localhost:8080/disciplinas`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json'},
                body: JSON.stringify(parametros)
            }).then(function () {
                let trs = document.querySelectorAll("#tabela > tr");
                let inputs = document.querySelectorAll("#formModal > input");
                trs.forEach(tr => {
                    if (parseInt(tr.children[0].textContent) === parseInt(inputs[0].value)) {
                        tr.children[1].textContent = nome.value;
                        tr.children[2].textContent = ch.value;
                        tr.children[3].textContent = aulasemanal.value;
                    }
                })
                closeModal();
                retirar_loading();
            });
        }

    }

}

function updateTabela(){
    deletarTabela();
    criarTabela();
    carregarDisciplinas();
}

function editarRegistro(codigo) {
    ativar_loading();
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
    retirar_loading();
}

async function excluirRegistro(codigo) {
    var resposta = window.confirm("Deseja realmente excluir esse registro?");
    if (resposta) {
        ativar_loading();
        let url = `http://localhost:8080/disciplinas/${codigo}`;
        await fetch(url, {
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
        retirar_loading();
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