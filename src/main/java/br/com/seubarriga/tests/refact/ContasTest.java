package br.com.seubarriga.tests.refact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import br.com.seubarriga.core.BaseTest;
import br.com.seubarriga.core.utils.BarrigaUtils;

public class ContasTest extends BaseTest {
	
	@Test
	public void deveIncluirContaComSucesso() {
		//Cadastro de conta
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", "Conta inserida");
		
		given()
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		Integer contaId = BarrigaUtils.getIdContaPeloNome("Conta para alterar");
		
		//Atualização da conta
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", "Conta alterada");
		
			
		given()
			.body(conta)
			.pathParam("id", contaId)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("id", is(contaId))
			.body("nome", is("Conta alterada"))
		;	
	}
	
	@Test
	public void naoDeveIncluirContaComNomeRepetido() {
		Map<String, String> conta = new HashMap<>();
		conta.put("nome", "Conta mesmo nome");
		
		given()
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}	
}
