package br.com.seubarriga.tests;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import br.com.seubarriga.core.BaseTest;

public class BarrigaTests extends BaseTest {

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
}
