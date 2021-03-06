'use strict'

window.onload=function(){
    carregarProfessores();
    retirar_loading();
    document.getElementById("selectProfessor").addEventListener('change', (e) => {
        if (e.currentTarget.value !== "0") {
            ativar_loading();
            updateTabela();
            carregarDisciplinaProfessor(e.currentTarget.value);
            retirar_loading();
        } else {
            limparCampos()
        }
    })

    document.getElementById('modalClose').addEventListener('click', closeModal);
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
    ativar_loading();
    document.getElementById('modal').classList.remove('active');
    removerElementosModal();
    retirar_loading();
}

function carregarProfessores() {
    fetch("http://localhost:8080/professores", {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
        // body: JSON.stringify(parametros)
    }).then(function (response) {
        response.text().then(function (result){
            let professores = JSON.parse(result);
            if (document.getElementById("selectProfessor").childNodes.length === 3) {
                addProfessoresSelect(professores);
            }
        })
    }).catch(function (err) {
        console.log(err);
    })
}

function addProfessoresSelect(professores) {
    let select = document.getElementById("selectProfessor");

    for (let i = 0; i<professores.length;i++) {
        let option = document.createElement("option");
        option.value = professores[i].codigo;
        option.label = professores[i].nome
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
            <th>A????o</th>
        </tr>
    `;
    main.appendChild(table);
}

function deletarTabela() {
    var tabela = document.getElementById("tabela");
    tabela.remove();
}

function carregarDisciplinaProfessor(idProfessor) {
    addBotaoAddDisciplina();
    addInfoProfessor();
    addDisciplinasTabela(idProfessor);
}

function addDisciplinasTabela(idProfessor) {
    fetch(`http://localhost:8080/professores/${idProfessor}/disciplinas`, {
        method: "GET",
        mode: "cors"
    }).then(function (response) {
        response.text().then(function (result){
            let disciplinas = JSON.parse(result);
            adicionarDisciplinasTabela(disciplinas);
        })
    }).catch(function (err) {
        console.log(err);
    })
}

function addInfoProfessor() {
    let label = document.createElement("label");
    let select = document.getElementById("selectProfessor");
    let indiceProfessor = select.selectedIndex;
    let professorSelecionado = select.options[indiceProfessor];
    let nomeProfessor = professorSelecionado.label;
    label.textContent = `Disciplinas associadas ao professor ${nomeProfessor}`;
    label.style.cssText = "text-align: center; line-height: 150%; font-size: 15px;";
    label.id = "infoProfessor";
    let tabela = document.getElementById("tabela");
    tabela.parentElement.insertBefore(label, tabela);
}

function deletarInfoProfessor() {
    let label = document.getElementById("infoProfessor");
    if (label !== null) {
        label.remove();
    }
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
        botao.addEventListener('click', abrirBtnAddDisciplina);
    }
}

function adicionarDisciplinasTabela(disciplinas) {
    var tabela = document.getElementById("tabela");
    for (let i=0; i<disciplinas.length; i++) {
        let tr = document.createElement("tr");
        tr.innerHTML = `
            <td hidden>${disciplinas[i].codigo}</td>
            <td>${disciplinas[i].nome}</td>
            <td><button type="button" class="button red" onclick="excluirDisciplina(${disciplinas[i].codigo})">remover</button></td>
        `;
        tabela.appendChild(tr);
    }
}

function adicionarUmaDisciplinaTabela(disciplina) {
    var tabela = document.getElementById("tabela");
    let tr = document.createElement("tr");
    tr.innerHTML = `
        <td hidden>${disciplina.id}</td>
        <td>${disciplina.nome}</td>
        <td><button type="button" class="button red" onclick="excluirDisciplina(${disciplina.codigo})">remover</button></td>
    `;
    tabela.appendChild(tr);
}

async function excluirDisciplina(codigo) {
    if (window.confirm("Deseja realmente excluir esse registro?") === true) {
        ativar_loading();
        await fetch(`http://localhost:8080/professores/disciplina/${codigo}`, {
            method: "DELETE",
            headers: {'Content-Type': 'application/json'},
        }).then(function () {
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

function limparCampos() {
    ativar_loading();
    removerBotaoAddDisciplina();
    updateTabela();
    retirar_loading();
}

function removerBotaoAddDisciplina() {
    document.getElementById("adicionarDisciplina").remove();
}

function updateTabela(){
    deletarTabela();
    deletarInfoProfessor();
    criarTabela();
    carregarProfessores();
}

async function addElementosNoModal() {
    let disciplinas = await listarDisciplinasSemRelacionamento();
    if (disciplinas.length === 0) {
        let form = document.getElementById("formModal");
        let label = document.createElement("label");
        label.id = "labelModel";
        label.innerText = "Nenhuma disciplina dispon??vel";
        form.appendChild(label);
    } else {
        addElementosNoLabelModal(disciplinas);
    }
}

function addElementosNoLabelModal(disciplinas) {
    let form = document.getElementById("formModal");
    let label = document.createElement("label");
    label.id = "labelModel";
    label.className = "input-select-custom";
    label.innerText = "Selecione uma disciplina";
    form.appendChild(label);
    let select = document.createElement("select");
    select.id = "selectDisciplina";
    form.appendChild(select);
    let optionNull = document.createElement("option");
    optionNull.value = "0";
    optionNull.label = "-----";
    select.appendChild(optionNull);
    for (let i=0; i<disciplinas.length; i++) {
        let option = document.createElement("option");
        option.value = disciplinas[i].codigo;
        option.label = disciplinas[i].nome;
        select.appendChild(option);
    }

    let botao = document.createElement("button");
    botao.type = "button";
    botao.className = "button blue mobile";
    botao.id = "addDisciplinaParaOProfessor";
    botao.textContent = "Adicionar";
    botao.addEventListener('click', btnAddDisciplinaParaOProfessor);
    form.appendChild(botao);
}

function removerElementosModal() {
    let form = document.getElementById("formModal");
    form.innerHTML = "";
}

function abrirBtnAddDisciplina() {
    ativar_loading();
    addElementosNoModal();
    openModal();
    retirar_loading();
}

async function btnAddDisciplinaParaOProfessor() {
    ativar_loading();
    let disciplina = document.getElementById("selectDisciplina");
    let codigoDisciplina = disciplina.value;
    if (codigoDisciplina === "0") {
        alert("Selecione uma disciplina dispon??vel");
    } else {
        let codigoProfessor = document.getElementById("selectProfessor");
        await adionarDisciplinaParaOProfessor(codigoDisciplina, codigoProfessor.value);
        let disciplina = await atualizarTabela(codigoDisciplina);
        adicionarUmaDisciplinaTabela(disciplina);
        removerElementosModal();
        addElementosNoModal();
    }
    retirar_loading();
}

async function listarDisciplinasSemRelacionamento() {
    let url = 'http://localhost:8080/disciplinas/semrelacionamentoprofessor';
    let request = await fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
        mode: "cors"
    });
    let response = await request.text();
    let disciplinas = await JSON.parse(response);
    return disciplinas;
}

async function adionarDisciplinaParaOProfessor(codigoDisciplina, codigoProfessor) {
    let url = `http://localhost:8080/disciplinas/disciplinaProfessor?codigoDisciplina=${codigoDisciplina}&codigoProfessor=${codigoProfessor}`;
    let request = await fetch(url, {
        method: "POST",
        headers: {'Content-Type': 'application/json'}
    });
    return request;
}

async function atualizarTabela(codigoDisciplina) {
    let url = `http://localhost:8080/disciplinas/${codigoDisciplina}`;
    let request = await fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'}
    });
    let response = await request.text();
    let disciplina = await JSON.parse(response);
    return disciplina;
}






