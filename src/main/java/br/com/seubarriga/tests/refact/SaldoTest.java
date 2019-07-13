package br.com.seubarriga.tests.refact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.com.seubarriga.core.BaseTest;
import io.restassured.RestAssured;

public class SaldoTest extends BaseTest {

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
	public void deveCalcularSaldoDasContas() {
		Integer contaId = this.getIdContaPeloNome("Conta para saldo");
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id ==" + contaId + "}.saldo", is("534.00"))
		;
	}
	
	private Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
}
