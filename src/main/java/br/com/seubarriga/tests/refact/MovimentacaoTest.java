package br.com.seubarriga.tests.refact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.com.seubarriga.core.BaseTest;
import br.com.seubarriga.core.domain.Transacao;
import br.com.seubarriga.core.domain.enums.TipoMovimentacao;
import br.com.seubarriga.core.utils.DateUtils;
import io.restassured.RestAssured;

public class MovimentacaoTest extends BaseTest {

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
		
		//Reset da aplicacao
		RestAssured.get("/reset").then().statusCode(200);
	}
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Transacao transacao = this.getTransacaoValida();
		
		given()
			.body(transacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.body("descricao", is(transacao.getDescricao()))
			.body("envolvido", is(transacao.getEnvolvido()))
			.body("observacao", nullValue())
			.body("tipo", is(transacao.getTipo()))
			.body("status", is(transacao.isStatus()))
			.body("transferencia_id", nullValue())
		;
	}
	
	@Test 
	public void deveValidarCamposObrigatorios() {
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
	public void naoDeveCadastrarMovimentacaoFutura() {
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
	public void naoDeveRemoverContaComMovimentacao() {
		Integer contaId = this.getIdContaPeloNome("Conta com movimentacao");		
		
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
	public void deveRemoverMovimentacao() {
		Integer movimentacaoId = this.getIdMovimentacaoPelaDescricao("Movimentacao para exclusao");
		
		given()
			.pathParam("id", movimentacaoId)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	private Integer getIdMovimentacaoPelaDescricao(String descricao) {
		return RestAssured.get("/transacoes?descricao=" + descricao).then().extract().path("id[0]");
	}
	
	private Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
	
	private Transacao getTransacaoValida() {
		return new Transacao(this.getIdContaPeloNome("Conta para movimentacoes"), "Descrição da movimentação", 
				"Envolvido na movimentação", TipoMovimentacao.RECEITA, DateUtils.getDataDiferencaDias(-1), 
				DateUtils.getDataDiferencaDias(5), 100f, true);
	}
}
