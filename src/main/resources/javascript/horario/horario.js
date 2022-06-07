'use strict'

window.onload=function(){
    document.getElementById('cadastrarHorario').addEventListener('click', openModal);

    document.getElementById('modalClose').addEventListener('click', closeModal);

    document.getElementById('btnCancelar').addEventListener('click', closeModal);

    document.getElementById('btnSalvar').addEventListener('click', cadastrarHorario);

    document.getElementById('inputPesquisar').addEventListener('input', (e) => {
        if (e.currentTarget.value == "") {
            ativar_loading();
            updateTabela();
            retirar_loading();
        } else {
            pesquisarHorario(e.currentTarget.value);
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

function addHorariosTabela(horarios) {
    var tabela = document.getElementById("tabela");

    for (let i = 0; i<horarios.length;i++) {
        let tr = document.createElement("tr");

        tr.innerHTML = `
            <td hidden>${horarios[i].codigo}</td>
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

function addUmHorarioNaTabela(horario) {
    var tabela = document.getElementById("tabela");

    let tr = document.createElement("tr");

    tr.innerHTML = `
            <td hidden>${horario.codigo}</td>
            <td>${horario.descricao}</td>
            <td>${horario.inicio}</td> 
            <td>${horario.fim}</td> 
            <td>${horario.ordem}</td>
            <td>
                <button type="button" class="button green" onclick="javascript:editarRegistro(${horario.codigo})">editar</button>
                <button type="button" class="button red" onclick="javascript:excluirRegistro(${horario.codigo})">excluir</button>
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
            <th>Descrição</th>
            <th>Inicio</th>
            <th>Fim</th>
            <th>Ordem</th>
            <th>Ação</th>
        </tr>
    `;
    main.appendChild(table);
}

function deletarTabela() {
    var tabela = document.getElementById("tabela");
    tabela.remove();
}

async function cadastrarHorario() {
    let formModal = document.getElementById("formModal");
    if (formModal.reportValidity()) {
        ativar_loading();
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
            let request = await fetch("http://localhost:8080/horarios", {
                mode: "cors",
                method: "POST",
                headers: { 'Content-Type': 'application/json'},
                body: JSON.stringify(parametros)
            }).then(function (response) {
                response.text().then(function (text) {
                    let horario = JSON.parse(text);
                    console.log(horario);
                    addUmHorarioNaTabela(horario);
                    limparCampos();
                    closeModal();
                    retirar_loading();
                })
            })
        } else if (id.value != "") { //Atualizar (o input id armazena o codigo do objeto)
            parametros.codigo = id.value;
            let request = await fetch(`http://localhost:8080/horarios`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json'},
                body: JSON.stringify(parametros)
            }).then(function () {
                let trs = document.querySelectorAll("#tabela > tr");
                trs.forEach(tr => {
                    if (parseInt(tr.children[0].textContent) === parseInt(id.value)) {
                        tr.children[1].textContent = descricao.value;
                        tr.children[2].textContent = inicio.value;
                        tr.children[3].textContent = fim.value;
                        tr.children[4].textContent = ordem.value;
                    }
                })
                limparCampos();
                closeModal();
                retirar_loading();
            });
        }
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
    carregarHorarios();
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
        ativar_loading();
        let url = `http://localhost:8080/horarios/${codigo}`;
        fetch(url, {
            method: "DELETE",
            headers: {'Content-Type': 'application/json'},
        }).then(function (response) {
            let trs = document.querySelectorAll("#tabela > tr");
            trs.forEach(tr => {
                if (parseInt(tr.children[0].textContent) === codigo) {
                    tr.remove();
                }
            })
            retirar_loading();
        }).catch(function (err) {
            console.log(err);
            retirar_loading();
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