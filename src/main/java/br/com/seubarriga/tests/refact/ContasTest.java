package br.com.seubarriga.tests.refact;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;

import br.com.seubarriga.core.BaseTest;
import io.restassured.RestAssured;

public class ContasTest extends BaseTest {

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
}
