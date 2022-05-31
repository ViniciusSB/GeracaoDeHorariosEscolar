'use strict'

window.onload=async function(){
    document.getElementById('gerarGrade').addEventListener('click', gerarGradeDeHorarios);

    document.getElementById('modalClose').addEventListener('click', closeModal);

    document.getElementById('btnCancelar').addEventListener('click', closeModal);

    document.getElementById('btnSalvar').addEventListener('click', cadastrarHorario);

    document.getElementById('inputPesquisar').addEventListener('input', async (e) => {
        if (e.currentTarget.value == "") {
            await updateTabela();
        } else {
            pesquisarHorario(e.currentTarget.value);
        }
    })

    await updateTabela();
}

const openModal = () => document.getElementById('modal')
    .classList.add('active');

const closeModal = () => {
    limparCampos();
    document.getElementById('modal').classList.remove('active');
}

function limparCampos() {
    document.getElementById("inputDescricao").value = "";
    document.getElementById("inputInicio").value = "";
    document.getElementById("inputFim").value = "";
    document.getElementById("inputOrdem").value = "";
    document.getElementById("inputId").value = "";
}

function carregarHorarios() {
    fetch("http://localhost:8080/horarios", {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
        // body: JSON.stringify(parametros)
    }).then(function (response) {
        response.text().then(function (result){
            var horarios = JSON.parse(result);
            console.log(horarios);
            if (horarios.length > 0) {
                addHorariosTabela(horarios);
            }
        })
    }).catch(function (err) {
        console.log(err);
    })

}

async function carregarDadosGrade() {
    let url = "http://localhost:8080/horarios/gerarGrade";
    let request = await fetch(url, {
        mode: "cors",
        method: "POST"
    })
    let response = await request.text();
    let grade = JSON.parse(response);
    return grade;
}

function addHorariosTabela(horarios) {
    var tabela = document.getElementById("tabela");

    for (let i = 0; i<horarios.length;i++) {
        let tr = document.createElement("tr");

        tr.innerHTML = `
            <td hidden>${horarios[i].id}</td>
            <td>${horarios[i].descricao}</td>
            <td>${horarios[i].inicio}</td> 
            <td>${horarios[i].fim}</td> 
            <td>${horarios[i].ordem}</td>
            <td>
                <button type="button" class="button green" onclick="javascript:editarRegistro(${horarios[i].codigo})">editar</button>
                <button type="button" class="button red" onclick="javascript:excluirRegistro(${horarios[i].codigo})">excluir</button>
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
            <th>Horario</th>
            <th>Professor</th>
            <th>Disciplina</th>
            <th>Turma</th>
            <th>Ação</th>
        </tr>
    `;
    main.appendChild(table);
}

function deletarTabela() {
    var tabela = document.getElementById("tabela");
    tabela.remove();
}

async function gerarGradeDeHorarios() {
    let grade = await carregarDadosGrade();
    deletarTabela();
    criarTabela();
    addGradeNaTabela(grade);
}

function addGradeNaTabela(grade) {
    var tabela = document.getElementById("tabela");

    for (let i = 0; i<grade.length;i++) {
        let tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${grade[i].descricao}</td>
            <td>${grade[i].professores[0].nome}</td> 
            <td>${grade[i].disciplinas[0].nome}</td> 
            <td>${grade[i].turmas[0].nome}</td>
            <td>
                <button type="button" class="button green" onclick="javascript:editarRegistro(${horarios[i].codigo})">editar</button>
                <button type="button" class="button red" onclick="javascript:excluirRegistro(${horarios[i].codigo})">excluir</button>
            </td>   
        `;
        tabela.appendChild(tr);
    }
}

async function cadastrarHorario() {
    let formModal = document.getElementById("formModal");
    if (formModal.reportValidity()) {
        let descricao = document.getElementById("inputDescricao");
        let inicio = document.getElementById("inputInicio");
        let fim = document.getElementById("inputFim");
        let ordem = document.getElementById("inputOrdem");
        let id = document.getElementById("inputId");
        let parametros = {
            "descricao": descricao.value,
            "inicio": inicio.value,
            "fim": fim.value,
            "ordem": ordem.value
        }

        //incluir
        if (id.value == "") {
            let request = new XMLHttpRequest();
            request.open("POST", "http://localhost:8080/horarios");
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            request.send(JSON.stringify(parametros));
            await delay(0.6);
        } else if (id.value != "") { //Atualizar (o input id armazena o codigo do objeto)
            parametros.codigo = id.value;
            fetch(`http://localhost:8080/horarios`, {
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

async function updateTabela(){
    deletarTabela();
    criarTabela();
    let grade = await carregarDadosGrade();
    addGradeNaTabela(grade);
}

function editarRegistro(codigo) {
    openModal();
    let url = `http://localhost:8080/horarios/${codigo}`;
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(function (response) {
        response.text().then(function (result){
            let horario = JSON.parse(result);
            console.log(horario);
            document.getElementById("inputDescricao").value = horario.descricao;
            document.getElementById("inputInicio").value = horario.inicio;
            document.getElementById("inputFim").value = horario.fim;
            document.getElementById("inputOrdem").value = horario.ordem;
            document.getElementById("inputId").value = horario.codigo;
        })
    }).catch(function (err) {
        console.log(err);
    })
}

function excluirRegistro(codigo) {
    var resposta = window.confirm("Deseja realmente excluir esse registro?");
    if (resposta) {
        let url = `http://localhost:8080/horarios/${codigo}`;
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

function pesquisarHorario(campo) {
    let url = `http://localhost:8080/horarios/pesquisar?nome=${campo}`;
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    }).then(function (response) {
        response.text().then(function (result) {
            let dados = JSON.parse(result);
            console.log(dados);
            deletarTabela();
            criarTabela();
            addHorariosTabela(dados);
        })
    }).catch(function (err) {
        console.log(err);
    })
}