package br.com.seubarriga.tests.refact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

import br.com.seubarriga.core.BaseTest;
import br.com.seubarriga.core.domain.Transacao;
import br.com.seubarriga.core.utils.BarrigaUtils;
import br.com.seubarriga.core.utils.DateUtils;

public class MovimentacaoTest extends BaseTest {
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Transacao transacao = BarrigaUtils.getTransacaoValida();
		
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
		Transacao transacao = BarrigaUtils.getTransacaoValida();	
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
		Integer contaId = BarrigaUtils.getIdContaPeloNome("Conta com movimentacao");		
		
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
		Integer movimentacaoId = BarrigaUtils.getIdMovimentacaoPelaDescricao("Movimentacao para exclusao");
		
		given()
			.pathParam("id", movimentacaoId)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
}
