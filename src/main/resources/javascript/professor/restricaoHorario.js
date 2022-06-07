window.onload = async function () {
    await montarProfessoresSelect();

    document.getElementById("selectProfessor").addEventListener("change", async function (evento) {
        if (evento.currentTarget.value !== "0") {
            await updateTabela();
        } else {
            limparCampos();
        }
    })

    document.getElementById('modalClose').addEventListener('click', closeModal);
    document.getElementById('btnCancelar').addEventListener('click', closeModal);
    document.getElementById("btnSalvar").addEventListener('click', salvarHorarioProfessor);

    retirar_loading();
}

let restricoesProfessor;

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

const closeModal = async () => {
    ativar_loading();
    document.getElementById('modal').classList.remove('active');
    removerElementosModal();
    deletarTabela();
    criarTabela();
    let horarios = await carregarHorariosRestricaoProfessor(document.getElementById("selectProfessor").value);
    montarHorariosNaTabela(horarios);
    retirar_loading();
}

async function carregarHorariosRestricaoProfessor(codigoProfessor) {
    let url = `http://localhost:8080/professores/${codigoProfessor}/horarios`;
    let requisicao = await fetch(url, {
        method: "GET",
        mode: "cors"
    });
    let request = await requisicao.text();
    let horarios = JSON.parse(request);
    restricoesProfessor = horarios;
    return horarios;
}

function montarHorariosNaTabela(horarios) {
    let tabela = document.getElementById("tabela");
    for (let i=0; i<horarios.length; i++) {
        let tr = document.createElement("tr");
        tr.innerHTML = `
            <td hidden>${horarios[i].codigo}</td>
            <td>${horarios[i].descricao}</td>
            <td>${horarios[i].inicio}</td>
            <td>${horarios[i].fim}</td>
            <td><button type="button" class="button red" onclick="deletarHorarioProfessor(${horarios[i].codigo})">Excluir</button></td>
        `;
        tabela.appendChild(tr);
    }
}

async function deletarHorarioProfessor(codigoHorario) {
    console.log(codigoHorario);
    if (confirm("Deseja realmente exluir esse registro?")) {
        ativar_loading();
        let select = document.getElementById("selectProfessor");
        let url = `http://localhost:8080/professores/deleteHorario?codigoProfessor=${select.value}&codigoHorario=${codigoHorario}`;
        await fetch(url, {
            mode: "cors",
            method: "DELETE"
        }).then(async result => {
            if (result.status === 200) {
                let trs = document.querySelectorAll("#tabela > tr");
                trs.forEach(tr => {
                    if (parseInt(tr.children[0].textContent) === codigoHorario) {
                        tr.remove();
                    }
                })
                for (let i=0; i<restricoesProfessor.length; i++) {
                    if (restricoesProfessor[i].codigo === codigoHorario) {
                        restricoesProfessor.splice(i, 1);
                    }
                }
            }
        }).catch(function (err) {
            console.log(err);
        })
        retirar_loading();
    }
}

async function updateTabela(){
    ativar_loading();
    deletarTabela();
    deletarInfoProfessor();
    criarTabela();
    let horarios = await carregarHorariosRestricaoProfessor(document.getElementById("selectProfessor").value);
    addBotaoAddDHorario();
    addInfoProfessor();
    montarHorariosNaTabela(horarios);
    retirar_loading();
}

function addInfoProfessor() {
    let label = document.createElement("label");
    let select = document.getElementById("selectProfessor");
    let indiceProfessor = select.selectedIndex;
    let professorSelecionado = select.options[indiceProfessor];
    let nomeProfessor = professorSelecionado.label;
    label.textContent = `Restrição de horarios associados ao professor ${nomeProfessor}`;
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

function deletarTabela() {
    let tabela = document.getElementById("tabela");
    tabela.remove();
}

function criarTabela() {
    let main = document.getElementById("main");
    let table = document.createElement("table");
    table.className = "records";
    table.id = "tabela";
    table.innerHTML = `
        <tr>
            <th hidden>Codigo</th>
            <th>Descricao</th>
            <th>Início</th>
            <th>Fim</th>
            <th>Ação</th>
        </tr>
    `;
    main.appendChild(table);
}

async function montarProfessoresSelect() {
    let prof = await listarProfessor();
    montarSelect(prof);
}

function montarSelect(professores) {
    let select = document.getElementById("selectProfessor");
    for (let i=0; i<professores.length; i++) {
        let option = document.createElement("option");
        option.label = professores[i].nome;
        option.value = professores[i].codigo;
        select.appendChild(option);
    }
}

async function listarProfessor() {
    let url = "http://localhost:8080/professores";
    let request = await fetch(url, {
        mode: "cors",
        method: "GET"
    });
    let response = await request.text();
    let professores = JSON.parse(response);
    return professores;
}

function limparCampos() {
    ativar_loading();
    removerBotaoAddHorario();
    deletarInfoProfessor();
    deletarTabela();
    criarTabela();
    retirar_loading();
}

function addBotaoAddDHorario() {
    if (document.getElementById("btnAddHorario") == null) {
        let tabela = document.getElementById("tabela");
        let botao = document.createElement("button");
        botao.type = "button";
        botao.className = "button blue mobile";
        botao.id = "btnAddHorario";
        botao.textContent = "Adicionar horario";
        tabela.parentElement.insertBefore(botao, tabela);
        botao.addEventListener('click', abrirBtnAddHorario);
    }
}

function abrirBtnAddHorario() {
    addElementosNoModal();
    openModal();
}

async function addElementosNoModal() {
    let horarios = await listarTodosOsHorarios();
    console.log(horarios);
    addElementosNoCheckbox(horarios);
    marcarDisciplinasDoProfessor();
}

function addElementosNoCheckbox(horarios) {
    let formModal = document.getElementById("formModal");
    for (let i=0; i<horarios.length;i++) {
        let checkbox = document.createElement("input");
        let label = document.createElement("label");
        checkbox.id = horarios[i].codigo;
        checkbox.type = "checkbox";
        checkbox.name = "restricoes";
        label.htmlFor = horarios[i].codigo;
        label.textContent = horarios[i].descricao;
        formModal.appendChild(checkbox);
        formModal.appendChild(label);
    }
}

function marcarDisciplinasDoProfessor() {
    let checkboxes = document.querySelectorAll("#formModal > input[type='checkbox']");
    checkboxes.forEach(checks => {
        restricoesProfessor.forEach((check) => {
            if (parseInt(checks.id) === check.codigo) {
                checks.checked = true;
            }
        })
    })
}

async function salvarRestricaoHorario() {
    let marcados = document.querySelectorAll("#formModal > input[type=checkbox]:checked");
    let codigoHorarios = [];
    marcados.forEach(elemento => {
        codigoHorarios.push(parseInt(elemento.id));
    })
    let codigoProfessor = document.getElementById("selectProfessor");
    let url = `http://localhost:8080/professores/horarios?codigoHorarios=${codigoHorarios}&codigoProfessor=${codigoProfessor.value}`;
    let request = await fetch(url, {
        method: "POST",
        mode: "no-cors"
    })
}

async function salvarHorarioProfessor() {
    ativar_loading();
    await salvarRestricaoHorario();
    await closeModal();
    retirar_loading();
}

async function listarTodosOsHorarios() {
    let url = `http://localhost:8080/horarios`;
    let request = await fetch(url, {
        mode: "cors",
        method: "GET"
    })
    if (request.status === 200) {
        let response = await request.text();
        let horarios = JSON.parse(response);
        return horarios;
    }
}

function removerBotaoAddHorario() {
    let botao = document.getElementById("btnAddHorario");
    botao.remove();
}

function removerElementosModal() {
    let form = document.getElementById("formModal");
    form.innerHTML = "";
}