'use strict'

const openModal = () => document.getElementById('modal')
    .classList.add('active');

const closeModal = () => {
    limparCampos();
    document.getElementById('modal').classList.remove('active');
}

window.onload=function(){
    document.getElementById('cadastrarProfessor').addEventListener('click', openModal);

    document.getElementById('modalClose').addEventListener('click', closeModal);

    document.getElementById('btnCancelar').addEventListener('click', closeModal);

    document.getElementById('btnSalvar').addEventListener('click', cadastrarProfessor);

    document.getElementById("inputPesquisar").addEventListener('input', (e) => {
        if (e.currentTarget.value == "") {
            updateTabela();
        } else {
            pesquisarProfessor(e.currentTarget.value);
        }
    })

    updateTabela();
}



function limparCampos() {
    let nome = document.getElementById("inputNome");
    document.getElementById("inputId").value = "";
    nome.value = "";
}

function carregarProfessores() {
    fetch("http://localhost:8080/professores", {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
        // body: JSON.stringify(parametros)
    }).then(function (response) {
        response.text().then(function (result){
            var professores = JSON.parse(result);
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
            <td hidden>${professores[i].id}</td>
            <td>${professores[i].nome}</td> 
            <td>
                <button type="button" class="button green" onclick="javascript:editarRegistro(${professores[i].codigo})">editar</button>
                <button type="button" class="button red" onclick="javascript:excluirRegistro(${professores[i].codigo})">excluir</button>
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

async function cadastrarProfessor() {
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
            request.open("POST", "http://localhost:8080/professores");
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            request.send(JSON.stringify(parametros));
            await delay(0.6);
        } else if (nome.value.length > 0 && id.value != "") { //Atualizar
            parametros.codigo = id.value;
            fetch(`http://localhost:8080/professores`, {
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
    carregarProfessores();
}

function editarRegistro(codigo) {
    openModal();
    let url = `http://localhost:8080/professores/${codigo}`;
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(function (response) {
        response.text().then(function (result){
            let professor = JSON.parse(result);
            console.log(professor);
            document.getElementById("inputNome").value = professor.nome;
            document.getElementById("inputId").value = professor.codigo;
        })
    }).catch(function (err) {
        console.log(err);
    })
}

function excluirRegistro(codigo) {
    var resposta = window.confirm("Deseja realmente excluir esse registro?");
    if (resposta) {
        let url = `http://localhost:8080/professores/${codigo}`;
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

function pesquisarProfessor(campo) {
    let url = `http://localhost:8080/professores/pesquisar?nome=${campo}`;
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(function (response) {
        response.text().then(function (result) {
            let dados = JSON.parse(result);
            console.log(dados);
            deletarTabela();
            criarTabela();
            addProfessoresTabela(dados);
        })
    }).catch(function (err) {
        console.log(err);
    })
}