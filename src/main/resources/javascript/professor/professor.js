'use strict'

const openModal = () => document.getElementById('modal')
    .classList.add('active');

const closeModal = () => {
    limparCampos();
    document.getElementById('modal').classList.remove('active');
}

window.onload=async function () {
    document.getElementById('cadastrarProfessor').addEventListener('click', openModal);

    document.getElementById('modalClose').addEventListener('click', closeModal);

    document.getElementById('btnCancelar').addEventListener('click', closeModal);

    document.getElementById('btnSalvar').addEventListener('click', cadastrarProfessor);

    document.getElementById("inputPesquisar").addEventListener('input', async (e) => {
        if (e.currentTarget.value == "") {
            await updateTabela();
        } else {
            await pesquisarProfessor(e.currentTarget.value);
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

function limparCampos() {
    let nome = document.getElementById("inputNome");
    document.getElementById("inputId").value = "";
    nome.value = "";
}

async function carregarProfessores() {
    await fetch("http://localhost:8080/professores", {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
    }).then(async function (response) {
        await response.text().then(async function (result) {
            let professores = await JSON.parse(result);
            console.log(professores);
            if (professores.length > 0) {
                addProfessoresTabela(professores);
            }
        })
    }).catch(function (err) {
        console.log(err);
    })

}

function addProfessoresTabela(professores) {
    var tabela = document.getElementById("tabela");

    for (let i = 0; i<professores.length;i++) {
        let tr = document.createElement("tr");

        tr.innerHTML = `
            <td hidden>${professores[i].codigo}</td>
            <td>${professores[i].nome}</td> 
            <td>
                <button type="button" class="button green" onclick="javascript:editarRegistro(${professores[i].codigo})">editar</button>
                <button type="button" class="button red" onclick="javascript:excluirRegistro(${professores[i].codigo})">excluir</button>
            </td>   
        `;
        tabela.appendChild(tr);
    }
}

function addUmProfessorNaTabela(professor) {
    var tabela = document.getElementById("tabela");

    let tr = document.createElement("tr");

    tr.innerHTML = `
        <td hidden>${professor.codigo}</td>
        <td>${professor.nome}</td> 
        <td>
            <button type="button" class="button green" onclick="javascript:editarRegistro(${professor.codigo})">editar</button>
            <button type="button" class="button red" onclick="javascript:excluirRegistro(${professor.codigo})">excluir</button>
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
            <th>Ação</th>
        </tr>
    `;
    main.appendChild(table);
}

function deletarTabela() {
    var tabela = document.getElementById("tabela");
    tabela.remove();
}

async function cadastrarProfessor() {
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
            let request = await fetch(`http://localhost:8080/professores`, {
                method: 'POST',
                mode: "cors",
                body: nome.value
            });
            let response = await request.text();
            let professor = JSON.parse(response);
            addUmProfessorNaTabela(professor);
            closeModal();
            retirar_loading();
        } else if (nome.value.length > 0 && id.value !== "") { //Atualizar
           parametros.codigo = id.value;
           let request = await fetch(`http://localhost:8080/professores`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json'},
                body: JSON.stringify(parametros)
            });
            let response = await request.text();
            let professor = JSON.parse(response);
            //Atualizando o elemento na tabela
            let trs = document.querySelectorAll("#tabela > tr");
            trs.forEach(tr => {
                if (parseInt(tr.children[0].textContent) === parseInt(id.value)) {
                    tr.children[1].textContent = nome.value;
                }
            });
            closeModal();
            retirar_loading();
        }
    }

}

function delay(n){
    return new Promise(function(resolve){
        setTimeout(resolve,n*1000);
    });
}

async function updateTabela(){
    deletarTabela();
    criarTabela();
    await carregarProfessores();
}

async function editarRegistro(codigo) {
    ativar_loading();
    openModal();
    let url = `http://localhost:8080/professores/${codigo}`;
    await fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(async function (response) {
        await response.text().then(async function (result) {
            let professor = await JSON.parse(result);
            console.log(professor);
            document.getElementById("inputNome").value = professor.nome;
            document.getElementById("inputId").value = professor.codigo;
        })
    }).catch(function (err) {
        console.log(err);
    })
    retirar_loading();
}

async function excluirRegistro(codigo) {
    let resposta = window.confirm("Deseja realmente excluir esse registro?");
    if (resposta) {
        ativar_loading();
        let url = `http://localhost:8080/professores/${codigo}`;
        await fetch(url, {
            method: "DELETE",
            headers: {'Content-Type': 'application/json'},
        }).then(async function (response) {
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

async function pesquisarProfessor(campo) {
    let url = `http://localhost:8080/professores/pesquisar?nome=${campo}`;
    await fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(async function (response) {
        await response.text().then(async function (result) {
            let dados = await JSON.parse(result);
            console.log(dados);
            deletarTabela();
            criarTabela();
            addProfessoresTabela(dados);
        })
    }).catch(function (err) {
        console.log(err);
    })
}