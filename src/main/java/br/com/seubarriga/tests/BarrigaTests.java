package br.com.seubarriga.tests;

import static io.restassured.RestAssured.given;

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
}
