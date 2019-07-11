package br.com.seubarriga.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.com.seubarriga.core.BaseTest;

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
}
