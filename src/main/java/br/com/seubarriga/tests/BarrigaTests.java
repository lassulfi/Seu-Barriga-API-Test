package br.com.seubarriga.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.com.seubarriga.core.BaseTest;
import br.com.seubarriga.core.domain.Transacao;
import br.com.seubarriga.core.domain.enums.TipoMovimentacao;
import br.com.seubarriga.core.utils.DateUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTests extends BaseTest {
	
	private static String contaName = "Conta " + System.nanoTime();
	private static Integer contaId;
	private static Integer movimentacaoId;
	
	@BeforeClass
	public static void login() {
		//login na api
		//receber token
		Map<String, String> login = new HashMap<>();
		login.put("email", "luis@daniel");
		login.put("senha", "123456");
		
		String token = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + token);
	}

	@Test
	public void t02_deveIncluirContaComSucesso() {
		//Cadastro de conta
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", contaName);
		
		contaId = given()
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t03_deveAlterarContaComSucesso() {
		//Atualização da conta
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", contaName + " alterada");
		
			
		given()
			.body(conta)
			.pathParam("id", contaId)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("id", is(contaId))
			.body("nome", is(contaName + " alterada"))
		;
		
	}
	
	@Test
	public void t04_naoDeveIncluirContaComNomeRepetido() {
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", contaName + " alterada");
		
		given()
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void t05_deveInserirMovimentacaoComSucesso() {
		Transacao transacao = this.getTransacaoValida();
		
		movimentacaoId = given()
			.body(transacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test 
	public void t06_deveValidarCamposObrigatorios() {
		Transacao transacao = new Transacao();
		
		given()
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
	public void t07_naoDeveCadastrarMovimentacaoFutura() {
		Transacao transacao = this.getTransacaoValida();	
		transacao.setData_transacao(DateUtils.getDataDiferencaDias(2));
		
		given()
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
	public void t08_naoDeveRemoverContaComMovimentacao() {
		
		given()
			.pathParam("id", contaId)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t09_deveCalcularSaldoDasContas() {
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id ==" + contaId + "}.saldo", is("100.00"))
		;
	}
	
	@Test
	public void t10_deveRemoverMovimentacao() {
		given()
			.pathParam("id", movimentacaoId)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
			;
	}
	
	@Test
	public void t11_naoDeveAcessarSemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	
	private Transacao getTransacaoValida() {
		return new Transacao(contaId, "Descrição da movimentação", 
				"Envolvido na movimentação", TipoMovimentacao.RECEITA, DateUtils.getDataDiferencaDias(-1), DateUtils.getDataDiferencaDias(5), 100f, true);
	}
}
