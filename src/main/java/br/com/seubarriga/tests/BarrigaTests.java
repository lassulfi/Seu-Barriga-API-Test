package br.com.seubarriga.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.com.seubarriga.core.BaseTest;
import br.com.seubarriga.core.domain.Transacao;
import br.com.seubarriga.core.domain.enums.TipoMovimentacao;

public class BarrigaTests extends BaseTest {
	
	private String token;
	
	@Before
	public void login() {
		//login na api
		//receber token
		Map<String, String> login = new HashMap<>();
		login.put("email", "luis@daniel");
		login.put("senha", "123456");
		
		this.token = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
	}

	@Test
	public void naoDeveAcessarSemToken() {
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	@Test
	public void deveIncluirContaComSucesso() {
		//Cadastro de conta
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", "Conta para testes");
		
		given()
			.body(conta)
			.header("Authorization", "JWT " + token)
		.when()
			.post("/contas")
		.then()
			.statusCode(201);
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		//Atualização da conta
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", "Conta alterada para testes");
		
			
		given()
			.body(conta)
			.header("Authorization", "JWT " + token)
		.when()
			.put("/contas/22091")
		.then()
			.statusCode(200)
			.body("id", is(22091))
			.body("nome", is("Conta alterada para testes"))
		;
		
	}
	
	@Test
	public void naoDeveIncluirContaComNomeRepetido() {
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", "Conta alterada para testes");
		
		given()
			.body(conta)
			.header("Authorization", "JWT " + token)
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Transacao transacao = this.getTransacaoValida();
		
		given()
			.header("Authorization", "JWT " + token)
			.body(transacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}
	
	@Test 
	public void deveValidarCamposObrigatorios() {
		Transacao transacao = new Transacao();
		
		given()
			.header("Authorization", "JWT " + token)
			.body(transacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(7))
			.body("param", hasItems("data_transacao", "data_pagamento", "descricao", "envolvido", "valor", "conta_id"))
			.body("msg", hasItems("Data da Movimentação é obrigatório", "Data do pagamento é obrigatório", 
					"Descrição é obrigatório", "Interessado é obrigatório", "Valor é obrigatório", "Valor deve ser um número", 
					"Conta é obrigatório"))
		;
	}
	
	@Test
	public void naoDeveCadastrarMovimentacaoFutura() {
		Transacao transacao = this.getTransacaoValida();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		Calendar calendar = Calendar.getInstance();
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, currentDay + 1);
		
		transacao.setData_transacao(sdf.format(calendar.getTime()));
		
		given()
			.header("Authorization", "JWT " + token)
			.body(transacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("param", hasItem("data_transacao"))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
			.body("value",hasItem(transacao.getData_transacao()))
		;
	}
	
	@Test
	public void naoDeveRemoverContaComMovimentacao() {
		
		given()
			.header("Authorization", "JWT " + token)
		.when()
			.delete("/contas/22091")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void deveCalcularSaldoDasContas() {
		given()
			.header("Authorization", "JWT " + token)
		.when()
			.get("/saldo")
		.then()
			.log().all()
			.statusCode(200)
			.body("find{it.conta_id == 22091}.saldo", is("650"))
		;
	}
	
	private Transacao getTransacaoValida() {
		return new Transacao(22091, "Descrição da movimentação", 
				"Envolvido na movimentação", TipoMovimentacao.RECEITA, "01/01/2000", "10/05/2010", 100f, true);
	}
}
